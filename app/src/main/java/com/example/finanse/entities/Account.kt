package com.example.finanse.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Account(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val balance: Double
)
