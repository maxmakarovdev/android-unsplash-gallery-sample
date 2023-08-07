package com.maxmakarov.base.gallery.api

import com.maxmakarov.base.gallery.model.UnsplashImage

data class SearchImagesResponse(
    val total: Int,
    val total_pages: Int,
    val results: List<UnsplashImage>
)