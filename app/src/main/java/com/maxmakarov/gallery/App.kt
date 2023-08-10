package com.maxmakarov.gallery

import android.app.Application
import com.maxmakarov.core.Config
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
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