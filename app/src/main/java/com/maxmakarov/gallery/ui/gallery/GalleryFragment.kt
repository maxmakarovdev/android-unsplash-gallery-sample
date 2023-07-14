package com.maxmakarov.gallery.ui.gallery

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.maxmakarov.gallery.databinding.GalleryFragmentBinding
import com.maxmakarov.gallery.model.UnsplashPhoto
import com.maxmakarov.gallery.ui.gallery.list.PhotoViewHolder
import com.maxmakarov.gallery.ui.gallery.list.PhotosAdapter
import com.maxmakarov.gallery.ui.gallery.model.UiAction
import com.maxmakarov.gallery.ui.gallery.model.UiModel
import com.maxmakarov.gallery.ui.gallery.model.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.LazyThreadSafetyMode.NONE

class GalleryFragment : Fragment() {

    private lateinit var binding: GalleryFragmentBinding
    private val viewModel by lazy(NONE){ GalleryViewModel.get(this) }

    private lateinit var photosAdapter: PhotosAdapter
    private lateinit var layoutManager: StaggeredGridLayoutManager
    private val adapterCallback = object : PhotoViewHolder.PhotoClickCallback {
        override fun onClick(photo: UnsplashPhoto) {
            //todo make a built-in fullscreen photo viewer
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//                view.context.startActivity(intent)
        }

        override fun onLongClick(photo: UnsplashPhoto) {
            TODO("Not yet implemented")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GalleryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindState(
            uiState = viewModel.state,
            pagingData = viewModel.pagingDataFlow,
            uiActions = viewModel.accept
        )
    }

    /**
     * Binds the [UiState] provided by the [GalleryViewModel] to the UI,
     * and allows the UI to feed back user actions to it.
     */
    private fun GalleryFragmentBinding.bindState(
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<UiModel>>,
        uiActions: (UiAction) -> Unit
    ) {
        photosAdapter = PhotosAdapter(adapterCallback)
        layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        list.adapter = photosAdapter
        list.layoutManager = layoutManager

        bindSearch(
            uiState = uiState,
            onQueryChanged = uiActions
        )
        bindList(
            photosAdapter = photosAdapter,
            uiState = uiState,
            pagingData = pagingData
        )
    }

    private fun GalleryFragmentBinding.bindSearch(
        uiState: StateFlow<UiState>,
        onQueryChanged: (UiAction.Search) -> Unit
    ) {
        searchRepo.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }
        searchRepo.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepoListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }

        lifecycleScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collect(searchRepo::setText)
        }
    }

    private fun GalleryFragmentBinding.updateRepoListFromInput(onQueryChanged: (UiAction.Search) -> Unit) {
        searchRepo.text.trim().let {
            if (it.isNotEmpty()) {
                list.scrollToPosition(0)
                onQueryChanged(UiAction.Search(query = it.toString()))
            }
        }
    }

    private fun GalleryFragmentBinding.bindList(
        photosAdapter: PhotosAdapter,
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<UiModel>>
    ) {
        retryButton.setOnClickListener { photosAdapter.retry() }

        lifecycleScope.launch {
            pagingData.collectLatest(photosAdapter::submitData)
        }

        lifecycleScope.launch {
            photosAdapter.loadStateFlow.collect { loadState ->
                val isListEmpty = loadState.refresh is LoadState.NotLoading && photosAdapter.itemCount == 0
                emptyList.isVisible = isListEmpty
                list.isVisible = !isListEmpty
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                retryButton.isVisible = loadState.source.refresh is LoadState.Error

                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(requireActivity(),
                        "\uD83D\uDE28 Wooops ${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // we want the recycler view to have 3 columns when in landscape and 2 in portrait
        layoutManager.spanCount = if (newConfig.orientation == ORIENTATION_LANDSCAPE) 3 else 2
        photosAdapter.notifyDataSetChanged()
    }
}