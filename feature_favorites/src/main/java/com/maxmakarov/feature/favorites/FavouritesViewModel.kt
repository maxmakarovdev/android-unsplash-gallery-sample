package com.maxmakarov.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.maxmakarov.base.gallery.data.ImagesRepository
import com.maxmakarov.base.gallery.ui.UiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val repository: ImagesRepository
) : ViewModel() {

    val pagingDataFlow = repository.getFavorites()
        .map { pagingData -> pagingData.map { UiModel.ImageItem(it) as UiModel } }
        .cachedIn(viewModelScope)
}