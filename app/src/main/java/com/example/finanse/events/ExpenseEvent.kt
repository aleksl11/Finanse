package com.example.finanse.events

import com.example.finanse.entities.Expense

import com.example.finanse.sortTypes.ExpenseSortType


sealed interface ExpenseEvent {
    object SaveExpense: ExpenseEvent
    data class  SetId(val id: Int): ExpenseEvent
    data class  SetAmount(val amount: String): ExpenseEvent
    data class  SetTitle(val title: String): ExpenseEvent
    data class  SetDate(val date: String): ExpenseEvent
    data class  SetCategory(val category: String): ExpenseEvent
    data class  SetDescription(val description: String?): ExpenseEvent

    object ShowDialog: ExpenseEvent

    object HideDialog: ExpenseEvent

    data class SortExpenses(val expenseSortType: ExpenseSortType): ExpenseEvent
    data class DeleteExpense(val expense: Expense): ExpenseEvent
    data class GetData(val id: Int) : ExpenseEvent
}