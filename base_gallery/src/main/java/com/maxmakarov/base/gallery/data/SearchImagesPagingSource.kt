package com.maxmakarov.base.gallery.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.maxmakarov.base.gallery.api.UnsplashApi
import com.maxmakarov.base.gallery.data.ImagesRepository.Companion.PAGE_SIZE
import com.maxmakarov.base.gallery.model.UnsplashImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class SearchImagesPagingSource(
    private val api: UnsplashApi,
    private val query: String
) : PagingSource<Int, UnsplashImage>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashImage> {
        return try {
            withContext(Dispatchers.Default) {
                val page = params.key ?: STARTING_PAGE_INDEX
                val response = api.searchImages(query, page, params.loadSize)
                val imagesList = response.results
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