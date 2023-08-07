package com.maxmakarov.base.gallery.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.maxmakarov.base.gallery.model.UnsplashImage

@Database(entities = [UnsplashImage::class], version = 1, exportSchema = false)
@TypeConverters(value = [UnsplashUrlsConverter::class, UnsplashLinksConverter::class, UnsplashUserConverter::class])
abstract class ImageDatabase : RoomDatabase() {

    abstract fun favoritesDao(): FavoritesDao

    companion object {
        @Volatile private var INSTANCE: ImageDatabase? = null

        fun getInstance(context: Context): ImageDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, ImageDatabase::class.java, "images.db")
                .build()
    }
}