package com.example.finanse

import com.example.finanse.entities.Income
import java.time.LocalDateTime

sealed interface IncomeEvent {
    object SaveIncome: IncomeEvent
    data class  SetAmount(val amount: Double): IncomeEvent
    data class  SetTitle(val title: String): IncomeEvent
    data class  SetDate(val date: String): IncomeEvent
    data class  SetDescription(val description: String?): IncomeEvent

    object ShowDialog: IncomeEvent
    object HideDialog: IncomeEvent

    data class SortIncomes(val incomeSortType: IncomeSortType): IncomeEvent
    data class DeleteIncome(val income: Income): IncomeEvent
}