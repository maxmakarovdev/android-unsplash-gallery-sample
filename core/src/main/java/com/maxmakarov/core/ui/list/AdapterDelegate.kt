package com.maxmakarov.core.ui.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class AdapterDelegate<I : AdapterItem, VH : ViewHolder<*, *>>(val itemClass: Class<I>) {
    abstract fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    abstract fun bindViewHolder(item: I, viewHolder: VH, payloads: List<AdapterItem.Payloadable>)
}