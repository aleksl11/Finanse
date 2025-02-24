package com.example.finanse

import android.graphics.Color
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.finanse.dao.AccountDao
import com.example.finanse.dao.CategoryDao
import com.example.finanse.dao.ExpenseDao
import com.example.finanse.dao.IncomeDao
import com.example.finanse.entities.Account
import com.example.finanse.entities.Category
import com.example.finanse.entities.Expense
import com.example.finanse.entities.Income
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var database: MainDatabase
    private lateinit var expenseDao: ExpenseDao
    private lateinit var accountDao: AccountDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var incomeDao: IncomeDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MainDatabase::class.java
        ).allowMainThreadQueries().build()


        expenseDao = database.expenseDao()
        accountDao = database.accountDao()
        categoryDao = database.categoryDao()
        incomeDao = database.incomeDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAccount_succeeds() = runBlocking {
        val account = Account(id = 1, name = "Savings", balance = 1000.0)

        accountDao.insertAccount(account)
        val retrievedAccounts = accountDao.getAccounts().first()

        assertTrue(retrievedAccounts.isNotEmpty())
    }

    @Test
    fun getCorrectValues_fromAccount() = runBlocking {
        val account = Account(id = 1, name = "Savings", balance = 1000.0)

        accountDao.insertAccount(account)

        val name = accountDao.getName(1)
        val balance = accountDao.getBalance(1)

        assertTrue(name == "Savings")
        assertTrue(balance == 1000)
    }
    @Test
    fun deleteAccount_removesItFromDatabase() = runBlocking {
        val account = Account(id = 1, name = "Savings", balance = 1000.0)

        accountDao.insertAccount(account)
        accountDao.deleteAccount(account)
        val retrievedAccounts = accountDao.getAccounts().first()

        assertTrue(retrievedAccounts.isEmpty())
    }

    @Test
    fun insertCategory_succeeds() = runBlocking {
        val category = Category(name = "Groceries", color = Color.GREEN)

        categoryDao.insertCategory(category)
        val retrievedCategories = categoryDao.getCategories().first()

        assertTrue(retrievedCategories.isNotEmpty())
    }

    @Test
    fun getCorrectValues_fromCategory() = runBlocking {
        val category = Category(name = "Groceries", color = Color.GREEN)

        categoryDao.insertCategory(category)

        val color = categoryDao.getColor("Groceries")

        assertTrue(color == Color.GREEN)
    }

    @Test
    fun deleteCategory_removesItFromDatabase() = runBlocking {
        val category = Category(name = "Groceries", color = Color.GREEN)

        categoryDao.insertCategory(category)
        categoryDao.deleteCategory(category)
        val retrievedCategories = categoryDao.getCategories().first()

        assertTrue(retrievedCategories.isEmpty())
    }

    @Test
    fun insertExpense_withoutCategoryOrAccount_fails() = runBlocking {
        val expense = Expense(
            id = 1,
            title = "Dinner",
            date = LocalDate.of(2024, 2, 20),
            amount = 30.0,
            category = "Food",
            account = 101,
            description = "Dinner at a restaurant",
            photos = "receipt.jpg"
        )

        var exceptionThrown = false
        try {
            expenseDao.insertExpense(expense)
        } catch (e: Exception) {
            exceptionThrown = true
        }

        assertTrue(exceptionThrown)
    }

    @Test
    fun insertExpense_withValidCategoryAndAccount_succeeds() = runBlocking {
        val account = Account(id = 101, name = "Bank Account", balance = 500.0)
        val category = Category(name = "Food", color = Color.RED)

        accountDao.insertAccount(account)
        categoryDao.insertCategory(category)

        val expense = Expense(
            id = 1,
            title = "Lunch",
            date = LocalDate.of(2024, 2, 21),
            amount = 20.0,
            category = "Food",
            account = 101,
            description = "Lunch at a restaurant",
            photos = "receipt.jpg"
        )

        expenseDao.insertExpense(expense)
        val retrievedExpenses = expenseDao.getExpensesOrderedById()

        assertNotNull(retrievedExpenses)
    }

    @Test
    fun getCorrectValues_fromExpense() = runBlocking {
        val accountEntry = Account(id = 101, name = "Bank Account", balance = 500.0)
        val categoryEntry = Category(name = "Food", color = Color.RED)

        accountDao.insertAccount(accountEntry)
        categoryDao.insertCategory(categoryEntry)

        val expense = Expense(
            id = 1,
            title = "Lunch",
            date = LocalDate.of(2024, 2, 21),
            amount = 20.0,
            category = "Food",
            account = 101,
            description = "Lunch at a restaurant",
            photos = "receipt.jpg"
        )

        expenseDao.insertExpense(expense)

        val title = expenseDao.getTitle(1)
        val date = expenseDao.getDate(1)
        val amount = expenseDao.getAmount(1)
        val category = expenseDao.getCategory(1)
        val account = expenseDao.getAccount(1)
        val description = expenseDao.getDescription(1)
        val photos = expenseDao.getPhotos(1)

        assertTrue(title == "Lunch")
        assertTrue(date == LocalDate.of(2024, 2, 21))
        assertTrue(amount == 20.0)
        assertTrue(category == "Food")
        assertTrue(account == 101)
        assertTrue(description == "Lunch at a restaurant")
        assertTrue(photos == "receipt.jpg")
    }
    @Test
    fun deleteExpense() = runBlocking {
        val accountEntry = Account(id = 101, name = "Bank Account", balance = 500.0)
        val categoryEntry = Category(name = "Food", color = Color.RED)

        accountDao.insertAccount(accountEntry)
        categoryDao.insertCategory(categoryEntry)

        val expense = Expense(
            id = 1,
            title = "Lunch",
            date = LocalDate.of(2024, 2, 21),
            amount = 20.0,
            category = "Food",
            account = 101,
            description = "Lunch at a restaurant",
            photos = "receipt.jpg"
        )

        expenseDao.insertExpense(expense)

        val addedExpenses = expenseDao.getExpensesOrderedById().first()

        assertFalse(addedExpenses.isEmpty())

        expenseDao.deleteExpense(expense)
        val retrievedExpenses = expenseDao.getExpensesOrderedById().first()

        assertTrue(retrievedExpenses.isEmpty())
    }

    @Test
    fun insertIncome_withoutAccount_fails() = runBlocking {
        val income = Income(
            id = 1,
            title = "Salary",
            date = LocalDate.of(2024, 2, 20),
            amount = 2000.0,
            account = 101,
            description = "Monthly salary",
            photos = "payslip.jpg"
        )

        var exceptionThrown = false
        try {
            incomeDao.insertIncome(income)
        } catch (e: Exception) {
            exceptionThrown = true
        }

        assertTrue(exceptionThrown)
    }

    @Test
    fun insertIncome_withValidAccount_succeeds() = runBlocking {
        val account = Account(id = 101, name = "Bank Account", balance = 500.0)
        accountDao.insertAccount(account)

        val income = Income(
            id = 1,
            title = "Salary",
            date = LocalDate.of(2024, 2, 21),
            amount = 2000.0,
            account = 101,
            description = "Monthly salary",
            photos = "payslip.jpg"
        )

        incomeDao.insertIncome(income)
        val retrievedIncomes = incomeDao.getIncomesOrderedById().first()

        assertNotNull(retrievedIncomes)
    }

    @Test
    fun getCorrectValues_fromIncome() = runBlocking {
        val accountEntry = Account(id = 101, name = "Bank Account", balance = 500.0)
        accountDao.insertAccount(accountEntry)

        val income = Income(
            id = 1,
            title = "Salary",
            date = LocalDate.of(2024, 2, 21),
            amount = 2000.0,
            account = 101,
            description = "Monthly salary",
            photos = "payslip.jpg"
        )

        incomeDao.insertIncome(income)

        val title = incomeDao.getTitle(1)
        val date = incomeDao.getDate(1)
        val amount = incomeDao.getAmount(1)
        val account = incomeDao.getAccount(1)
        val description = incomeDao.getDescription(1)
        val photos = incomeDao.getPhotos(1)

        assertEquals("Salary", title)
        assertEquals(LocalDate.of(2024, 2, 21), date)
        assertEquals(2000.0, amount, 0.001)
        assertEquals(101, account)
        assertEquals("Monthly salary", description)
        assertEquals("payslip.jpg", photos)
    }

    @Test
    fun deleteIncome() = runBlocking {
        val accountEntry = Account(id = 101, name = "Bank Account", balance = 500.0)
        accountDao.insertAccount(accountEntry)

        val income = Income(
            id = 1,
            title = "Salary",
            date = LocalDate.of(2024, 2, 21),
            amount = 2000.0,
            account = 101,
            description = "Monthly salary",
            photos = "payslip.jpg"
        )

        incomeDao.insertIncome(income)

        val addedIncomes = incomeDao.getIncomesOrderedById().first()
        assertFalse(addedIncomes.isEmpty())

        incomeDao.deleteIncome(income)
        val retrievedIncomes = incomeDao.getIncomesOrderedById().first()

        assertTrue(retrievedIncomes.isEmpty())
    }

    @Test
    fun deleteAccount_cascadesToExpense() = runBlocking {
        val account = Account(id = 101, name = "Bank Account", balance = 500.0)
        accountDao.insertAccount(account)

        val category = Category(name = "Food", color = Color.RED)
        categoryDao.insertCategory(category)

        val expense = Expense(
            id = 1,
            title = "Dinner",
            date = LocalDate.of(2024, 2, 20),
            amount = 30.0,
            category = "Food",
            account = 101,
            description = "Dinner at a restaurant",
            photos = "receipt.jpg"
        )
        expenseDao.insertExpense(expense)

        val expensesBeforeDelete = expenseDao.getExpensesOrderedById().first()
        assertFalse(expensesBeforeDelete.isEmpty())

        accountDao.deleteAccount(account)

        val expensesAfterDelete = expenseDao.getExpensesOrderedById().first()
        assertTrue(expensesAfterDelete.isEmpty())
    }

    @Test
    fun deleteAccount_cascadesIncome() = runBlocking {
        val account = Account(id = 101, name = "Bank Account", balance = 500.0)
        accountDao.insertAccount(account)

        val income = Income(
            id = 2,
            title = "Salary",
            date = LocalDate.of(2024, 2, 21),
            amount = 2000.0,
            account = 101,
            description = "Monthly salary",
            photos = "payslip.jpg"
        )
        incomeDao.insertIncome(income)

        val incomesBeforeDelete = incomeDao.getIncomesOrderedById().first()
        assertFalse(incomesBeforeDelete.isEmpty())

        accountDao.deleteAccount(account)

        val incomesAfterDelete = incomeDao.getIncomesOrderedById().first()
        assertTrue(incomesAfterDelete.isEmpty())
    }

    @Test
    fun deleteCategory_cascadesExpense() = runBlocking {
        val account = Account(id = 101, name = "Bank Account", balance = 500.0)
        accountDao.insertAccount(account)

        val defaultCategory = Category(name = "Other", color = Color.YELLOW)
        categoryDao.insertCategory(defaultCategory)

        val category = Category(name = "Food", color = Color.RED)
        categoryDao.insertCategory(category)

        val expense = Expense(
            id = 1,
            title = "Dinner",
            date = LocalDate.of(2024, 2, 20),
            amount = 30.0,
            category = "Food",
            account = 101,
            description = "Dinner at a restaurant",
            photos = "receipt.jpg"
        )
        expenseDao.insertExpense(expense)

        val expensesBeforeDelete = expenseDao.getExpensesOrderedById().first()
        assertFalse(expensesBeforeDelete.isEmpty())
        assertEquals("Food", expensesBeforeDelete.first().category)

        categoryDao.deleteCategory(category)

        val expensesAfterDelete = expenseDao.getExpensesOrderedById().first()
        assertFalse(expensesAfterDelete.isEmpty())
        assertEquals("Other", expensesAfterDelete.first().category)
    }
}
