package com.maxmakarov.base.gallery.api

import com.maxmakarov.base.gallery.model.UnsplashPhoto

data class SearchPhotosResponse(
    val total: Int,
    val total_pages: Int,
    val results: List<UnsplashPhoto>
)