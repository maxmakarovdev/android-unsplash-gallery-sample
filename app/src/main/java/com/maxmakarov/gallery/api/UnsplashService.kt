package com.maxmakarov.gallery.api

import com.maxmakarov.gallery.core.Config
import com.maxmakarov.gallery.model.UnsplashPhoto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface UnsplashService {

    @GET("collections/317099/photos")
    suspend fun loadPhotos(
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int,
        @Query("client_id") clientId: String = Config.accessKey
    ): List<UnsplashPhoto>

    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int,
        @Query("client_id") clientId: String = Config.accessKey
    ): SearchPhotosResponse

    @GET
    suspend fun trackDownload(@Url url: String): Response<Any>

    companion object {
        const val BASE_URL = "https://api.unsplash.com/"
    }
}