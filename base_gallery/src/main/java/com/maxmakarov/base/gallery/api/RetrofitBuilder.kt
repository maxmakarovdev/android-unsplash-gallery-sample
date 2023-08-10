package com.maxmakarov.base.gallery.api

import com.maxmakarov.core.Config
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RetrofitBuilder {

    fun build(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(createHttpClient())
            .build()
    }

    private fun createHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader(HEADER_CONTENT_TYPE, "application/json")
                .addHeader(HEADER_ACCEPT_VERSION, "v1")
                .addHeader(HEADER_AUTHORIZATION, "Client-ID ${Config.accessKey}")
                .build()
            chain.proceed(newRequest)
        }
    }

    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    private fun createHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.addNetworkInterceptor(createHeaderInterceptor())
        if (Config.isLoggingEnabled) {
            builder.addNetworkInterceptor(createLoggingInterceptor())
        }
        val cacheSize = 10 * 1024 * 1024 // 10 MB of cache
        val cache = Cache(Config.application.cacheDir, cacheSize.toLong())
        builder.cache(cache)
        return builder.build()
    }

    companion object {
        private const val HEADER_CONTENT_TYPE = "Content-Type"
        private const val HEADER_ACCEPT_VERSION = "Accept-Version"
        private const val HEADER_AUTHORIZATION = "Authorization"
    }
}