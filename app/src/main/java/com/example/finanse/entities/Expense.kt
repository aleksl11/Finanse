package com.example.finanse.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = arrayOf("name"),
            childColumns = arrayOf("category"),
            onDelete = ForeignKey.SET_DEFAULT
        ),
        ForeignKey(
            entity = Account::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("account"),
            onDelete = ForeignKey.CASCADE
        )]
)
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val amount: Double,
    val title: String,
    val date: LocalDate,
    val category: String = "Other",
    val account: Int,
    val description: String? = null,
    val photos: String? = null //JSON
)
