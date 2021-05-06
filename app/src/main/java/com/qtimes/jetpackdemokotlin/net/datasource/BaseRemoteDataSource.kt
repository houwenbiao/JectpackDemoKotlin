/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 15:29
 * Description:
 */

package com.qtimes.jetpackdemokotlin.net.datasource

import android.util.LruCache
import android.widget.Toast
import androidx.paging.PagingSource
import com.qtimes.jetpackdemokotlin.BuildConfig
import com.qtimes.jetpackdemokotlin.R
import com.qtimes.jetpackdemokotlin.common.MainApplication
import com.qtimes.jetpackdemokotlin.net.HttpConfig
import com.qtimes.jetpackdemokotlin.net.HttpsUtil
import com.qtimes.jetpackdemokotlin.net.base.*
import com.qtimes.jetpackdemokotlin.net.intercept.FilterInterceptor
import com.qtimes.jetpackdemokotlin.net.intercept.HeaderInterceptor
import com.qtimes.jetpackdemokotlin.utils.LogUtil
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection


abstract class BaseRemoteDataSource<T : Any, P : Any>(
    private val iUIActionEvent: IUIActionEvent?,
    private val apiServiceClass: Class<T>
) : ICoroutineEvent, PagingSource<Int, P>() {
    companion object {
        //Api service 缓存
        private val apiServiceCache = LruCache<String, Any>(30)

        //retrofit 缓存
        private val retrofitCache = LruCache<String, Retrofit>(3)

        //默认的OKHttpClient
        private val defaultOkHttpClient by lazy {

            val builder = OkHttpClient.Builder()
                .readTimeout(HttpConfig.READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(HttpConfig.WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(HttpConfig.CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(FilterInterceptor())
                .addInterceptor(HeaderInterceptor())
            if (BuildConfig.DEBUG) {
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                builder.addInterceptor(httpLoggingInterceptor)
            }

            //https域名校验
            builder.hostnameVerifier { hostname, session ->
                /*域名校验---首先检验是否是我们信任的域名然后再检验域名和服务端传输过来的证书里的域名(CN)是否一致*/
                LogUtil.d("hostname: $hostname")
                var trust: Boolean = verifyHostname(hostname, HttpConfig.HOST_REGEX)
                if (trust) {
                    val hv = HttpsURLConnection.getDefaultHostnameVerifier()
                    trust = hv.verify(hostname, session)
                }
                trust
            }

            var qtimesIs: InputStream? = null
            qtimesIs = MainApplication.context.resources.openRawResource(R.raw.server)
            val sslParams: HttpsUtil.Companion.SSLParams =
                HttpsUtil.getSslSocketFactory(arrayOf(qtimesIs!!), null, null)
            builder.sslSocketFactory(sslParams.socketFactory!!, sslParams.trustManager!!)
            builder.build()
        }


        private fun verifyHostname(hostname: String, pattern: String): Boolean {
            var hostname: String? = hostname
            var pattern: String? = pattern
            if (hostname == null || hostname.isEmpty() || hostname.startsWith(".")
                || hostname.endsWith("..")
            ) {
                // Invalid domain name
                return false
            }
            if (pattern == null || pattern.isEmpty() || pattern.startsWith(".")
                || pattern.endsWith("..")
            ) {
                // Invalid pattern/domain name
                return false
            }

            // Normalize hostname and pattern by turning them into absolute domain names if they are not
            // yet absolute. This is needed because server certificates do not normally contain absolute
            // names or patterns, but they should be treated as absolute. At the same time, any hostname
            // presented to this method should also be treated as absolute for the purposes of matching
            // to the server certificate.
            //   www.android.com  matches www.android.com
            //   www.android.com  matches www.android.com.
            //   www.android.com. matches www.android.com.
            //   www.android.com. matches www.android.com
            if (!hostname.endsWith(".")) {
                hostname += '.'
            }
            if (!pattern.endsWith(".")) {
                pattern += '.'
            }
            // hostname and pattern are now absolute domain names.
            pattern = pattern.toLowerCase(Locale.US)
            // hostname and pattern are now in lower case -- domain names are case-insensitive.
            if (!pattern.contains("*")) {
                // Not a wildcard pattern -- hostname and pattern must match exactly.
                return hostname == pattern
            }
            // Wildcard pattern

            // WILDCARD PATTERN RULES:
            // 1. Asterisk (*) is only permitted in the left-most domain name label and must be the
            //    only character in that label (i.e., must match the whole left-most label).
            //    For example, *.example.com is permitted, while *a.example.com, a*.example.com,
            //    a*b.example.com, a.*.example.com are not permitted.
            // 2. Asterisk (*) cannot match across domain name labels.
            //    For example, *.example.com matches test.example.com but does not match
            //    sub.test.example.com.
            // 3. Wildcard patterns for single-label domain names are not permitted.
            if (!pattern.startsWith("*.") || pattern.indexOf('*', 1) != -1) {
                // Asterisk (*) is only permitted in the left-most domain name label and must be the only
                // character in that label
                return false
            }

            // Optimization: check whether hostname is too short to match the pattern. hostName must be at
            // least as long as the pattern because asterisk must match the whole left-most label and
            // hostname starts with a non-empty label. Thus, asterisk has to match one or more characters.
            if (hostname.length < pattern.length) {
                // hostname too short to match the pattern.
                return false
            }
            if ("*." == pattern) {
                // Wildcard pattern for single-label domain name -- not permitted.
                return false
            }

            // hostname must end with the region of pattern following the asterisk.
            val suffix = pattern.substring(1)
            if (!hostname.endsWith(suffix)) {
                // hostname does not end with the suffix
                return false
            }

            // Check that asterisk did not match across domain name labels.
            val suffixStartIndexInHostname = hostname.length - suffix.length
            return !(suffixStartIndexInHostname > 0
                    && hostname.lastIndexOf('.', suffixStartIndexInHostname - 1) != -1)

            // hostname matches pattern
        }

        /**
         * 构建默认的 Retrofit
         */
        private fun createDefaultRetrofit(baseUrl: String): Retrofit {
            return Retrofit.Builder()
                .client(defaultOkHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }


    /**
     * 和生命周期绑定的协程作用域
     */
    override val lifecycleSupportedScope: CoroutineScope
        get() = iUIActionEvent?.lifecycleSupportedScope ?: GlobalScope

    /**
     * 由子类实现此字段以便获取 baseUrl
     */
    protected abstract val baseUrl: String

    /**
     * 允许子类自己来实现创建 Retrofit 的逻辑
     * 外部无需缓存 Retrofit 实例，ReactiveHttp 内部已做好缓存处理
     * 但外部需要自己判断是否需要对 OKHttpClient 进行缓存
     * @param baseUrl
     */
    protected open fun createRetrofit(baseUrl: String): Retrofit {
        return createDefaultRetrofit(baseUrl)
    }

    protected open fun generateBaseUrl(baseUrl: String): String {
        if (baseUrl.isNotBlank()) {
            return baseUrl
        }
        return this.baseUrl
    }

    fun getApiService(baseUrl: String = ""): T {
        return getApiService(generateBaseUrl(baseUrl), apiServiceClass)
    }

    private fun getApiService(baseUrl: String, apiServiceClazz: Class<T>): T {
        val key = baseUrl + apiServiceClazz.canonicalName
        val get = apiServiceCache.get(key)?.let {
            it as? T
        }
        if (get != null) {
            return get
        }
        val retrofit = retrofitCache.get(baseUrl) ?: (createRetrofit(baseUrl).apply {
            retrofitCache.put(baseUrl, this)
        })
        val apiService = retrofit.create(apiServiceClazz)
        apiServiceCache.put(key, apiService)
        return apiService
    }

    protected fun handleException(throwable: Throwable, callback: BaseRequestCallback?) {
        if (callback == null) {
            return
        }
        if (throwable is CancellationException) {
            callback.onCancelled?.invoke()
            return
        }
        val exception = generateBaseExceptionReal(throwable)
        if (exceptionHandle(exception)) {
            callback.onFailed?.invoke(exception)
            if (callback.onFailToast()) {
                val error = exceptionFormat(exception)
                if (error.isNotBlank()) {
                    showToast(error)
                }
            }
        }
    }

    internal fun generateBaseExceptionReal(throwable: Throwable): BaseHttpException {
        LogUtil.e(throwable)
        return generateBaseException(throwable).apply {
            exceptionRecord(this)
        }
    }

    /**
     * 如果外部想要对 Throwable 进行特殊处理，则可以重写此方法，用于改变 Exception 类型
     * 例如，在 token 失效时接口一般是会返回特定一个 httpCode 用于表明移动端需要去更新 token 了
     * 此时外部就可以实现一个 BaseException 的子类 TokenInvalidException 并在此处返回
     * 从而做到接口异常原因强提醒的效果，而不用去纠结 httpCode 到底是多少
     */
    protected open fun generateBaseException(throwable: Throwable): BaseHttpException {
        return if (throwable is BaseHttpException) {
            throwable
        } else {
            LocalBadException(throwable)
        }
    }

    /**
     * 用于由外部中转控制当抛出异常时是否走 onFail 回调，当返回 true 时则回调，否则不回调
     * @param httpException
     */
    protected open fun exceptionHandle(httpException: BaseHttpException): Boolean {
        return true
    }

    /**
     * 用于将网络请求过程中的异常反馈给外部，以便记录
     * @param throwable
     */
    protected open fun exceptionRecord(throwable: Throwable) {

    }

    /**
     * 用于对 BaseException 进行格式化，以便在请求失败时 Toast 提示错误信息
     * @param httpException
     */
    protected open fun exceptionFormat(httpException: BaseHttpException): String {
        return when (httpException.realException) {
            null -> {
                httpException.errorMessage
            }
            is ConnectException, is SocketTimeoutException, is UnknownHostException -> {
                "连接超时，请检查您的网络设置"
            }
            else -> {
                "请求过程抛出异常：" + httpException.errorMessage
            }
        }
    }

    protected fun showLoading(job: Job?) {
        LogUtil.d("showloading, $iUIActionEvent")
        iUIActionEvent?.showLoading(job)
    }

    protected fun dismissLoading() {
        iUIActionEvent?.dismissLoading()
    }

    private fun showToast(msg: String) {
        Toast.makeText(MainApplication.context, msg, Toast.LENGTH_LONG).show()
    }
}