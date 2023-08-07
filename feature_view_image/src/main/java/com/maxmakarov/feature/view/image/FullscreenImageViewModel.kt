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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FullscreenImageViewModel(private val repository: PhotosRepository) : ViewModel() {

    lateinit var photo: UnsplashPhoto
    val isFavoriteStream = MutableStateFlow<Boolean?>(null)

    fun init(photo: UnsplashPhoto) {
        this.photo = photo
        viewModelScope.launch {
            repository.checkPhotoIsAdded(photo)
                .collectLatest {
                    isFavoriteStream.emit(it)
                }
        }
    }

    fun favoriteClicked() {
        viewModelScope.launch {
            val isFavorite = isFavoriteStream.value ?: false
            if (isFavorite) {
                repository.removeFromFavorites(photo)
            } else {
                repository.addToFavorites(photo)
            }
            isFavoriteStream.emit(!isFavorite)
        }
    }

    /**
     * To abide by the API guidelines, it needed to be triggered on each download
     */
    fun trackDownloads() {
        viewModelScope.launch {
            photo.links.download_location?.also { url ->
                repository.trackDownload(url)
            }
        }
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