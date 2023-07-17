package com.maxmakarov.gallery.ui.gallery.list

import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.maxmakarov.gallery.R
import com.maxmakarov.gallery.model.UnsplashPhoto
import com.maxmakarov.gallery.ui.utils.BlurHashDecoder


//todo use ViewBinding as an argument for a convenience
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
            val bitmap = BlurHashDecoder.decode(photo.blur_hash, 20, 20)
            photoView.load(photo.urls.regular) {
                placeholder(BitmapDrawable(photoView.resources, bitmap))
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
