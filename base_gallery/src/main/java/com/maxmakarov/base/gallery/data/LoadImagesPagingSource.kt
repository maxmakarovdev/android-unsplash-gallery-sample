package com.maxmakarov.base.gallery.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.maxmakarov.base.gallery.api.UnsplashApi
import com.maxmakarov.base.gallery.data.ImagesRepositoryImpl.Companion.PAGE_SIZE
import com.maxmakarov.base.gallery.model.UnsplashImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class LoadImagesPagingSource(private val api: UnsplashApi) : PagingSource<Int, UnsplashImage>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashImage> {
        return try {
            withContext(Dispatchers.Default) {
                val page = params.key ?: STARTING_PAGE_INDEX
                val imagesList = api.loadImages(page, params.loadSize)
                //todo response.headers().get("x-ratelimit-remaining")?.toInt()?
//            lastPage = response.headers().get("x-total")?.toInt()?.div(params.requestedLoadSize)
                LoadResult.Page(
                    data = imagesList,
                    prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                    nextKey = if (imagesList.isEmpty()) null else page + params.loadSize / PAGE_SIZE
                )
            }
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UnsplashImage>) = state.defaultRefreshKey()

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }
}