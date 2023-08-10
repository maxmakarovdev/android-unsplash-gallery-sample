package com.maxmakarov.base.gallery.di

import com.maxmakarov.base.gallery.api.UnsplashApi
import com.maxmakarov.base.gallery.data.ImagesRepository
import com.maxmakarov.base.gallery.data.ImagesRepositoryImpl
import com.maxmakarov.base.gallery.db.ImageDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(api: UnsplashApi, database: ImageDatabase): ImagesRepository {
        return ImagesRepositoryImpl(api, database)
    }
}