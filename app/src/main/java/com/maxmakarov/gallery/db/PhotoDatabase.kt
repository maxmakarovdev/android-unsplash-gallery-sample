package com.maxmakarov.gallery.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.maxmakarov.gallery.model.UnsplashPhoto

@Database(entities = [UnsplashPhoto::class], version = 1, exportSchema = false)
@TypeConverters(value = [UnsplashUrlsConverter::class, UnsplashLinksConverter::class, UnsplashUserConverter::class])
abstract class PhotoDatabase : RoomDatabase() {

    abstract fun favoritesDao(): FavoritesDao

    companion object {

        @Volatile private var INSTANCE: PhotoDatabase? = null

        fun getInstance(context: Context): PhotoDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, PhotoDatabase::class.java, "photo.db")
                .build()
    }
}