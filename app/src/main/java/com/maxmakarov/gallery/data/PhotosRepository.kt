package com.maxmakarov.gallery.data

import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.maxmakarov.gallery.api.RetrofitFactory
import com.maxmakarov.gallery.api.UnsplashService
import com.maxmakarov.gallery.core.Config
import com.maxmakarov.gallery.model.UnsplashPhoto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

class PhotosRepository(private val service: UnsplashService) {

    fun loadPhotosStream(): Flow<PagingData<UnsplashPhoto>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { LoadPhotoPagingSource(service) }
        ).flow
    }

    fun searchPhotosStream(query: String): Flow<PagingData<UnsplashPhoto>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SearchPhotoDataSource(service, query) }
        ).flow
    }

    //todo move to data source?
    fun trackDownload(url: String?): Flow<Any> {
        if (url != null) {
            val uriBuilder = Uri.parse(url).buildUpon()
            uriBuilder.appendQueryParameter("client_id", Config.accessKey)
            val downloadUrl = uriBuilder.build().toString()
            return flow { service.trackDownload(downloadUrl) }
        }
        return emptyFlow()
    }

    companion object {
        const val PAGE_SIZE = 20
        fun create(): PhotosRepository {
            return PhotosRepository(RetrofitFactory.createNetworkEndpoints())
        }
    }
}