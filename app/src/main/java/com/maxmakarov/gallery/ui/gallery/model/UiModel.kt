package com.maxmakarov.gallery.ui.gallery.model

import com.maxmakarov.gallery.model.UnsplashPhoto

sealed class UiAction {
    data class Search(val query: String) : UiAction()
}

data class UiState(
    val query: String = DEFAULT_QUERY
)

sealed class UiModel {
    data class PhotoItem(val photo: UnsplashPhoto) : UiModel()
}

const val VISIBLE_THRESHOLD = 5
const val DEFAULT_QUERY = ""