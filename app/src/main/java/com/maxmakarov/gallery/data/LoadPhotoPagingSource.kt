package com.maxmakarov.gallery.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.maxmakarov.gallery.api.UnsplashService
import com.maxmakarov.gallery.data.PhotosRepository.Companion.PAGE_SIZE
import com.maxmakarov.gallery.model.UnsplashPhoto
import retrofit2.HttpException
import java.io.IOException

class LoadPhotoPagingSource(private val service: UnsplashService) : PagingSource<Int, UnsplashPhoto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashPhoto> {
        val page = params.key ?: STARTING_PAGE_INDEX
        return try {
            val photosList = service.loadPhotos(page, params.loadSize)
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

    // The refresh key is used for subsequent refresh calls to PagingSource.load after the initial load
    override fun getRefreshKey(state: PagingState<Int, UnsplashPhoto>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }
}