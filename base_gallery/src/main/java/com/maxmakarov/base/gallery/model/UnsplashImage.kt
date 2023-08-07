package com.maxmakarov.base.gallery.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "favorites")
data class UnsplashImage(
    @PrimaryKey val id: String,
    val created_at: String,
    val width: Int,
    val height: Int,
    val color: String?,
    val blur_hash: String,
    val likes: Int,
    val description: String?,
    val urls: UnsplashUrls,
    val links: UnsplashLinks,
    val user: UnsplashUser,
    val savedTimestamp: Long? = null
) : Parcelable