package com.maxmakarov.base.gallery.di

import com.maxmakarov.base.gallery.api.RetrofitBuilder
import com.maxmakarov.base.gallery.api.UnsplashApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.create
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideUnsplashApi(): UnsplashApi {
        return RetrofitBuilder().build("https://api.unsplash.com/").create()
    }
}