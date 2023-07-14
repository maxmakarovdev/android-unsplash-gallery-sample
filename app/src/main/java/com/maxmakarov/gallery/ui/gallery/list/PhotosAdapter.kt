package com.maxmakarov.gallery.ui.gallery.list

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.maxmakarov.gallery.R
import com.maxmakarov.gallery.ui.gallery.list.PhotoViewHolder.PhotoClickCallback
import com.maxmakarov.gallery.ui.gallery.model.UiModel

//todo make an adapter composed from delegated adapters to support unlimited various view types
class PhotosAdapter(private val callback: PhotoClickCallback) :
    PagingDataAdapter<UiModel, RecyclerView.ViewHolder>(UIMODEL_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.view_item_photo -> PhotoViewHolder.create(parent, callback)
            else -> throw UnsupportedOperationException("Unknown view type")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UiModel.PhotoItem -> R.layout.view_item_photo
            null -> throw UnsupportedOperationException("Unknown view")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val uiModel = getItem(position)
        uiModel.let {
            when (uiModel) {
                is UiModel.PhotoItem -> (holder as PhotoViewHolder).bind(uiModel.photo)
                null -> throw UnsupportedOperationException("Unknown view")
            }
        }
    }

    companion object {
        private val UIMODEL_COMPARATOR = object : DiffUtil.ItemCallback<UiModel>() {
            override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
                return (oldItem is UiModel.PhotoItem && newItem is UiModel.PhotoItem &&
                        oldItem.photo.id == newItem.photo.id)
            }

            override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean =
                oldItem == newItem
        }
    }
}