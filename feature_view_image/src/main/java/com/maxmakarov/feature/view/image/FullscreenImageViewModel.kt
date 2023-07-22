package com.maxmakarov.feature.view.image

import androidx.fragment.app.Fragment
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.maxmakarov.base.gallery.data.PhotosRepository
import com.maxmakarov.base.gallery.db.PhotoDatabase
import com.maxmakarov.base.gallery.model.UnsplashPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class FullscreenImageViewModel(private val repository: PhotosRepository) : ViewModel() {

    lateinit var photo: UnsplashPhoto
    val isFavoriteStream = MutableStateFlow<Boolean?>(null)

    fun init(photo: UnsplashPhoto) {
        this.photo = photo
        viewModelScope.launch {
            checkIsFavorite()
        }
    }

    fun favoriteClicked() {
        viewModelScope.launch {
            val isFavorite = isFavoriteStream.value
            repository.run { if(isFavorite == true) removeFromFavorites(photo) else addToFavorites(photo) }
                .flowOn(Dispatchers.IO)
                .collectLatest {
                    checkIsFavorite()
                }
        }
    }

    private suspend fun checkIsFavorite() {
        repository.checkPhotoIsAdded(photo)
            .flowOn(Dispatchers.IO)
            .collectLatest {
                isFavoriteStream.emit(it)
            }
    }

    /**
     * To abide by the API guidelines, you need to trigger a GET request to this endpoint
     * every time your application performs a download of a photo
     */

    fun trackDownloads() { //todo
//        viewModelScope.launch {
//            repository.trackDownload(photo.links.download_location)
//                .flowOn(Dispatchers.IO)
//                .collect()
//        }
    }

    companion object {
        fun get(fragment: Fragment): FullscreenImageViewModel {
            val factory = object : AbstractSavedStateViewModelFactory(fragment, null){
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    val repository = PhotosRepository.create(PhotoDatabase.getInstance(fragment.requireContext()))
                    if (modelClass.isAssignableFrom(FullscreenImageViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return FullscreenImageViewModel(repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }

            return ViewModelProvider(fragment, factory)[FullscreenImageViewModel::class.java]
        }
    }
}