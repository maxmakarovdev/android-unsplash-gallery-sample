package com.maxmakarov.feature.gallery

sealed class UiAction {
    data class Search(val query: String) : UiAction()
}

data class UiState(
    val query: String = ""
)