package com.maxmakarov.base.gallery.ui

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.maxmakarov.base.gallery.R
import com.maxmakarov.base.gallery.ui.ImageViewHolder.ImageClickCallback

//todo make an adapter composed from delegated adapters to support unlimited various view types
class ImagesAdapter(private val callback: ImageClickCallback) :
    PagingDataAdapter<UiModel, RecyclerView.ViewHolder>(UIMODEL_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.view_item_image -> ImageViewHolder.create(parent, callback)
            else -> throw UnsupportedOperationException("Unknown view type")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UiModel.ImageItem -> R.layout.view_item_image
            null -> throw UnsupportedOperationException("Unknown view")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val uiModel = getItem(position)
        uiModel.let {
            when (uiModel) {
                is UiModel.ImageItem -> (holder as ImageViewHolder).bind(uiModel.image)
                null -> throw UnsupportedOperationException("Unknown view")
            }
        }
    }

    companion object {
        private val UIMODEL_COMPARATOR = object : DiffUtil.ItemCallback<UiModel>() {
            override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
                return oldItem is UiModel.ImageItem && newItem is UiModel.ImageItem &&
                        oldItem.image.id == newItem.image.id
            }

            override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean =
                oldItem == newItem
        }
    }
}