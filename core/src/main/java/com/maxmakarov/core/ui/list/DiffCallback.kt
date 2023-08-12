package com.maxmakarov.core.ui.list

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

internal class DiffCallback: DiffUtil.ItemCallback<AdapterItem>() {

    override fun areItemsTheSame(oldItem: AdapterItem, newItem: AdapterItem): Boolean =
        oldItem::class == newItem::class && oldItem.id() == newItem.id()

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: AdapterItem, newItem: AdapterItem): Boolean =
        oldItem.content() == newItem.content()

    override fun getChangePayload(oldItem: AdapterItem, newItem: AdapterItem): Any =
        oldItem.payload(newItem)
}