package com.example.finanse.states

import com.example.finanse.entities.Expense
import com.example.finanse.sortTypes.ExpenseSortType

data class ExpenseState(
    val expense: List<Expense> = emptyList(),
    val amount: String = "",
    val title: String = "",
    val date: String = "",
    val category: String = "",
    val id: Int = -1,
    val description: String? = null,
    val isAddingExpense: Boolean = false,
    val expenseSortType: ExpenseSortType = ExpenseSortType.DATE_ADDED
)