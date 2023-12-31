package com.maxmakarov.feature.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.transition.TransitionManager
import com.maxmakarov.core.ui.BaseFragment
import com.maxmakarov.feature.settings.databinding.SettingsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment<SettingsFragmentBinding>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): SettingsFragmentBinding {
        return SettingsFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            root.postDelayed(500) {
                TransitionManager.beginDelayedTransition(root)
                nothingHere.isVisible = true
            }
        }
    }
}