package com.maxmakarov.base.gallery.ui

import android.annotation.SuppressLint
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.paging.LoadState
import androidx.paging.LoadState.Error
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import com.maxmakarov.base.gallery.R
import com.maxmakarov.base.gallery.data.PhotosRepository
import com.maxmakarov.base.gallery.model.UnsplashPhoto
import com.maxmakarov.core.ui.BaseFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class BaseGalleryFragment<VB : ViewBinding> : BaseFragment<VB>() {
    private lateinit var photosAdapter: PhotosAdapter
    private lateinit var layoutManager: StaggeredGridLayoutManager

    private val adapterCallback = object : PhotoViewHolder.PhotoClickCallback {
        override fun onClick(photo: UnsplashPhoto) {
            PhotosRepository.photoToView = photo
            val request = NavDeepLinkRequest.Builder
                .fromUri("android-app://com.maxmakarov.gallery/image_view_fragment".toUri())
                .build()
            findNavController().navigate(
                request,
                navOptions {
                    anim {
                        enter = android.R.animator.fade_in
                        exit = android.R.animator.fade_out
                    }
                },
                //todo FragmentNavigator.Extras.Builder().addSharedElement()
            )
        }
    }

    abstract fun provideData(): Flow<PagingData<UiModel>>
    abstract fun provideList(): RecyclerView
    abstract fun provideProgressBar(): View
    abstract fun provideEmptyListPlaceholder(): View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindList(provideList(), provideData())
    }

    @SuppressLint("ShowToast")
    private fun bindList(list: RecyclerView, pagingData: Flow<PagingData<UiModel>>) {
        photosAdapter = PhotosAdapter(adapterCallback)
        val spanCount = if (resources.configuration.orientation == ORIENTATION_LANDSCAPE) 3 else 2
        layoutManager = StaggeredGridLayoutManager(spanCount, LinearLayoutManager.VERTICAL)
        list.adapter = photosAdapter
        list.layoutManager = layoutManager

        lifecycleScope.launch {
            pagingData.collectLatest(photosAdapter::submitData)
        }

        lifecycleScope.launch {
            photosAdapter.loadStateFlow.collect { loadState ->
                val isListEmpty = loadState.refresh is LoadState.NotLoading && photosAdapter.itemCount == 0
                provideEmptyListPlaceholder().isVisible = isListEmpty
                list.isVisible = !isListEmpty
                provideProgressBar().isVisible = loadState.source.refresh is LoadState.Loading

                val refreshState = loadState.source.refresh
                if (refreshState is Error) {
                    val message = refreshState.error.message.orEmpty()
                    Snackbar.make(list, message, LENGTH_INDEFINITE).apply {
                        setAction(R.string.retry) { photosAdapter.retry() }
                        showAboveNavBar()
                    }
                }

                val errorState = loadState.source.append as? Error
                    ?: loadState.source.prepend as? Error
                    ?: loadState.append as? Error
                    ?: loadState.prepend as? Error
                errorState?.error?.also { error ->
                    activity?.also {
                        val message = getString(R.string.error_wooops_n1, error)
                        Snackbar.make(list, message, Snackbar.LENGTH_SHORT).showAboveNavBar()
                    }
                }
            }
        }
    }
}