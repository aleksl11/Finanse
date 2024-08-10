package com.example.finanse.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.finanse.entities.Income
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface IncomeDao {
    @Upsert
    suspend fun insertIncome(income: Income)

    @Delete
    suspend fun deleteIncome(income: Income)

    @Query("SELECT title FROM income where id = :id ")
    fun getTitle(id: Int): String
    @Query("SELECT date FROM income where id = :id ")
    fun getDate(id: Int): LocalDate
    @Query("SELECT amount FROM income where id = :id ")
    fun getAmount(id: Int): Double
    @Query("SELECT description FROM income where id = :id ")
    fun getDescription(id: Int): String

    @Query("SELECT * FROM income ORDER BY id ASC")
    fun getIncomesOrderedById(): Flow<List<Income>>

    @Query("SELECT * FROM income ORDER BY date ASC")
    fun getIncomesOrderedByDate(): Flow<List<Income>>

    @Query("SELECT * FROM income ORDER BY amount ASC")
    fun getIncomesOrderedByAmount(): Flow<List<Income>>
}