package com.maxmakarov.gallery.ui.gallery.list

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.maxmakarov.gallery.R
import com.maxmakarov.gallery.model.UnsplashPhoto

//todo use ViewBinding as an argument for a convinience
class PhotoViewHolder(view: View, callback: PhotoClickCallback) : RecyclerView.ViewHolder(view) {
    private val photoView: AspectRatioImageView = view.findViewById(R.id.photo)
    private val photographerView: TextView = view.findViewById(R.id.photographer)
    private var photo: UnsplashPhoto? = null

    init {
        view.setOnClickListener {
            photo?.also { callback.onClick(it) }
        }
        view.setOnLongClickListener {
            photo?.also { callback.onLongClick(it) }
            true
        }
    }

    fun bind(photo: UnsplashPhoto?) {
        if (photo == null) {
            photographerView.visibility = View.GONE
        } else {
            this.photo = photo

            photoView.aspectRatio = photo.height.toDouble() / photo.width.toDouble()
            photoView.setBackgroundColor(Color.parseColor(photo.color))
            photoView.load(photo.urls.small) {
                crossfade(true)
            }

            photographerView.isVisible = true
            photographerView.text = photo.user.name
        }
    }

    companion object {
        fun create(parent: ViewGroup, callback: PhotoClickCallback): PhotoViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_item_photo, parent, false)
            return PhotoViewHolder(view, callback)
        }
    }

    interface PhotoClickCallback {
        fun onClick(photo: UnsplashPhoto)
        fun onLongClick(photo: UnsplashPhoto)
    }
}
