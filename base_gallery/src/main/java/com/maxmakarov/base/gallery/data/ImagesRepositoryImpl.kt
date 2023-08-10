package com.maxmakarov.base.gallery.data

import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.maxmakarov.base.gallery.api.UnsplashApi
import com.maxmakarov.base.gallery.db.ImageDatabase
import com.maxmakarov.base.gallery.model.UnsplashImage
import com.maxmakarov.core.Config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class ImagesRepositoryImpl(
    private val api: UnsplashApi,
    private val database: ImageDatabase
): ImagesRepository {

    private val defaultConfig = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false)

    private fun pagerFlow(pagingSourceFactory: () -> PagingSource<Int, UnsplashImage>): Flow<PagingData<UnsplashImage>> {
        return Pager(config = defaultConfig, pagingSourceFactory = pagingSourceFactory).flow
    }

    override fun loadImagesStream() =
        pagerFlow { LoadImagesPagingSource(api) }
            .flowOn(Dispatchers.Default)

    override fun searchImagesStream(query: String) =
        pagerFlow { SearchImagesPagingSource(api, query) }
            .flowOn(Dispatchers.Default)

    override fun getFavorites() =
        pagerFlow { database.favoritesDao().getFavorites() }
            .flowOn(Dispatchers.Default)

    override fun checkIsAddedToFavs(image: UnsplashImage) =
        database.favoritesDao()
            .checkIsAdded(image.id)
            .flowOn(Dispatchers.Default)

    override suspend fun addToFavorites(image: UnsplashImage) {
        withContext(Dispatchers.Default) {
            database.favoritesDao().addToFavorites(image.copy(savedTimestamp = System.currentTimeMillis()))
        }
    }

    override suspend fun removeFromFavorites(image: UnsplashImage) {
        withContext(Dispatchers.Default) {
            database.favoritesDao().removeFromFavorites(image.id)
        }
    }

    override suspend fun trackDownload(url: String) {
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

        //fixme: Temporary workaround of passing the argument to FullscreenImageFragment
        //currently Navigation doesn't support Parcelable as a deeplink argument as well as
        //it can't pass Json-serialized object properly because of symbols in the blur_hash field
        var imageToView: UnsplashImage? = null
    }
}