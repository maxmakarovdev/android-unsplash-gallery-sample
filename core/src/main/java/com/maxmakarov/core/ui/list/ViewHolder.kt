package com.maxmakarov.core.ui.list

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class ViewHolder<I : AdapterItem, VB : ViewBinding>(
    private val binding: VB,
    onClick: (I, VB) -> Unit = { _, _ -> }
): RecyclerView.ViewHolder(binding.root) {

    private var bound: I? = null

    init {
        binding.root.setOnClickListener {
            bound?.also { item ->
                onClick(item, binding)
            }
        }
    }

    fun bind(item: I, to: VB.(I) -> Unit) {
        bound = item
        binding.to(item)
    }
}