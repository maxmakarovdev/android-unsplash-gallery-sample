package com.maxmakarov.feature.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.maxmakarov.base.gallery.data.ImagesRepository
import com.maxmakarov.base.gallery.ui.list.ImageAdapterItem
import com.maxmakarov.core.ui.list.AdapterItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class GalleryViewModel @Inject constructor(
    private val repository: ImagesRepository
) : ViewModel() {

    /**
     * Stream of immutable states representative of the UI.
     */
    val state: StateFlow<UiState>

    val pagingDataFlow: Flow<PagingData<AdapterItem>>

    /**
     * Processor of side effects from the UI which in turn feedback into [state]
     */
    val accept: (UiAction) -> Unit

    init {
        val actionStateFlow = MutableSharedFlow<UiAction>()
        val searches = actionStateFlow
            .filterIsInstance<UiAction.Search>()
            .onStart { emit(UiAction.Search(query = "")) }
            .distinctUntilChanged()

        pagingDataFlow = searches
            .flatMapLatest { if (it.query.isBlank()) loadImages() else searchImages(it.query) }
            .cachedIn(viewModelScope)

        state = searches
            .map { search -> UiState(query = search.query) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = UiState())

        accept = { action -> viewModelScope.launch { actionStateFlow.emit(action) } }
    }

    private fun searchImages(queryString: String): Flow<PagingData<AdapterItem>> =
        repository.searchImagesStream(queryString)
            .map { pagingData -> pagingData.map { ImageAdapterItem(it) } }

    private fun loadImages(): Flow<PagingData<AdapterItem>> =
        repository.loadImagesStream()
            .map { pagingData ->
                pagingData.map { ImageAdapterItem(it) }
            }
}