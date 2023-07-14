package com.maxmakarov.gallery.api

import com.maxmakarov.gallery.core.Config
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Factory for manual DI to avoid sticking to a specific DI library.
 */
//todo maybe Hilt will be implemented
object RetrofitFactory {

    private const val CONTENT_TYPE = "Content-Type"
    private const val APPLICATION_JSON = "application/json"
    private const val ACCEPT_VERSION = "Accept-Version"

    private fun createHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                .addHeader(ACCEPT_VERSION, "v1")
                .build()
            chain.proceed(newRequest)
        }
    }

    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply { level = Level.BODY }
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

    private fun createRetrofitBuilder(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(UnsplashService.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(createHttpClient())
            .build()
    }

    fun createNetworkEndpoints(): UnsplashService =
        createRetrofitBuilder().create(UnsplashService::class.java)
}