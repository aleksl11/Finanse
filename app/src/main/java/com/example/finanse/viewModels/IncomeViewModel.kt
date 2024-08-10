package com.example.finanse.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanse.dao.IncomeDao
import com.example.finanse.entities.Income
import com.example.finanse.events.IncomeEvent
import com.example.finanse.sortTypes.IncomeSortType
import com.example.finanse.states.IncomeState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class IncomeViewModel(
    private val dao: IncomeDao
): ViewModel() {

    private val _incomeSortType = MutableStateFlow(IncomeSortType.DATE_ADDED)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _incomes = _incomeSortType
        .flatMapLatest { incomeSortType ->
            when(incomeSortType){
                IncomeSortType.DATE_ADDED ->dao.getIncomesOrderedById()
                IncomeSortType.AMOUNT -> dao.getIncomesOrderedByAmount()
                IncomeSortType.DATE_OF_INCOME -> dao.getIncomesOrderedByDate()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _state = MutableStateFlow(IncomeState())
    val state = combine(_state, _incomeSortType, _incomes) {state, incomeSortType, income ->
        state.copy(
            income = income,
            incomeSortType = incomeSortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), IncomeState())

    fun onEvent(event: IncomeEvent){
        when(event){
            is IncomeEvent.DeleteIncome -> {
                viewModelScope.launch {
                    dao.deleteIncome(event.income)
                }
            }
            IncomeEvent.HideDialog -> {
                _state.update { it.copy(
                    isAddingIncome = false
                )}
            }
            IncomeEvent.SaveIncome -> {
                val amount = state.value.amount.toDouble()
                val title = state.value.title
                val date = state.value.date
                val description = state.value.description

                if(amount.isNaN() || title.isBlank()){
                    return
                }
                if (state.value.id == -1) {
                    val income = Income(
                        amount = amount,
                        title = title,
                        date = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        description = description
                    )
                    viewModelScope.launch {
                        dao.insertIncome(income)
                    }
                }else {
                    val income = Income(
                        id = state.value.id,
                        amount = amount,
                        title = title,
                        date = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        description = description
                    )
                    viewModelScope.launch {
                        dao.insertIncome(income)
                    }
                }
                _state.update{it.copy(
                    isAddingIncome = false,
                    amount = "",
                    title = "",
                    date = "",
                    description = "",
                    id = -1
                )}
            }
            is IncomeEvent.GetData -> {
                val id = event.id
                Thread {
                    _state.update { it.copy(
                        title = dao.getTitle(id),
                        amount = dao.getAmount(id).toString(),
                        date = dao.getDate(id).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                        description = dao.getDescription(id),
                    )}
                }.start()
            }
            is IncomeEvent.SetId -> {
                _state.update { it.copy(
                    id = event.id
                ) }
            }
            is IncomeEvent.SetAmount -> {
                _state.update { it.copy(
                    amount = event.amount
                ) }
            }
            is IncomeEvent.SetDate -> {
                _state.update { it.copy(
                    date = event.date
                ) }
            }
            is IncomeEvent.SetDescription -> {
                _state.update { it.copy(
                    description = event.description
                ) }
            }
            is IncomeEvent.SetTitle -> {
                _state.update { it.copy(
                    title = event.title
                ) }
            }
            IncomeEvent.ShowDialog -> {
                _state.update { it.copy(
                    isAddingIncome = true
                )}
            }
            is IncomeEvent.SortIncomes -> {
                _incomeSortType.value = event.incomeSortType
            }
        }
    }
}