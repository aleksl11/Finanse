package com.example.finanse.states

import com.example.finanse.entities.Expense
import com.example.finanse.sortTypes.ExpenseSortType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class ExpenseState(
    val expense: List<Expense> = emptyList(),
    val amount: String = "",
    val title: String = "",
    val date: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
    val category: String = "Other",
    val id: Int = -1,
    val account: String = "1",
    val description: String? = null,
    val photoPaths: List<String>? = null,
    val isAddingExpense: Boolean = false,
    val expenseSortType: ExpenseSortType = ExpenseSortType.DATE_ADDED
)