package com.maxmakarov.gallery.api

import com.maxmakarov.gallery.model.UnsplashPhoto

data class SearchPhotosResponse(
    val total: Int,
    val total_pages: Int,
    val results: List<UnsplashPhoto>
)