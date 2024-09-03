package com.example.finanse.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("account"),
            onDelete = ForeignKey.CASCADE
        )]
)
data class Income(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val amount: Double,
    val title: String,
    val date: LocalDate,
    val account: Int,
    val description: String? = null,
    )