package com.example.finanse.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.finanse.entities.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert
    suspend fun insertCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM category ORDER BY id ASC")
    fun getCategoriesOrderedById(): Flow<List<Category>>

    @Query("SELECT * FROM category ORDER BY name ASC")
    fun getCategoriesOrderedByName(): Flow<List<Category>>

    @Query("SELECT name FROM category where id = :id")
    fun getNameFromId(id: Int): String
}