package com.maxmakarov.gallery.data

import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.maxmakarov.gallery.api.RetrofitFactory
import com.maxmakarov.gallery.api.UnsplashService
import com.maxmakarov.gallery.core.Config
import com.maxmakarov.gallery.db.PhotoDatabase
import com.maxmakarov.gallery.model.UnsplashPhoto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

class PhotosRepository(
    private val service: UnsplashService,
    private val database: PhotoDatabase
) {
    private val defaultConfig = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false)

    fun loadPhotosStream(): Flow<PagingData<UnsplashPhoto>> {
        return Pager(
            config = defaultConfig,
            pagingSourceFactory = { LoadPhotoPagingSource(service) }
        ).flow
    }

    fun searchPhotosStream(query: String): Flow<PagingData<UnsplashPhoto>> {
        return Pager(
            config = defaultConfig,
            pagingSourceFactory = { SearchPhotoDataSource(service, query) }
        ).flow
    }

    fun getFavorites(): Flow<PagingData<UnsplashPhoto>> {
        return Pager(
            config = defaultConfig,
            pagingSourceFactory = { database.favoritesDao().getFavorites() }
        ).flow
    }

    fun checkPhotoIsAdded(photo: UnsplashPhoto): Flow<Boolean> {
        return flow { emit(database.favoritesDao().checkPhotoIsAdded(photo.id)) }
    }

    fun addToFavorites(photo: UnsplashPhoto): Flow<Any> {
        return flow { emit(database.favoritesDao().addToFavorites(photo)) }
    }

    fun removeFromFavorites(photo: UnsplashPhoto): Flow<Any> {
        return flow { emit(database.favoritesDao().removeFromFavorites(photo.id)) }
    }

    fun trackDownload(url: String?): Flow<Any> {
        if (url != null) {
            val uriBuilder = Uri.parse(url).buildUpon()
            uriBuilder.appendQueryParameter("client_id", Config.accessKey)
            val downloadUrl = uriBuilder.build().toString()
            return flow { emit(service.trackDownload(downloadUrl)) }
        }
        return emptyFlow()
    }

    companion object {
        const val PAGE_SIZE = 20 //max limit is 30
        fun create(database: PhotoDatabase): PhotosRepository {
            return PhotosRepository(RetrofitFactory.createNetworkEndpoints(), database)
        }
    }
}