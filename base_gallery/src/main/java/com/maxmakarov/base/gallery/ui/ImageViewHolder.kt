package com.maxmakarov.base.gallery.ui

import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.maxmakarov.base.gallery.R
import com.maxmakarov.base.gallery.model.UnsplashImage
import com.maxmakarov.core.ui.AspectRatioImageView
import com.maxmakarov.core.ui.BlurHashDecoder


class ImageViewHolder(view: View, callback: ImageClickCallback) : RecyclerView.ViewHolder(view) {
    private val imageView: AspectRatioImageView = view.findViewById(R.id.image)
    private var image: UnsplashImage? = null

    init {
        view.setOnClickListener {
            image?.also { callback.onClick(imageView, it) }
        }
    }

    fun bind(image: UnsplashImage?) {
        if (image != null) {
            this.image = image

            imageView.transitionName = image.id
            imageView.aspectRatio = image.height.toDouble() / image.width.toDouble()
            val bitmap = BlurHashDecoder.decode(image.blur_hash, 20, 20)
            imageView.load(image.urls.regular) {
                placeholder(BitmapDrawable(imageView.resources, bitmap))
                crossfade(true)
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup, callback: ImageClickCallback): ImageViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_item_image, parent, false)
            return ImageViewHolder(view, callback)
        }
    }

    interface ImageClickCallback {
        fun onClick(imageView: View, image: UnsplashImage)
    }
}
