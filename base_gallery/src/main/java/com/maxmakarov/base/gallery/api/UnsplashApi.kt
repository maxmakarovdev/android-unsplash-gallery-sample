package com.maxmakarov.base.gallery.api

import com.maxmakarov.base.gallery.model.UnsplashImage
import com.maxmakarov.core.RetrofitFactory
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface UnsplashApi {

    @GET("collections/317099/photos")
    suspend fun loadImages(
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int
    ): List<UnsplashImage>

    @GET("search/photos")
    suspend fun searchImages(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int
    ): SearchImagesResponse

    @GET
    suspend fun trackDownload(@Url url: String): Response<Any>

    companion object {
        private const val BASE_URL = "https://api.unsplash.com/"
        fun create(): UnsplashApi =
            RetrofitFactory.createRetrofitBuilder(BASE_URL).create(UnsplashApi::class.java)
    }
}