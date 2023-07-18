package com.maxmakarov.feature.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import com.maxmakarov.core.ui.BaseFragment
import com.maxmakarov.feature.settings.databinding.SettingsFragmentBinding

class SettingsFragment : BaseFragment<SettingsFragmentBinding>() {

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): SettingsFragmentBinding {
        return SettingsFragmentBinding.inflate(inflater, container, false)
    }
}