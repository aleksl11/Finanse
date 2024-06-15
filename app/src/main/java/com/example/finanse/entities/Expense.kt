package com.example.finanse.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val amount: Double,
    val title: String,
    val date: LocalDate,
    val category: String,
    val description: String? = null,
)