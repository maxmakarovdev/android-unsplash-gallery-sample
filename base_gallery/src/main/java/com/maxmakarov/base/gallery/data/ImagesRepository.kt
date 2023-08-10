package com.maxmakarov.base.gallery.data

import androidx.paging.PagingData
import com.maxmakarov.base.gallery.model.UnsplashImage
import kotlinx.coroutines.flow.Flow

interface ImagesRepository {
    fun loadImagesStream(): Flow<PagingData<UnsplashImage>>
    fun searchImagesStream(query: String): Flow<PagingData<UnsplashImage>>

    fun getFavorites(): Flow<PagingData<UnsplashImage>>
    fun checkIsAddedToFavs(image: UnsplashImage): Flow<Boolean>
    suspend fun addToFavorites(image: UnsplashImage)
    suspend fun removeFromFavorites(image: UnsplashImage)

    suspend fun trackDownload(url: String)
}