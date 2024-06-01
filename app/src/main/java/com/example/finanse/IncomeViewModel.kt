package com.example.finanse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanse.dao.IncomeDao
import com.example.finanse.entities.Income
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

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
                val amount = state.value.amount
                val title = state.value.title
                val date = state.value.date
                val description = state.value.description

                if(amount.isNaN() || title.isBlank()){
                    return
                }

                val income = Income(
                    amount = amount,
                    title = title,
                    date = date,
                    description = description
                )
                viewModelScope.launch {
                    dao.insertIncome(income)
                }
                _state.update{it.copy(
                    isAddingIncome = false,
                    amount = 0.0,
                    title = "",
                    date = "",
                    description = ""
                )}
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