package com.maxmakarov.base.gallery.ui

import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.maxmakarov.base.gallery.R
import com.maxmakarov.base.gallery.model.UnsplashPhoto
import com.maxmakarov.core.ui.AspectRatioImageView
import com.maxmakarov.core.ui.BlurHashDecoder


class PhotoViewHolder(view: View, callback: PhotoClickCallback) : RecyclerView.ViewHolder(view) {
    private val photoView: AspectRatioImageView = view.findViewById(R.id.photo)
    private var photo: UnsplashPhoto? = null

    init {
        view.setOnClickListener {
            photo?.also { callback.onClick(photoView, it) }
        }
    }

    fun bind(photo: UnsplashPhoto?) {
        if (photo != null) {
            this.photo = photo

            photoView.transitionName = photo.id
            photoView.aspectRatio = photo.height.toDouble() / photo.width.toDouble()
            val bitmap = BlurHashDecoder.decode(photo.blur_hash, 20, 20)
            photoView.load(photo.urls.regular) {
                placeholder(BitmapDrawable(photoView.resources, bitmap))
                crossfade(true)
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup, callback: PhotoClickCallback): PhotoViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_item_photo, parent, false)
            return PhotoViewHolder(view, callback)
        }
    }

    interface PhotoClickCallback {
        fun onClick(imageView: View, photo: UnsplashPhoto)
    }
}
