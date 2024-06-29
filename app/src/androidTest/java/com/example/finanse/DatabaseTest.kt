package com.example.finanse

import android.content.Context
import android.graphics.Color
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.finanse.dao.CategoryDao
import com.example.finanse.dao.ExpenseDao
import com.example.finanse.dao.IncomeDao
import com.example.finanse.entities.Category
import com.example.finanse.entities.Expense
import com.example.finanse.entities.Income
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var incomeDao: IncomeDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var expenseDao: ExpenseDao
    private lateinit var db: MainDatabase

    @Before
    fun createDb(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, MainDatabase::class.java).build()
        incomeDao = db.incomeDao()
        categoryDao = db.categoryDao()
        expenseDao = db.expenseDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb(){
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeIncomeAndRead() = runBlocking{
        val income = Income(1, 10.0, "Test", LocalDate.now(), "test income")
        incomeDao.insertIncome(income)
        val list = incomeDao.getIncomesOrderedById().first()
        assertThat(list.size, equalTo(1))
        assertThat(list[0], equalTo(income))
    }

    @Test
    @Throws(Exception::class)
    fun deleteIncome() = runBlocking{
        val income = Income(1, 10.0, "Test", LocalDate.now(), "test income")
        incomeDao.insertIncome(income)
        var list = incomeDao.getIncomesOrderedById().first()
        assertThat(list.size, equalTo(1))
        incomeDao.deleteIncome(income)
        list = incomeDao.getIncomesOrderedById().first()
        assertThat(list.size, equalTo(0))
    }
    @Test
    @Throws(Exception::class)
    fun writeCategoryAndRead() = runBlocking{
        val category = Category("test", Color.MAGENTA)
        categoryDao.insertCategory(category)
        val list = categoryDao.getCategories().first()
        assertThat(list.size, equalTo(1))
        assertThat(list[0], equalTo(category))
    }

    @Test
    @Throws(Exception::class)
    fun deleteCategory() = runBlocking{
        val category = Category("test", Color.MAGENTA)
        categoryDao.insertCategory(category)
        var list = categoryDao.getCategories().first()
        assertThat(list.size, equalTo(1))
        categoryDao.deleteCategory(category)
        list = categoryDao.getCategories().first()
        assertThat(list.size, equalTo(0))
    }
    @Test
    @Throws(Exception::class)
    fun writeExpenseAndRead() = runBlocking{
        val category = Category("test", Color.MAGENTA)
        categoryDao.insertCategory(category)
        val expense = Expense(1, 10.0, "Test", LocalDate.now(), "test","test expense")
        expenseDao.insertExpense(expense)
        val list = expenseDao.getExpensesOrderedById().first()
        assertThat(list.size, equalTo(1))
        assertThat(list[0], equalTo(expense))
    }

    @Test
    @Throws(Exception::class)
    fun deleteExpense() = runBlocking{
        val category = Category("test", Color.MAGENTA)
        categoryDao.insertCategory(category)
        val expense = Expense(1, 10.0, "Test", LocalDate.now(), "test","test expense")
        expenseDao.insertExpense(expense)
        var list = expenseDao.getExpensesOrderedById().first()
        assertThat(list.size, equalTo(1))
        expenseDao.deleteExpense(expense)
        list = expenseDao.getExpensesOrderedById().first()
        assertThat(list.size, equalTo(0))
    }
}