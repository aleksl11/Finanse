package com.example.finanse

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.finanse.dao.ExpenseDao
import com.example.finanse.dao.IncomeDao
import com.example.finanse.entities.Expense
import com.example.finanse.entities.Income

@Database(
    entities = [Income::class, Expense::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class MainDatabase: RoomDatabase() {

    abstract fun incomeDao(): IncomeDao
    abstract fun expenseDao(): ExpenseDao
}