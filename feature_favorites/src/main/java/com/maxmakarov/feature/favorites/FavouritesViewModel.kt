package com.maxmakarov.feature.favorites

import androidx.fragment.app.Fragment
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.maxmakarov.base.gallery.data.PhotosRepository
import com.maxmakarov.base.gallery.db.PhotoDatabase
import com.maxmakarov.base.gallery.ui.UiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class FavouritesViewModel(repository: PhotosRepository) : ViewModel() {

    val pagingDataFlow: Flow<PagingData<UiModel>>

    init {
        pagingDataFlow = repository.getFavorites()
            .flowOn(Dispatchers.IO)
            .map { pagingData -> pagingData.map { UiModel.PhotoItem(it) as UiModel } }
            .cachedIn(viewModelScope)
    }

    companion object {
        fun get(fragment: Fragment): FavouritesViewModel {
            val factory = object : AbstractSavedStateViewModelFactory(fragment, null){
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    val repository = PhotosRepository.create(PhotoDatabase.getInstance(fragment.requireContext()))
                    if (modelClass.isAssignableFrom(FavouritesViewModel::class.java)) {
                        return FavouritesViewModel(repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }

            return ViewModelProvider(fragment, factory)[FavouritesViewModel::class.java]
        }
    }
}