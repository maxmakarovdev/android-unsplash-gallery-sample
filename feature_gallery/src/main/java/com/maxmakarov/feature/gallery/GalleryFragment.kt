package com.maxmakarov.feature.gallery

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.maxmakarov.base.gallery.ui.BaseGalleryFragment
import com.maxmakarov.base.gallery.ui.UiAction
import com.maxmakarov.base.gallery.ui.UiState
import com.maxmakarov.feature.gallery.databinding.GalleryFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

@AndroidEntryPoint
class GalleryFragment : BaseGalleryFragment<GalleryFragmentBinding>() {

    private val viewModel: GalleryViewModel by viewModels()

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): GalleryFragmentBinding {
        return GalleryFragmentBinding.inflate(inflater, container, false)
    }

    override fun provideData() = viewModel.pagingDataFlow
    override fun provideList() = binding.list
    override fun provideProgressBar() = binding.animationProgress
    override fun provideEmptyListPlaceholder() = binding.emptyList

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bindSearch(
            uiState = viewModel.state,
            onQueryChanged = viewModel.accept
        )
        binding.apply {
            list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val alignLowerBound = max(0, dy - search.translationY.toInt())
                    val alignUpperBound = -min(alignLowerBound, search.height + search.marginTop * 2)
                    search.translationY = alignUpperBound.toFloat()
                }
            })
        }
    }

    private inline fun GalleryFragmentBinding.bindSearch(
        uiState: StateFlow<UiState>,
        crossinline onQueryChanged: (UiAction.Search) -> Unit
    ) {
        search.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }
        search.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }
        searchClear.setOnClickListener {
            onQueryChanged(UiAction.Search(query = ""))
        }

        viewLifecycleOwner.lifecycleScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect {
                    search.setText(it)
                    search.setSelection(search.length())
                    searchClear.isVisible = it.isNotEmpty()
                }
        }
    }

    private inline fun GalleryFragmentBinding.updateListFromInput(onQueryChanged: (UiAction.Search) -> Unit) {
        search.text.trim().let {
            if (it.isNotEmpty()) {
                list.smoothScrollToPosition(0)
                onQueryChanged(UiAction.Search(query = it.toString()))
            }
        }
    }
}