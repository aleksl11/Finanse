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
import java.time.LocalDate

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

                        //test; delete after
                        getInstance(context).accountDao().insertAccount(Account(2,"Bank",1000.0))

                        //test expenses; delete after
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 200.0, account = 1, title = "test", date = LocalDate.of(2024,6,1), category = "Groceries") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 100.0, account = 1, title = "test", date = LocalDate.of(2024,7,1), category = "Groceries") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 25.0, account = 1, title = "test", date = LocalDate.of(2024,8,1), category = "Transport") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 23.15, account = 2, title = "test", date = LocalDate.of(2024,8,1), category = "Entertainment") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 42.12, account = 1, title = "test", date = LocalDate.of(2024,8,1), category = "Entertainment") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 20.0, account = 1, title = "test", date = LocalDate.of(2024,8,1), category = "Transport") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 345.99, account = 1, title = "test", date = LocalDate.of(2024,5,1), category = "Other") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 2200.0, account = 2, title = "test", date = LocalDate.of(2024,5,1), category = "Bills") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 212.10, account = 1, title = "test", date = LocalDate.of(2024,7,1), category = "Groceries") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 1200.0, account = 1, title = "test", date = LocalDate.of(2024,9,1), category = "Bills") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 30.70, account = 1, title = "test", date = LocalDate.of(2024,2,1), category = "Transport") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 123.12, account = 1, title = "test", date = LocalDate.of(2024,2,1), category = "Groceries") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 1.15, account = 2, title = "test", date = LocalDate.of(2024,2,1), category = "Other") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 45.54, account = 2, title = "test", date = LocalDate.of(2024,9,1), category = "Groceries") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 65.78, account = 2, title = "test", date = LocalDate.of(2024,2,1), category = "Other") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 324.23, account = 1, title = "test", date = LocalDate.of(2024,5,1), category = "Bills") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 120.0, account = 2, title = "test", date = LocalDate.of(2024,7,1), category = "Entertainment") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 80.0, account = 1, title = "test", date = LocalDate.of(2024,7,1), category = "Groceries") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 212.30, account = 2, title = "test", date = LocalDate.of(2024,6,1), category = "Transport") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 12.0, account = 1, title = "test", date = LocalDate.of(2024,7,1), category = "Other") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 54.0, account = 2, title = "test", date = LocalDate.of(2024,8,1), category = "Groceries") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 432.0, account = 2, title = "test", date = LocalDate.of(2024,6,1), category = "Entertainment") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 342.0, account = 2, title = "test", date = LocalDate.of(2024,6,1), category = "Other") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 1111.11, account = 2, title = "test", date = LocalDate.of(2024,3,1), category = "Bills") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 321.0, account = 2, title = "test", date = LocalDate.of(2024,3,1), category = "Groceries") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 543.0, account = 2, title = "test", date = LocalDate.of(2024,6,1), category = "Bills") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 231.0, account = 1, title = "test", date = LocalDate.of(2024,1,1), category = "Transport") )
                        getInstance(context).expenseDao().insertExpense(Expense(amount = 99.99, account = 2, title = "test", date = LocalDate.of(2023,6,1), category = "Groceries") )

                        //test incomes; delete after
                        getInstance(context).incomeDao().insertIncome(Income(amount = 300.0, title="test", account = 1, date=LocalDate.of(2024,5,1)))
                        getInstance(context).incomeDao().insertIncome(Income(amount = 3250.0, title = "test2", account = 1, date = LocalDate.of(2024, 8, 1)))
                        getInstance(context).incomeDao().insertIncome(Income(amount = 500.0, title = "salary", account = 1, date = LocalDate.of(2024, 9, 1)))
                        getInstance(context).incomeDao().insertIncome(Income(amount = 1000.0, title = "freelance", account = 2, date = LocalDate.of(2024, 9, 15)))
                        getInstance(context).incomeDao().insertIncome(Income(amount = 200.0, title = "gift", account = 2, date = LocalDate.of(2024, 8, 20)))
                        getInstance(context).incomeDao().insertIncome(Income(amount = 750.0, title = "bonus", account = 1, date = LocalDate.of(2024, 6, 5)))
                        getInstance(context).incomeDao().insertIncome(Income(amount = 1200.0, title = "investment", account = 2, date = LocalDate.of(2024, 6, 10)))
                        getInstance(context).incomeDao().insertIncome(Income(amount = 300.0, title = "dividends", account = 2, date = LocalDate.of(2024, 7, 1)))
                        getInstance(context).incomeDao().insertIncome(Income(amount = 450.0, title = "side project", account = 1, date = LocalDate.of(2024, 8, 20)))
                        getInstance(context).incomeDao().insertIncome(Income(amount = 600.0, title = "rental income", account = 2, date = LocalDate.of(2024, 9, 25)))
                        getInstance(context).incomeDao().insertIncome(Income(amount = 950.0, title = "consulting", account = 1, date = LocalDate.of(2024, 7, 15)))
                    }
                }
            }).build()
    }
}