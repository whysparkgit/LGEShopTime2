package com.lge.lgshoptimem.model.dao

import androidx.room.*
import com.lge.lgshoptimem.model.dto.CategoryInfo

@Dao
interface CategoryDao
{
    @Insert
    fun insert(category: CategoryInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(category: CategoryInfo)

    @Transaction
    fun insertAll(categories: List<CategoryInfo>) = categories.forEach { insertOrReplace(it) }

    @Update
    fun update(category: CategoryInfo)

    @Delete
    fun delete(category: CategoryInfo)

    @Query("DELETE FROM CategoryInfo")
    fun deleteAll()

    @Query("SELECT * FROM CategoryInfo")
    fun selectAll(): List<CategoryInfo>

    @Query("SELECT COUNT(*) FROM CategoryInfo")
    fun getCount(): Int

//    @Query("SELECT * FROM CategoryInfo ORDER BY lgCatCd DESC LIMIT :nNum")
    @Query("SELECT * FROM CategoryInfo LIMIT :nNum")
    fun selectLast(nNum: Int): List<CategoryInfo>
}