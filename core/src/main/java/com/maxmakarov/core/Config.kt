package com.maxmakarov.core

import android.app.Application

object Config {
    lateinit var application: Application
        private set

    lateinit var accessKey: String
        private set
    lateinit var secretKey: String
        private set

    val isLoggingEnabled = true

    fun init(
        application: Application,
        accessKey: String,
        secretKey: String
    ) {
        this.application = application
        this.accessKey = accessKey
        this.secretKey = secretKey
    }
}