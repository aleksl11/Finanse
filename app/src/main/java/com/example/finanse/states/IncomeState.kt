package com.example.finanse.states

import com.example.finanse.entities.Income
import com.example.finanse.sortTypes.IncomeSortType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class IncomeState(
    val income: List<Income> = emptyList(),
    val amount: String = "",
    val title: String = "",
    val date: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
    val id: Int = -1,
    val account: String = "1",
    val description: String? = null,
    val photoPaths: List<String>? = null,
    val isAddingIncome: Boolean = false,
    val incomeSortType: IncomeSortType = IncomeSortType.DATE_ADDED
    )