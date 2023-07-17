package com.maxmakarov.gallery.ui.favourites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.maxmakarov.gallery.core.BaseFragment
import com.maxmakarov.gallery.databinding.FavouritesFragmentBinding
import kotlin.LazyThreadSafetyMode.NONE

class FavouritesFragment : BaseFragment<FavouritesFragmentBinding>() {

    private val viewModel by lazy(NONE) { ViewModelProvider(this)[FavouritesViewModel::class.java] }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): FavouritesFragmentBinding {
        return FavouritesFragmentBinding.inflate(inflater, container, false)
    }
}