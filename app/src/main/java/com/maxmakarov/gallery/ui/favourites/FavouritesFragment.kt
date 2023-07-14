package com.maxmakarov.gallery.ui.favourites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.maxmakarov.gallery.databinding.FavouritesFragmentBinding
import kotlin.LazyThreadSafetyMode.NONE

class FavouritesFragment : Fragment() {

    private lateinit var binding: FavouritesFragmentBinding
    private val viewModel by lazy(NONE){ ViewModelProvider(this)[FavouritesViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FavouritesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
}