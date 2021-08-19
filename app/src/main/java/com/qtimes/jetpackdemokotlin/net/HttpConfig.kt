/**
 * Created with JackHou
 * Date: 2021/3/17
 * Time: 13:54
 * Description:
 */

package com.qtimes.jetpackdemokotlin.net


class HttpConfig {
    companion object {
        const val READ_TIMEOUT: Long = 6000
        const val WRITE_TIMEOUT: Long = 6000
        const val CONNECT_TIMEOUT: Long = 6000

        const val KEY = "key"
        const val HTTP_REQUEST_TYPE_KEY = "requestType"

        /*天气请求相关配置*/
        const val BASE_URL_WEATHER = "https://restapi.amap.com/v3/"
        const val KEY_WEATHER = "fb0a1b0d89f3b93adca639f0a29dbf23"

        //新闻
        const val BASE_URL_NEWS = "http://v.juhe.cn/"
        const val KEY_NEWS = "c3f9d6c4c70559205cab02fb9f8d4a66"

        //Github
        const val BASE_URL_GITHUB = "https://api.github.com/"

        //Qtimes服务
        const val QTIMES_URL_API = "https://api.qtimes.cc:8080"
        const val QTIMES_URL_FILE = "http://file.qtimes.cc:8091"

        //域名校验
        const val HOST_REGEX = "*.qtimes.cc"

        //Janus
        const val JANUS_URL = "ws://192.168.4.4:8188/ws"
        const val JANUS_ICE_URL = "stun:192.168.4.4:3478"
//        const val JANUS_URL = "ws://192.168.0.6:8188/ws"
//        const val JANUS_ICE_URL = "stun:192.168.0.6:3478"
    }

    /**
     * 请求类型
     */
    interface RequestType {
        companion object {
            const val WEATHER = "weather"
            const val NEWS = "news"
            const val REPOSITORY = "repository"
        }
    }

    /**
     * http code
     */
    interface HttpCode {
        companion object {
            const val CODE_SUCCESS = 1
            const val CODE_UNKNOWN = -1
            const val CODE_TOKEN_INVALID = -2
            const val CODE_ACCOUNT_INVALID = -3
            const val CODE_PARAMETER_INVALID = -4
            const val CODE_CONNECTION_FAILED = -5
            const val CODE_FORBIDDEN = -6
            const val CODE_RESULT_INVALID = -7
        }
    }
}