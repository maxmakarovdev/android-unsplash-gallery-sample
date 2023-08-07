package com.maxmakarov.base.gallery.data

import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.maxmakarov.base.gallery.api.UnsplashApi
import com.maxmakarov.base.gallery.db.PhotoDatabase
import com.maxmakarov.base.gallery.model.UnsplashPhoto
import com.maxmakarov.core.Config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class PhotosRepository(private val api: UnsplashApi, private val database: PhotoDatabase) {
    private val defaultConfig = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false)

    private fun getPagerFlow(dataSource: PagingSource<Int, UnsplashPhoto>): Flow<PagingData<UnsplashPhoto>> {
        return Pager(config = defaultConfig, pagingSourceFactory = { dataSource }).flow
    }

    fun loadPhotosStream() =
        getPagerFlow(LoadPhotoPagingSource(api))
            .flowOn(Dispatchers.Default)

    fun searchPhotosStream(query: String) =
        getPagerFlow(SearchPhotoPagingSource(api, query))
            .flowOn(Dispatchers.Default)

    fun getFavorites() =
        getPagerFlow(database.favoritesDao().getFavorites())
            .flowOn(Dispatchers.Default)

    fun checkPhotoIsAdded(photo: UnsplashPhoto) =
        database.favoritesDao()
            .checkPhotoIsAdded(photo.id)
            .flowOn(Dispatchers.Default)

    suspend fun addToFavorites(photo: UnsplashPhoto) {
        withContext(Dispatchers.Default) {
            database.favoritesDao().addToFavorites(photo.copy(savedTimestamp = System.currentTimeMillis()))
        }
    }

    suspend fun removeFromFavorites(photo: UnsplashPhoto) {
        withContext(Dispatchers.Default) {
            database.favoritesDao().removeFromFavorites(photo.id)
        }
    }

    suspend fun trackDownload(url: String) {
        withContext(Dispatchers.Default) {
            Uri.parse(url).buildUpon().run {
                appendQueryParameter("client_id", Config.accessKey)
                build().toString()
            }.also { downloadUrl ->
                api.trackDownload(downloadUrl)
            }
        }
    }

    companion object {
        const val PAGE_SIZE = 20 //max limit is 30
        fun create(database: PhotoDatabase): PhotosRepository {
            return PhotosRepository(UnsplashApi.create(), database)
        }

        //fixme: Temporary workaround of passing the argument to FullscreenImageFragment
        //currently Navigation doesn't support Parcelable as a deeplink argument as well as
        //it can't pass Json-serialized object properly because of symbols in the blur_hash field
        var photoToView: UnsplashPhoto? = null
    }
}