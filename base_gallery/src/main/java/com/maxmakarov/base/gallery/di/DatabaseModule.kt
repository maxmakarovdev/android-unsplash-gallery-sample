package com.maxmakarov.base.gallery.di

import android.content.Context
import androidx.room.Room
import com.maxmakarov.base.gallery.db.ImageDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context): ImageDatabase {
        return Room.databaseBuilder(appContext, ImageDatabase::class.java, "images.db")
            .build()
    }
}