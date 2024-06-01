package com.example.finanse.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.finanse.entities.Income
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {
    @Insert
    suspend fun insertIncome(income: Income)

    @Delete
    suspend fun deleteIncome(income: Income)

    @Query("SELECT * FROM income ORDER BY id ASC")
    fun getIncomesOrderedById(): Flow<List<Income>>

    @Query("SELECT * FROM income ORDER BY date ASC")
    fun getIncomesOrderedByDate(): Flow<List<Income>>

    @Query("SELECT * FROM income ORDER BY amount ASC")
    fun getIncomesOrderedByAmount(): Flow<List<Income>>
}