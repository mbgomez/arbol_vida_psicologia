package com.netah.hakkam.numyah.mind.di

import com.netah.hakkam.numyah.mind.data.datasource.remote.PostService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

private const val API_TIMEOUT: Long = 60
private const val POST_URL: String = "https://jsonplaceholder.typicode.com/"

object APIFactory {

    fun retrofitPosts(): PostService {
        val builder = OkHttpClient.Builder()
        builder.readTimeout(API_TIMEOUT, TimeUnit.SECONDS)
        builder.writeTimeout(API_TIMEOUT, TimeUnit.SECONDS)
        builder.connectTimeout(API_TIMEOUT, TimeUnit.SECONDS)
        val requestInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(newRequest)
        }
        builder.addInterceptor(requestInterceptor)

        val logInterceptor = HttpLoggingInterceptor {
            Timber.tag("OkHttp").i(it)
        }
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(logInterceptor)


        return Retrofit.Builder()
            .baseUrl(POST_URL)
            .client(builder.build())
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(PostService::class.java)
    }

    val POST_SERVICE: PostService = retrofitPosts()
}

class PostAPIFactory1 {


    fun retrofitPosts(): PostService {
        val builder = OkHttpClient.Builder()
        builder.readTimeout(API_TIMEOUT, TimeUnit.SECONDS)
        builder.writeTimeout(API_TIMEOUT, TimeUnit.SECONDS)
        builder.connectTimeout(API_TIMEOUT, TimeUnit.SECONDS)
        val requestInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(newRequest)
        }
        builder.addInterceptor(requestInterceptor)

        val logInterceptor = HttpLoggingInterceptor {
            Timber.tag("OkHttp").i(it)
        }
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(logInterceptor)


        return Retrofit.Builder()
            .baseUrl(POST_URL)
            .client(builder.build())
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(PostService::class.java)
    }

    val POST_SERVICE: PostService = retrofitPosts()

}