package com.lge.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lge.lgshoptimem.model.dao.CategoryDao
import com.lge.lgshoptimem.model.dao.VideoDao
import com.lge.lgshoptimem.model.dto.CategoryInfo
import com.lge.lgshoptimem.model.dto.Video

@Database(entities = arrayOf(Video::class, CategoryInfo::class), version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getVideoDao(): VideoDao

    abstract fun getCategoryDao(): CategoryDao
}