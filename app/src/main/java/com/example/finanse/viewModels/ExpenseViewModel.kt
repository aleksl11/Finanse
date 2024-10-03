package com.example.finanse.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanse.dao.AccountDao
import com.example.finanse.dao.ExpenseDao
import com.example.finanse.entities.Expense
import com.example.finanse.events.ExpenseEvent
import com.example.finanse.sortTypes.ExpenseSortType
import com.example.finanse.states.ExpenseState
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
import java.time.LocalDate
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
            ExpenseEvent.HideDialog -> {
                _state.update{it.copy(
                    isAddingExpense = false,
                    amount = "",
                    title = "",
                    date = "",
                    category = "",
                    account = "",
                    description = "",
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


                if(amount.isNaN() || title.isBlank() || category.isBlank()){
                    return
                }
                if (state.value.id == -1) {
                    val expense = Expense(
                        amount = amount,
                        title = title,
                        date = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        category = category,
                        account = account,
                        description = description
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
                        description = description
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
                    date = "",
                    category = "",
                    account = "",
                    description = "",
                    id = -1
                )}
            }
            is ExpenseEvent.GetData -> {
                val id = event.id
                Thread {
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