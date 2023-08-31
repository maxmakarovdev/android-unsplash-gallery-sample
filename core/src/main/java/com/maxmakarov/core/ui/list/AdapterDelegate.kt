package com.maxmakarov.core.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class AdapterDelegate<I : AdapterItem, VB : ViewBinding, VH : ViewHolder<I, VB>>(
    val itemClass: Class<I>,
    private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB,
    private val onBind: (I, VH, List<AdapterItem.Payloadable>) -> Unit,
    private val onRootClick: (I, VB) -> Unit = { _, _ -> }
) {

    fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = bindingInflater(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onRootClick)
    }

    fun bindViewHolder(item: I, viewHolder: VH, payloads: List<AdapterItem.Payloadable>){
        onBind(item, viewHolder, payloads)
    }
}