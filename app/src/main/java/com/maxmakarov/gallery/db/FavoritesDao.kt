package com.maxmakarov.gallery.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maxmakarov.gallery.model.UnsplashPhoto

@Dao
interface FavoritesDao {
    @Query("SELECT * FROM favorites") //todo order by adding time desc
    fun getFavorites(): PagingSource<Int, UnsplashPhoto>

    @Query("SELECT EXISTS (SELECT 1 FROM favorites WHERE id = :photoId)")
    suspend fun checkPhotoIsAdded(photoId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(photo: UnsplashPhoto)

    @Query("DELETE FROM favorites WHERE id = :photoId")
    suspend fun removeFromFavorites(photoId: String)
}