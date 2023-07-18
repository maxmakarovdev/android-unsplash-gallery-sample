package com.maxmakarov.base.gallery.db

import com.squareup.moshi.Moshi

object MoshiFactory {
    val moshi = Moshi.Builder().build()
}