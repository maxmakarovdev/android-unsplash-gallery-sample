package com.maxmakarov.base.gallery.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.maxmakarov.base.gallery.model.UnsplashImage

@Database(entities = [UnsplashImage::class], version = 1, exportSchema = false)
@TypeConverters(value = [UnsplashUrlsConverter::class, UnsplashLinksConverter::class, UnsplashUserConverter::class])
abstract class ImageDatabase : RoomDatabase() {

    abstract fun favoritesDao(): FavoritesDao
}