package com.maxmakarov.gallery.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.maxmakarov.gallery.core.BaseFragment
import com.maxmakarov.gallery.databinding.SettingsFragmentBinding
import kotlin.LazyThreadSafetyMode.NONE

class SettingsFragment : BaseFragment<SettingsFragmentBinding>() {

    private val viewModel by lazy(NONE){ ViewModelProvider(this)[SettingsViewModel::class.java] }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): SettingsFragmentBinding {
        return SettingsFragmentBinding.inflate(inflater, container, false)
    }
}