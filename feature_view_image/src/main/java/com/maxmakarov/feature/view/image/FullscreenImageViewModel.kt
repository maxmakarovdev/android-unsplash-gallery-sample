package com.maxmakarov.feature.view.image

import androidx.fragment.app.Fragment
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.maxmakarov.base.gallery.data.ImagesRepository
import com.maxmakarov.base.gallery.db.ImageDatabase
import com.maxmakarov.base.gallery.model.UnsplashImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FullscreenImageViewModel(private val repository: ImagesRepository) : ViewModel() {

    lateinit var image: UnsplashImage
    val isFavoriteStream = MutableStateFlow<Boolean?>(null)

    fun init(image: UnsplashImage) {
        this.image = image
        viewModelScope.launch {
            repository.checkIsAddedToFavs(image)
                .collectLatest {
                    isFavoriteStream.emit(it)
                }
        }
    }

    fun favoriteClicked() {
        viewModelScope.launch {
            val isFavorite = isFavoriteStream.value ?: false
            if (isFavorite) {
                repository.removeFromFavorites(image)
            } else {
                repository.addToFavorites(image)
            }
            isFavoriteStream.emit(!isFavorite)
        }
    }

    /**
     * To abide by the API guidelines, it needed to be triggered on each download
     */
    fun trackDownloads() {
        viewModelScope.launch {
            image.links.download_location?.also { url ->
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
                    val repository = ImagesRepository.create(ImageDatabase.getInstance(fragment.requireContext()))
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