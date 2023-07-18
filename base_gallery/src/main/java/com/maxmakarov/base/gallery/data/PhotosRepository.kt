package com.maxmakarov.base.gallery.data

import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.maxmakarov.base.gallery.api.UnsplashApi
import com.maxmakarov.base.gallery.db.PhotoDatabase
import com.maxmakarov.base.gallery.model.UnsplashPhoto
import com.maxmakarov.core.Config
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

class PhotosRepository(private val api: UnsplashApi, private val database: PhotoDatabase) {
    private val defaultConfig = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false)

    fun loadPhotosStream(): Flow<PagingData<UnsplashPhoto>> {
        return Pager(
            config = defaultConfig,
            pagingSourceFactory = { LoadPhotoPagingSource(api) }
        ).flow
    }

    fun searchPhotosStream(query: String): Flow<PagingData<UnsplashPhoto>> {
        return Pager(
            config = defaultConfig,
            pagingSourceFactory = { SearchPhotoDataSource(api, query) }
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
            return flow { emit(api.trackDownload(downloadUrl)) }
        }
        return emptyFlow()
    }

    companion object {
        const val PAGE_SIZE = 20 //max limit is 30
        fun create(database: PhotoDatabase): PhotosRepository {
            return PhotosRepository(UnsplashApi.create(), database)
        }

        //todo note: Temporary workaround of passing the argument to FullscreenImageFragment just to make it work
        //because now Navigation doesn't support Parcelable as a deeplink argument as well as
        // it can't pass Json-serialized object properly because of symbols in the blur_hash field
        var photoToView: UnsplashPhoto? = null
    }
}