package com.example.finanse.states

import com.example.finanse.sortTypes.IncomeSortType
import com.example.finanse.entities.Income

data class IncomeState(
    val income: List<Income> = emptyList(),
    val amount: String = "",
    val title: String = "",
    val date: String = "",
    val description: String? = null,
    val isAddingIncome: Boolean = false,
    val incomeSortType: IncomeSortType = IncomeSortType.DATE_ADDED
    )