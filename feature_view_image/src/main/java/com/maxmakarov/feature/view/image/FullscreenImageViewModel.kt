package com.maxmakarov.feature.view.image

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxmakarov.base.gallery.data.ImagesRepository
import com.maxmakarov.base.gallery.model.UnsplashImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FullscreenImageViewModel @Inject constructor(
    private val repository: ImagesRepository
) : ViewModel() {

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
}