package com.example.finanse.states

import com.example.finanse.entities.Income
import com.example.finanse.sortTypes.IncomeSortType

data class IncomeState(
    val income: List<Income> = emptyList(),
    val amount: String = "",
    val title: String = "",
    val date: String = "",
    val id: Int = -1,
    val description: String? = null,
    val isAddingIncome: Boolean = false,
    val incomeSortType: IncomeSortType = IncomeSortType.DATE_ADDED
    )