package webtech.com.courierdriver.communication


import android.annotation.SuppressLint
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import webtech.com.courierdriver.constants.ApiConstant
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/*
Created by ​
Hannure Abdulrahim
WebTech Co.
Swissbell Plaza Kuwait Hotel,
9th Floor, Office No.- 915, Kuwait City, KUWAIT

on 1/28/2018.
 */

object RetrofitClient {

    private val TIME_OUT_SESSION: Long = 30000 // 30 Seconds

    fun getClient(): ApiPostService {
        val okHttpClient = getUnsafeOkHttpClient() // SSL Safe
        val gson = GsonBuilder().setLenient().create()

        if (ApiConstant.retrofit == null) {
            ApiConstant.retrofit = Retrofit.Builder()
                    .baseUrl(ApiConstant.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
        }
        val apiFuntions = ApiConstant.retrofit!!.create(ApiPostService::class.java)
        return apiFuntions
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient {
        return try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(@SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            })

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { hostname, session -> true }

            builder.readTimeout(TIME_OUT_SESSION, TimeUnit.MILLISECONDS)
            builder.writeTimeout(TIME_OUT_SESSION, TimeUnit.MILLISECONDS)
            builder.connectTimeout(TIME_OUT_SESSION, TimeUnit.MILLISECONDS)

            if (webtech.com.courierdriver.BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                builder.addInterceptor(loggingInterceptor)
            }
            builder.build()

        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

}