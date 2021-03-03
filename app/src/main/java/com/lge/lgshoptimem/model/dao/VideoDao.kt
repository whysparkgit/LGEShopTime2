package com.lge.lgshoptimem.model.dao

import androidx.room.*
import com.lge.lgshoptimem.model.dto.CategoryInfo
import com.lge.lgshoptimem.model.dto.Video

@Dao
interface VideoDao
{
    @Insert
    fun insert(video: Video)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(video: Video)

    @Update
    fun update(video: Video)

    @Delete
    fun delete(video: Video)

    @Query("SELECT * FROM Video")
    fun selectAll(): List<Video>

    @Query("SELECT COUNT(*) FROM Video")
    fun getCount(): Int

    @Query("SELECT * FROM Video ORDER BY 'index' DESC LIMIT :nNum")
    fun selectLast(nNum: Int): List<Video>
}