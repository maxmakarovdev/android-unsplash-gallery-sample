package com.maxmakarov.base.gallery.ui.list

import com.maxmakarov.base.gallery.model.UnsplashImage
import com.maxmakarov.core.ui.list.AdapterItem
import kotlinx.parcelize.Parcelize

@Parcelize
class ImageAdapterItem(
    val image: UnsplashImage
): AdapterItem {
    override fun id(): Any = image.id
    override fun content(): Any = image.urls.raw!!
}