package com.maxmakarov.core.ui.list

import android.os.Parcelable

interface AdapterItem : Parcelable {
    fun id(): Any
    fun content(): Any
    fun payload(other: Any): Payloadable = Payloadable.None

    interface Payloadable {
        object None: Payloadable
    }
}