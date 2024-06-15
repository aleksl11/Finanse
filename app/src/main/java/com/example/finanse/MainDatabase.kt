package com.example.finanse

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.finanse.dao.CategoryDao
import com.example.finanse.dao.ExpenseDao
import com.example.finanse.dao.IncomeDao
import com.example.finanse.entities.Expense
import com.example.finanse.entities.Income
import com.example.finanse.entities.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Income::class, Expense::class, Category::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class MainDatabase: RoomDatabase() {

    abstract fun incomeDao(): IncomeDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile private var instance: MainDatabase? = null

        fun getInstance(context: Context): MainDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                MainDatabase::class.java, "main.db"
            ).addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        getInstance(context).categoryDao().insertFirstCategories(
                            listOf(
                                Category(1, "Bills"),
                                Category(2, "Groceries"),
                                Category(3, "Transport"),
                                Category(4, "Entertainment"),
                                Category(5, "Other")
                            )
                        )
                    }
                }
            }).build()
    }
}