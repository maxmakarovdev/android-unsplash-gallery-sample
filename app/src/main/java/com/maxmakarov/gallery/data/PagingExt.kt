package com.maxmakarov.gallery.data

import androidx.paging.PagingState

fun PagingState<Int, *>.defaultRefreshKey(): Int? {
    return anchorPosition?.let { anchorPosition ->
        val anchorPage = closestPageToPosition(anchorPosition)
        anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
    }
}