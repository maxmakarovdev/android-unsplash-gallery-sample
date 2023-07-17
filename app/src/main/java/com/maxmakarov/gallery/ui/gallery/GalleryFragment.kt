package com.maxmakarov.gallery.ui.gallery

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import com.maxmakarov.gallery.R
import com.maxmakarov.gallery.core.BaseFragment
import com.maxmakarov.gallery.databinding.GalleryFragmentBinding
import com.maxmakarov.gallery.model.UnsplashPhoto
import com.maxmakarov.gallery.ui.gallery.list.PhotoViewHolder
import com.maxmakarov.gallery.ui.gallery.list.PhotosAdapter
import com.maxmakarov.gallery.ui.gallery.model.UiAction
import com.maxmakarov.gallery.ui.gallery.model.UiModel
import com.maxmakarov.gallery.ui.gallery.model.UiState
import com.maxmakarov.gallery.ui.imageview.FullscreenImageFragment.Companion.ARG_PHOTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.math.max
import kotlin.math.min


class GalleryFragment : BaseFragment<GalleryFragmentBinding>() {

    private val viewModel by lazy(NONE){ GalleryViewModel.get(this) }

    private lateinit var photosAdapter: PhotosAdapter
    private lateinit var layoutManager: StaggeredGridLayoutManager
    private val adapterCallback = object : PhotoViewHolder.PhotoClickCallback {
        override fun onClick(photo: UnsplashPhoto) {
            findNavController().navigate(
                R.id.action_navigation_gallery_to_navigation_imageview,
                Bundle().apply { putParcelable(ARG_PHOTO, photo) },
                navOptions {
                    anim {
                        enter = android.R.animator.fade_in
                        exit = android.R.animator.fade_out
                    }
                }
            )
        }
    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): GalleryFragmentBinding {
        return GalleryFragmentBinding.inflate(inflater, container, false)
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
        val spanCount = if (resources.configuration.orientation == ORIENTATION_LANDSCAPE) 3 else 2
        layoutManager = StaggeredGridLayoutManager(spanCount, LinearLayoutManager.VERTICAL)
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
        search.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }
        search.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }

        lifecycleScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collect {
                    search.setText(it)
                    search.setSelection(search.length())
                }
        }
    }

    private fun GalleryFragmentBinding.updateListFromInput(onQueryChanged: (UiAction.Search) -> Unit) {
        search.text.trim().let {
            if (it.isNotEmpty()) {
                list.smoothScrollToPosition(0)
                onQueryChanged(UiAction.Search(query = it.toString()))
            }
        }
    }

    private fun GalleryFragmentBinding.bindList(
        photosAdapter: PhotosAdapter,
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<UiModel>>
    ) {
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val alignLowerBound = max(0, dy - search.translationY.toInt())
                val alignUpperBound = -min(alignLowerBound, search.height + search.marginTop * 2)

                search.translationY = alignUpperBound.toFloat()
            }
        })

        lifecycleScope.launch {
            pagingData.collectLatest(photosAdapter::submitData)
        }

        lifecycleScope.launch {
            photosAdapter.loadStateFlow.collect { loadState ->
                val isListEmpty = loadState.refresh is LoadState.NotLoading && photosAdapter.itemCount == 0
                emptyList.isVisible = isListEmpty
                list.isVisible = !isListEmpty
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                val refreshState = loadState.source.refresh
                if (refreshState is LoadState.Error) {
                    Snackbar.make(list, refreshState.error.message.orEmpty(), LENGTH_INDEFINITE)
                        .setAction(R.string.retry) { photosAdapter.retry() }
                        .show()
                }

                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.error?.also { error ->
                    activity?.also {
                        Snackbar.make(list, "\uD83D\uDE28 Wooops $error", LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}