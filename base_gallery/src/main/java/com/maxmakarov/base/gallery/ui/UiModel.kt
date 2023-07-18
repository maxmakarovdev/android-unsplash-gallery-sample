package com.maxmakarov.base.gallery.ui

import com.maxmakarov.base.gallery.model.UnsplashPhoto

sealed class UiAction {
    data class Search(val query: String) : UiAction()
}

data class UiState(
    val query: String = ""
)

sealed class UiModel {
    data class PhotoItem(val photo: UnsplashPhoto) : UiModel()
}