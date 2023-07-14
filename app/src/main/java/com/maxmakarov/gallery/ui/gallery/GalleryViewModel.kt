package com.maxmakarov.gallery.ui.gallery

import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.maxmakarov.gallery.data.PhotosRepository
import com.maxmakarov.gallery.model.UnsplashPhoto
import com.maxmakarov.gallery.ui.gallery.model.DEFAULT_QUERY
import com.maxmakarov.gallery.ui.gallery.model.UiAction
import com.maxmakarov.gallery.ui.gallery.model.UiModel
import com.maxmakarov.gallery.ui.gallery.model.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GalleryViewModel(
    private val repository: PhotosRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    /**
     * Stream of immutable states representative of the UI.
     */
    val state: StateFlow<UiState>

    val pagingDataFlow: Flow<PagingData<UiModel>>

    /**
     * Processor of side effects from the UI which in turn feedback into [state]
     */
    val accept: (UiAction) -> Unit

    init {
        val actionStateFlow = MutableSharedFlow<UiAction>()
        val searches = actionStateFlow
            .filterIsInstance<UiAction.Search>()
            .distinctUntilChanged()
            .onStart { emit(UiAction.Search(query = DEFAULT_QUERY)) }

        pagingDataFlow = searches
            .debounce(500)
            .flatMapLatest {
                if (it.query.isBlank()) loadPhotos() else searchPhotos(queryString = it.query)
            }
            .cachedIn(viewModelScope)

        state = searches.map { search ->
                UiState(
                    query = search.query
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = UiState()
            )

        accept = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }

    private fun searchPhotos(queryString: String): Flow<PagingData<UiModel>> =
        repository.searchPhotosStream(queryString)
            .map { pagingData -> pagingData.map { UiModel.PhotoItem(it) } }

    private fun loadPhotos(): Flow<PagingData<UiModel>> =
        repository.loadPhotosStream()
            .map { pagingData -> pagingData.map { UiModel.PhotoItem(it) } }

    /**
     * To abide by the API guidelines,
     * you need to trigger a GET request to this endpoint every time your application performs a download of a photo
     *
     * @param photos the list of selected photos
     */
    //todo track downloads
    fun trackDownloads(photos: ArrayList<UnsplashPhoto>) {
        for (photo in photos) {
            repository.trackDownload(photo.links.download_location)
        }
    }

    companion object {
        //todo simplify
        fun get(fragment: Fragment): GalleryViewModel {
            val factory = object : AbstractSavedStateViewModelFactory(fragment, null){
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    val repository = PhotosRepository.create()
                    if (modelClass.isAssignableFrom(GalleryViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return GalleryViewModel(repository, handle) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }

            return ViewModelProvider(fragment, factory)[GalleryViewModel::class.java]
        }
    }
}