package com.example.finanse.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.finanse.entities.Expense
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ExpenseDao {
    @Upsert
    suspend fun insertExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT title FROM expense where id = :id ")
    fun getTitle(id: Int): String
    @Query("SELECT date FROM expense where id = :id ")
    fun getDate(id: Int): LocalDate
    @Query("SELECT amount FROM expense where id = :id ")
    fun getAmount(id: Int): Double
    @Query("SELECT category FROM expense where id = :id ")
    fun getCategory(id: Int): String
    @Query("SELECT account FROM expense where id = :id ")
    fun getAccount(id: Int): Int
    @Query("SELECT description FROM expense where id = :id ")
    fun getDescription(id: Int): String
    @Query("SELECT photos FROM expense where id = :id ")
    fun getPhotos(id: Int): String

    @Query("SELECT * FROM expense ORDER BY id ASC")
    fun getExpensesOrderedById(): Flow<List<Expense>>

    @Query("SELECT * FROM expense ORDER BY date ASC")
    fun getExpensesOrderedByDate(): Flow<List<Expense>>

    @Query("SELECT * FROM expense ORDER BY amount ASC")
    fun getExpensesOrderedByAmount(): Flow<List<Expense>>

    @Query("SELECT * FROM expense ORDER BY category ASC")
    fun getExpensesOrderedByCategory(): Flow<List<Expense>>
}