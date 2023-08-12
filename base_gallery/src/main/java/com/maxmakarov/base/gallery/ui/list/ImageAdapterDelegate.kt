package com.maxmakarov.base.gallery.ui.list

import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import coil.load
import com.maxmakarov.base.gallery.databinding.ViewItemImageBinding
import com.maxmakarov.core.ui.BlurHashDecoder
import com.maxmakarov.core.ui.list.AdapterDelegate
import com.maxmakarov.core.ui.list.AdapterItem
import com.maxmakarov.core.ui.list.ViewHolder

class ImageAdapterDelegate(
    private val onClick: (ViewItemImageBinding, ImageAdapterItem) -> Unit
) : AdapterDelegate<ImageAdapterItem, ViewHolder<ViewItemImageBinding, ImageAdapterItem>>(
    ImageAdapterItem::class.java
) {

    override fun createViewHolder(parent: ViewGroup) = ViewHolder(
        ViewItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onClick
    )

    override fun bindViewHolder(
        item: ImageAdapterItem,
        viewHolder: ViewHolder<ViewItemImageBinding, ImageAdapterItem>,
        payloads: List<AdapterItem.Payloadable>
    ) {
        viewHolder.bind(item) {
            image.transitionName = item.image.id
            image.aspectRatio = item.image.height.toDouble() / item.image.width.toDouble()
            val bitmap = BlurHashDecoder.decode(item.image.blur_hash, 20, 20)
            image.load(item.image.urls.regular) {
                placeholder(BitmapDrawable(image.resources, bitmap))
                crossfade(true)
            }
        }
    }
}