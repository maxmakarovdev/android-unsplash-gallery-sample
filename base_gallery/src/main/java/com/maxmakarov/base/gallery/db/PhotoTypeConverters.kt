package com.maxmakarov.base.gallery.db

import androidx.room.TypeConverter
import com.maxmakarov.base.gallery.model.UnsplashLinks
import com.maxmakarov.base.gallery.model.UnsplashUrls
import com.maxmakarov.base.gallery.model.UnsplashUser

class UnsplashUrlsConverter {
    @TypeConverter
    fun fromJson(string: String): UnsplashUrls {
        return MoshiFactory.moshi.adapter(UnsplashUrls::class.java).fromJson(string) ?: EMPTY_URLS
    }

    @TypeConverter
    fun toJson(user: UnsplashUrls): String {
        return MoshiFactory.moshi.adapter(UnsplashUrls::class.java).toJson(user)
    }
}

class UnsplashLinksConverter {
    @TypeConverter
    fun fromJson(string: String): UnsplashLinks {
        return MoshiFactory.moshi.adapter(UnsplashLinks::class.java).fromJson(string) ?: EMPTY_LINKS
    }

    @TypeConverter
    fun toJson(user: UnsplashLinks): String {
        return MoshiFactory.moshi.adapter(UnsplashLinks::class.java).toJson(user)
    }
}

class UnsplashUserConverter {
    @TypeConverter
    fun fromJson(string: String): UnsplashUser {
        return MoshiFactory.moshi.adapter(UnsplashUser::class.java).fromJson(string) ?: EMPTY_USER
    }

    @TypeConverter
    fun toJson(user: UnsplashUser): String {
        return MoshiFactory.moshi.adapter(UnsplashUser::class.java).toJson(user)
    }
}

private val EMPTY_URLS = UnsplashUrls("", "", "", "", "", "", "")
private val EMPTY_LINKS = UnsplashLinks("", "", "", "", "", "", "")
private val EMPTY_USER = UnsplashUser("", "", "", "", "", "", 0, 0, 0, EMPTY_URLS, EMPTY_LINKS)