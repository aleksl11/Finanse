package com.example.finanse

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.finanse.dao.AccountDao
import com.example.finanse.dao.CategoryDao
import com.example.finanse.dao.ExpenseDao
import com.example.finanse.dao.IncomeDao
import com.example.finanse.entities.Account
import com.example.finanse.entities.Category
import com.example.finanse.entities.Expense
import com.example.finanse.entities.Income
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Income::class, Expense::class, Category::class, Account::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class MainDatabase: RoomDatabase() {

    abstract fun incomeDao(): IncomeDao
    abstract fun accountDao(): AccountDao
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
            ).addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        getInstance(context).categoryDao().insertFirstCategories(
                            listOf(
                                Category("Bills", Color.Red.hashCode()),
                                Category("Groceries", Color.Green.hashCode()),
                                Category("Transport", Color.Blue.hashCode()),
                                Category("Entertainment", Color.Magenta.hashCode()),
                                Category("Other", Color.Yellow.hashCode())
                            )
                        )
                        getInstance(context).accountDao().insertAccount(Account(1,"Wallet",0.0))
                    }
                }
            }).build()
    }
}