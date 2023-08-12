package com.maxmakarov.core.ui.list

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView

class DelegateAdapter(
    private val delegates: List<AdapterDelegate<AdapterItem, ViewHolder<*, *>>>
) : PagingDataAdapter<AdapterItem, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return delegates
            .indexOfFirst { it.itemClass == getItem(position)?.javaClass }
            .takeIf { it != -1 }
            ?: throw NullPointerException("Can not get viewType for position $position")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        delegates[viewType].createViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        onBindViewHolder(holder, position, mutableListOf())

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        delegates[getItemViewType(position)].also { adapter ->
            val delegatePayloads = payloads.map { it as AdapterItem.Payloadable }
            getItem(position)?.also {
                adapter.bindViewHolder(it, holder as ViewHolder<*, *>, delegatePayloads)
            }
        }
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun with(vararg delegates: AdapterDelegate<out AdapterItem, out ViewHolder<*, *>>): DelegateAdapter {
            return DelegateAdapter(delegates.toList() as List<AdapterDelegate<AdapterItem, ViewHolder<*, *>>>)
        }
    }
}