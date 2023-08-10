package com.maxmakarov.base.gallery.ui

import android.annotation.SuppressLint
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.FragmentNavigatorExtras
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
import com.maxmakarov.base.gallery.data.ImagesRepositoryImpl
import com.maxmakarov.base.gallery.model.UnsplashImage
import com.maxmakarov.core.ui.BaseFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class BaseGalleryFragment<VB : ViewBinding> : BaseFragment<VB>() {
    private lateinit var imagesAdapter: ImagesAdapter
    private lateinit var layoutManager: StaggeredGridLayoutManager

    private val adapterCallback = object : ImageViewHolder.ImageClickCallback {
        override fun onClick(imageView: View, image: UnsplashImage) {
            ImagesRepositoryImpl.imageToView = image
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
                FragmentNavigatorExtras(
                    imageView to image.id
                )
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
        imagesAdapter = ImagesAdapter(adapterCallback)
        val spanCount = if (resources.configuration.orientation == ORIENTATION_LANDSCAPE) 3 else 2
        layoutManager = StaggeredGridLayoutManager(spanCount, LinearLayoutManager.VERTICAL)
        list.adapter = imagesAdapter
        list.layoutManager = layoutManager

        viewLifecycleOwner.lifecycleScope.launch {
            pagingData
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collectLatest(imagesAdapter::submitData)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            imagesAdapter.loadStateFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { loadState ->
                    val isListEmpty = loadState.refresh is LoadState.NotLoading && imagesAdapter.itemCount == 0
                    provideEmptyListPlaceholder().isVisible = isListEmpty
                    list.isVisible = !isListEmpty
                    provideProgressBar().isVisible = loadState.source.refresh is LoadState.Loading

                    val refreshState = loadState.source.refresh
                    if (refreshState is Error) {
                        val message = refreshState.error.message.orEmpty()
                        Snackbar.make(list, message, LENGTH_INDEFINITE).apply {
                            setAction(R.string.retry) { imagesAdapter.retry() }
                            showAboveNavBar()
                        }
                    }

                    loadState.run {
                        source.append as? Error ?: source.prepend as? Error ?: append as? Error
                        ?: prepend as? Error
                    }?.error?.also { error ->
                        activity?.also {
                            val message = getString(R.string.error_wooops_n1, error)
                            Snackbar.make(list, message, Snackbar.LENGTH_SHORT).showAboveNavBar()
                        }
                    }
                }
        }
    }
}