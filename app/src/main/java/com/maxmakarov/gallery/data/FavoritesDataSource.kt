package com.maxmakarov.gallery.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.maxmakarov.gallery.db.PhotoDatabase
import com.maxmakarov.gallery.model.UnsplashPhoto

class FavoritesDataSource(private val database: PhotoDatabase) : PagingSource<Int, UnsplashPhoto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashPhoto> {
        val page = params.key ?: STARTING_PAGE_INDEX

        return try {
            val entities = database.favoritesDao().getFavorites(params.loadSize, page * params.loadSize)

            LoadResult.Page(
                data = entities,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (entities.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UnsplashPhoto>) = state.defaultRefreshKey()

    companion object {
        private const val STARTING_PAGE_INDEX = 0
    }
}