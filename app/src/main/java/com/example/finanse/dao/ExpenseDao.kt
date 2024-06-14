package com.example.finanse.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.finanse.entities.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insertExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expense ORDER BY id ASC")
    fun getExpensesOrderedById(): Flow<List<Expense>>

    @Query("SELECT * FROM expense ORDER BY date ASC")
    fun getExpensesOrderedByDate(): Flow<List<Expense>>

    @Query("SELECT * FROM expense ORDER BY amount ASC")
    fun getExpensesOrderedByAmount(): Flow<List<Expense>>

    @Query("SELECT * FROM expense ORDER BY category ASC")
    fun getExpensesOrderedByCategory(): Flow<List<Expense>>
}