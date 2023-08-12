package com.maxmakarov.core.ui.list

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class ViewHolder<VB : ViewBinding, I : AdapterItem>(
    private val binding: VB,
    onClick: (VB, I) -> Unit = { _, _ -> }
): RecyclerView.ViewHolder(binding.root) {

    private var bound: I? = null

    init {
        binding.root.setOnClickListener {
            bound?.also { item ->
                onClick(binding, item)
            }
        }
    }

    fun bind(item: I, to: VB.(I) -> Unit) {
        bound = item
        binding.to(item)
    }
}