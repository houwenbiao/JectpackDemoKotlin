/**
 * Created with JackHou
 * Date: 2021/4/26
 * Time: 14:00
 * Description:
 */

package com.qtimes.jetpackdemokotlin.net

import com.qtimes.jetpackdemokotlin.utils.LogUtil
import java.io.IOException
import java.io.InputStream
import java.security.*
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*


class HttpsUtil {

    companion object {
        class SSLParams {
            var socketFactory: SSLSocketFactory? = null
            var trustManager: X509TrustManager? = null
        }

        private class UnSafeHostnameVerifier : HostnameVerifier {
            override fun verify(hostname: String, session: SSLSession): Boolean {
                return true
            }
        }

        private class UnSafeTrustManager : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }


        fun getSslSocketFactory(
            certificates: Array<InputStream>,
            bksFile: InputStream?,
            password: String?
        ): SSLParams {
            val sslParams = SSLParams()
            return try {
                val trustManagers = prepareTrustManager(*certificates)
                val keyManagers = prepareKeyManager(bksFile, password)
                val sslContext = SSLContext.getInstance("TLS")
                var trustManager: X509TrustManager? = null
                trustManager = if (trustManagers != null) {
                    MyTrustManager(chooseTrustManager(trustManagers))
                } else {
                    UnSafeTrustManager()
                }
                sslContext.init(keyManagers, arrayOf<TrustManager>(trustManager), null)
                sslParams.socketFactory = sslContext.socketFactory
                sslParams.trustManager = trustManager
                sslParams
            } catch (e: NoSuchAlgorithmException) {
                throw AssertionError(e)
            } catch (e: KeyManagementException) {
                throw AssertionError(e)
            } catch (e: KeyStoreException) {
                throw AssertionError(e)
            }
        }

        private fun prepareTrustManager(vararg certificates: InputStream): Array<TrustManager>? {
            if (certificates == null || certificates.isEmpty()) {
                return null
            }
            try {
                val certFactory = CertificateFactory.getInstance("X.509")
                val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
                keyStore.load(null)
                var index = 0
                for (cert in certificates) {
                    val certAlias = Integer.toString(index++)
                    keyStore.setCertificateEntry(certAlias, certFactory.generateCertificate(cert))
                    try {
                        cert?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                var trustManagerFactory: TrustManagerFactory? = null
                trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                trustManagerFactory.init(keyStore)
                return trustManagerFactory.trustManagers
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: CertificateException) {
                e.printStackTrace()
            } catch (e: KeyStoreException) {
                e.printStackTrace()
                LogUtil.i("KeyStoreException: $e")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        private fun prepareKeyManager(
            bksFile: InputStream?,
            password: String?
        ): Array<KeyManager>? {
            try {
                if (bksFile == null || password == null) {
                    return null
                }
                val clientKeyStore = KeyStore.getInstance(KeyStore.getDefaultType())
                clientKeyStore.load(bksFile, password.toCharArray())
                val keyManagerFactory =
                    KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
                keyManagerFactory.init(clientKeyStore, password.toCharArray())
                return keyManagerFactory.keyManagers
            } catch (e: KeyStoreException) {
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: UnrecoverableKeyException) {
                e.printStackTrace()
            } catch (e: CertificateException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        private fun chooseTrustManager(trustManagers: Array<TrustManager>): X509TrustManager? {
            for (trustManager in trustManagers) {
                if (trustManager is X509TrustManager) {
                    return trustManager
                }
            }
            return null
        }

        private class MyTrustManager(var localTrustManager: X509TrustManager?) : X509TrustManager {
            private var defaultTrustManager: X509TrustManager?

            init {
                val var4 =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                var4.init(null as KeyStore?)
                defaultTrustManager = chooseTrustManager(var4.trustManagers)
            }


            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                LogUtil.d("checkClientTrusted")
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                try {
                    defaultTrustManager!!.checkServerTrusted(chain, authType)
                } catch (ce: CertificateException) {
                    localTrustManager?.checkServerTrusted(chain, authType)
                }
            }

            override fun getAcceptedIssuers(): Array<X509Certificate?> {
                return arrayOfNulls(0)
            }
        }

    }
}