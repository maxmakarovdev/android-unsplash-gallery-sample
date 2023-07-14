package com.maxmakarov.gallery.core

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Config.init(
            application = this,
            accessKey = "", //todo move it out of here
            secretKey = ""
        )
    }
}