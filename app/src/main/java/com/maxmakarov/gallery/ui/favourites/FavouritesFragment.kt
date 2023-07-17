package com.maxmakarov.gallery.ui.favourites

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.maxmakarov.gallery.R
import com.maxmakarov.gallery.core.BaseFragment
import com.maxmakarov.gallery.databinding.FavouritesFragmentBinding
import com.maxmakarov.gallery.model.UnsplashPhoto
import com.maxmakarov.gallery.ui.gallery.list.PhotoViewHolder
import com.maxmakarov.gallery.ui.gallery.list.PhotosAdapter
import com.maxmakarov.gallery.ui.gallery.model.UiModel
import com.maxmakarov.gallery.ui.imageview.FullscreenImageFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.LazyThreadSafetyMode.NONE

//todo create BaseGalleryFragment and move common logic there
class FavouritesFragment : BaseFragment<FavouritesFragmentBinding>() {

    private val viewModel by lazy(NONE) { FavouritesViewModel.get(this) }

    private lateinit var photosAdapter: PhotosAdapter
    private lateinit var layoutManager: StaggeredGridLayoutManager
    private val adapterCallback = object : PhotoViewHolder.PhotoClickCallback {
        override fun onClick(photo: UnsplashPhoto) {
            findNavController().navigate(
                R.id.action_navigation_favourites_to_navigation_imageview,
                Bundle().apply { putParcelable(FullscreenImageFragment.ARG_PHOTO, photo) },
                navOptions {
                    anim {
                        enter = android.R.animator.fade_in
                        exit = android.R.animator.fade_out
                    }
                }
            )
        }
    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): FavouritesFragmentBinding {
        return FavouritesFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindList(
            pagingData = viewModel.pagingDataFlow
        )
    }

    private fun FavouritesFragmentBinding.bindList(
        pagingData: Flow<PagingData<UiModel>>
    ) {
        photosAdapter = PhotosAdapter(adapterCallback)
        val spanCount = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 3 else 2
        layoutManager = StaggeredGridLayoutManager(spanCount, LinearLayoutManager.VERTICAL)
        list.adapter = photosAdapter
        list.layoutManager = layoutManager

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
                    Snackbar.make(list, refreshState.error.message.orEmpty(),
                        BaseTransientBottomBar.LENGTH_INDEFINITE
                    )
                        .setAction(R.string.retry) { photosAdapter.retry() }
                        .show()
                }

                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.error?.also { error ->
                    activity?.also {
                        Snackbar.make(list, "\uD83D\uDE28 Wooops $error", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}