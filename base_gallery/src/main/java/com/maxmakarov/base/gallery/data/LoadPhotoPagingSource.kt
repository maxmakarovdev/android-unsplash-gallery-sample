package com.maxmakarov.base.gallery.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.maxmakarov.base.gallery.api.UnsplashApi
import com.maxmakarov.base.gallery.data.PhotosRepository.Companion.PAGE_SIZE
import com.maxmakarov.base.gallery.model.UnsplashPhoto
import retrofit2.HttpException
import java.io.IOException

class LoadPhotoPagingSource(private val api: UnsplashApi) : PagingSource<Int, UnsplashPhoto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashPhoto> {
        val page = params.key ?: STARTING_PAGE_INDEX
        return try {
            val photosList = api.loadPhotos(page, params.loadSize)
            //todo response.headers().get("x-ratelimit-remaining")?.toInt()?
//            lastPage = response.headers().get("x-total")?.toInt()?.div(params.requestedLoadSize)
            LoadResult.Page(
                data = photosList,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (photosList.isEmpty()) null else page + (params.loadSize / PAGE_SIZE)
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UnsplashPhoto>) = state.defaultRefreshKey()

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }
}