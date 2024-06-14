package com.example.finanse.events

import com.example.finanse.sortTypes.IncomeSortType
import com.example.finanse.entities.Income

sealed interface IncomeEvent {
    object SaveIncome: IncomeEvent
    data class  SetAmount(val amount: String): IncomeEvent
    data class  SetTitle(val title: String): IncomeEvent
    data class  SetDate(val date: String): IncomeEvent
    data class  SetDescription(val description: String?): IncomeEvent

    object ShowDialog: IncomeEvent
    object HideDialog: IncomeEvent

    data class SortIncomes(val incomeSortType: IncomeSortType): IncomeEvent
    data class DeleteIncome(val income: Income): IncomeEvent
}