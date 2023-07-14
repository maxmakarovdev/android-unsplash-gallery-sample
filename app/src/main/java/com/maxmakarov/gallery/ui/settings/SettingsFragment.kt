package com.maxmakarov.gallery.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.maxmakarov.gallery.databinding.SettingsFragmentBinding
import kotlin.LazyThreadSafetyMode.NONE

class SettingsFragment : Fragment() {

    private lateinit var binding: SettingsFragmentBinding
    private val viewModel by lazy(NONE){ ViewModelProvider(this)[SettingsViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
}