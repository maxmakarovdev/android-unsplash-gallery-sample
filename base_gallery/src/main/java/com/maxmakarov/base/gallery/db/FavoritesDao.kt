package com.maxmakarov.base.gallery.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maxmakarov.base.gallery.model.UnsplashImage
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {
    @Query("SELECT * FROM favorites ORDER BY savedTimestamp DESC")
    fun getFavorites(): PagingSource<Int, UnsplashImage>

    @Query("SELECT EXISTS (SELECT 1 FROM favorites WHERE id = :imageId)")
    fun checkIsAdded(imageId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(image: UnsplashImage)

    @Query("DELETE FROM favorites WHERE id = :imageId")
    suspend fun removeFromFavorites(imageId: String)
}