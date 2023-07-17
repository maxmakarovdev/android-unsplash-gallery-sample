package com.maxmakarov.gallery.core

import android.app.Application
import com.maxmakarov.gallery.BuildConfig

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Config.init(
            application = this,
            accessKey = BuildConfig.UNSPLASH_ACCESS_KEY,
            secretKey = BuildConfig.UNSPLASH_SECRET_KEY
        )
    }
}