package com.maxmakarov.base.gallery.ui.list

import android.graphics.drawable.BitmapDrawable
import coil.load
import com.maxmakarov.base.gallery.databinding.ViewItemImageBinding
import com.maxmakarov.core.ui.BlurHashDecoder
import com.maxmakarov.core.ui.list.AdapterDelegate
import com.maxmakarov.core.ui.list.ViewHolder

class ImageAdapterDelegate(
    onRootClick: (ImageAdapterItem, ViewItemImageBinding) -> Unit
) : AdapterDelegate<ImageAdapterItem, ViewItemImageBinding, ViewHolder<ImageAdapterItem, ViewItemImageBinding>>(
    ImageAdapterItem::class.java,
    ViewItemImageBinding::inflate,
    onBind = { item, viewHolder, payloads ->
        viewHolder.bind(item) {
            image.transitionName = item.image.id
            image.aspectRatio = item.image.height.toDouble() / item.image.width.toDouble()
            val bitmap = BlurHashDecoder.decode(item.image.blur_hash, 20, 20)
            image.load(item.image.urls.regular) {
                placeholder(BitmapDrawable(image.resources, bitmap))
                crossfade(true)
            }
        }
    },
    onRootClick = onRootClick
)