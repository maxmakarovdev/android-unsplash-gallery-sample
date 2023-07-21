package com.maxmakarov.feature.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import com.maxmakarov.base.gallery.ui.BaseGalleryFragment
import com.maxmakarov.feature.favorites.databinding.FavouritesFragmentBinding
import kotlin.LazyThreadSafetyMode.NONE

class FavouritesFragment : BaseGalleryFragment<FavouritesFragmentBinding>() {

    private val viewModel by lazy(NONE) { FavouritesViewModel.get(this) }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): FavouritesFragmentBinding {
        return FavouritesFragmentBinding.inflate(inflater, container, false)
    }

    override fun provideData() = viewModel.pagingDataFlow
    override fun provideList() = binding.list
    override fun provideProgressBar() = binding.animationProgress
    override fun provideEmptyListPlaceholder() = binding.emptyList

    override fun onResume() {
        super.onResume()
        hostActivity?.setFavouritesBadge(isVisible = false)
    }
}