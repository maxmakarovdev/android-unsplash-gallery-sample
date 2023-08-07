package com.maxmakarov.core.ui

import android.view.View
import androidx.appcompat.app.AppCompatActivity

abstract class BaseNavActivity : AppCompatActivity() {
    abstract fun setFavouritesBadge(isVisible: Boolean)
    abstract fun getBottomNavView(): View
}