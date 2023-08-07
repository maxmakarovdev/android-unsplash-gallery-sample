package com.maxmakarov.base.gallery.api

import com.maxmakarov.base.gallery.model.UnsplashPhoto
import com.maxmakarov.core.RetrofitFactory
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface UnsplashApi {

    @GET("collections/317099/photos")
    suspend fun loadPhotos(
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int
    ): List<UnsplashPhoto>

    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int
    ): SearchPhotosResponse

    @GET
    suspend fun trackDownload(@Url url: String): Response<Any>

    companion object {
        private const val BASE_URL = "https://api.unsplash.com/"
        fun create(): UnsplashApi =
            RetrofitFactory.createRetrofitBuilder(BASE_URL).create(UnsplashApi::class.java)
    }
}