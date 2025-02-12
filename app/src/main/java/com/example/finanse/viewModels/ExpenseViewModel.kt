package com.example.finanse.viewModels

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanse.InternalStorage
import com.example.finanse.dao.AccountDao
import com.example.finanse.dao.ExpenseDao
import com.example.finanse.entities.Expense
import com.example.finanse.events.ExpenseEvent
import com.example.finanse.sortTypes.ExpenseSortType
import com.example.finanse.states.AlbumState
import com.example.finanse.states.ExpenseState
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ExpenseViewModel(
    private val dao: ExpenseDao,
    private val accountDao: AccountDao
): ViewModel() {

    private val _expenseSortType = MutableStateFlow(ExpenseSortType.DATE_ADDED)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _expenses = _expenseSortType
        .flatMapLatest { expenseSortType ->
            when(expenseSortType){
                ExpenseSortType.DATE_ADDED ->dao.getExpensesOrderedById()
                ExpenseSortType.AMOUNT -> dao.getExpensesOrderedByAmount()
                ExpenseSortType.DATE_OF_INCOME -> dao.getExpensesOrderedByDate()
                ExpenseSortType.CATEGORY -> dao.getExpensesOrderedByCategory()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _state = MutableStateFlow(ExpenseState())
    val state = combine(_state, _expenseSortType, _expenses) {state, expenseSortType, expense ->
        state.copy(
            expense = expense,
            expenseSortType = expenseSortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ExpenseState())

    private val _albumState = mutableStateOf(AlbumState())
    val albumState: State<AlbumState> get() = _albumState
    fun onEvent(event: ExpenseEvent){
        when(event){
            is ExpenseEvent.DeleteExpense -> {
                viewModelScope.launch {
                    val amount = withContext(Dispatchers.IO) {
                        dao.getAmount(event.expense.id)

                    }
                    val account = withContext(Dispatchers.IO) {
                        dao.getAccount(event.expense.id)
                    }
                    withContext(Dispatchers.IO) {
                        accountDao.updateAccountBalance(amount, account)
                    }
                    dao.deleteExpense(event.expense)
                }
            }
            is ExpenseEvent.HideDialog -> {
                val cacheDir = event.context.cacheDir
                InternalStorage().cleanCache(cacheDir)
                _state.update{it.copy(
                    isAddingExpense = false,
                    amount = "",
                    title = "",
                    date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    category = "Other",
                    account = "",
                    description = "",
                    photoPaths = null,
                    id = -1
                )}
            }
            ExpenseEvent.SaveExpense -> {
                val amount = state.value.amount.toDouble()
                val title = state.value.title
                val date = state.value.date
                val description = state.value.description
                val category = state.value.category
                val account = state.value.account.toInt()
                val photoPaths = state.value.photoPaths

                if(amount.isNaN() || title.isBlank() || category.isBlank()){
                    return
                }

                val photosJson = if (photoPaths?.isNotEmpty() == true) Gson().toJson(photoPaths) else null

                if (state.value.id == -1) {
                    val expense = Expense(
                        amount = amount,
                        title = title,
                        date = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        category = category,
                        account = account,
                        description = description,
                        photos = photosJson
                    )
                    viewModelScope.launch {
                        dao.insertExpense(expense)
                        withContext(Dispatchers.IO) {
                            accountDao.updateAccountBalance(-amount, account)
                        }
                    }
                }else {
                    val expense = Expense(
                        id = state.value.id,
                        amount = amount,
                        title = title,
                        date = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        category = category,
                        account = account,
                        description = description,
                        photos = photosJson
                    )
                    viewModelScope.launch {
                        val difference = withContext(Dispatchers.IO) {
                            val previousAmount = dao.getAmount(state.value.id)
                            amount - previousAmount
                        }
                        dao.insertExpense(expense)
                        withContext(Dispatchers.IO) {
                            accountDao.updateAccountBalance(-difference, account)
                        }
                    }
                }

                _state.update{it.copy(
                    isAddingExpense = false,
                    amount = "",
                    title = "",
                    date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    category = "Other",
                    account = "",
                    description = null,
                    id = -1,
                    photoPaths = null
                )}
            }
            is ExpenseEvent.GetData -> {
                val id = event.id
                Thread {
                    val photoString = dao.getPhotos(id)
                    val photosToList = if (!photoString.isNullOrBlank()) Gson().fromJson(photoString, Array<String>::class.java).toList() else null
                    moveExpensePhotosToCache(event.context, photosToList)
                    _state.update { it.copy(
                        title = dao.getTitle(id),
                        amount = dao.getAmount(id).toString(),
                        date = dao.getDate(id).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        category = dao.getCategory(id),
                        account = dao.getAccount(id).toString(),
                        description = dao.getDescription(id),
                    )}
                }.start()
            }
            is ExpenseEvent.SetId -> {
                _state.update { it.copy(
                    id = event.id
                ) }
            }
            is ExpenseEvent.SetAmount -> {
                _state.update { it.copy(
                    amount = event.amount
                ) }
            }
            is ExpenseEvent.SetDate -> {
                _state.update { it.copy(
                    date = event.date
                ) }
            }
            is ExpenseEvent.SetCategory -> {
                _state.update { it.copy(
                    category = event.category
                ) }
            }
            is ExpenseEvent.SetAccount -> {
                _state.update { it.copy(
                    account = event.account
                ) }
            }
            is ExpenseEvent.SetDescription -> {
                _state.update { it.copy(
                    description = event.description
                ) }
            }
            is ExpenseEvent.SetTitle -> {
                _state.update { it.copy(
                    title = event.title
                ) }
            }
            is ExpenseEvent.SetPhotoPaths -> {
                _state.update { it.copy(
                    photoPaths = event.photoPaths
                ) }
            }
            is ExpenseEvent.ShowDialog -> {
                _state.update { it.copy(
                    isAddingExpense = true
                )}
            }
            is ExpenseEvent.SortExpenses -> {
                _expenseSortType.value = event.expenseSortType
            }
        }
    }
}

fun moveExpensePhotosToCache(context: Context, photosToList: List<String>?) {
    val cacheDir = context.cacheDir

    photosToList?.forEach { photoPath ->
        val originalFile = File(photoPath)
        if (originalFile.exists()) {
            try {
                val newFile = File(cacheDir, originalFile.name)
                originalFile.copyTo(newFile, overwrite = true)
            } catch (e: IOException) {
                Toast.makeText(context, "Error loading photos", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }
}