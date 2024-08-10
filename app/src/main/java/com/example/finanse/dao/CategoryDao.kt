package com.example.finanse.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.finanse.entities.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Upsert
    suspend fun insertCategory(category: Category)
    @Insert
    suspend fun insertFirstCategories(categories: List<Category>)
    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT color FROM category where name = :name")
    fun getColor(name: String): Int

    @Query("SELECT * FROM category")
    fun getCategories(): Flow<List<Category>>

    @Query("SELECT * FROM category ORDER BY name ASC")
    fun getCategoriesOrderedByName(): Flow<List<Category>>

    @Query("SELECT color FROM category WHERE name = :name ")
    fun getColorValue(name: String): Int
}