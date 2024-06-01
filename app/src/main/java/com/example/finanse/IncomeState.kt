package com.example.finanse

import com.example.finanse.entities.Income
import java.time.LocalDateTime

data class IncomeState(
    val income: List<Income> = emptyList(),
    val amount: Double = 0.0,
    val title: String = "",
    val date: String = "",
    val description: String? = null,
    val isAddingIncome: Boolean = false,
    val incomeSortType: IncomeSortType = IncomeSortType.DATE_ADDED
    )