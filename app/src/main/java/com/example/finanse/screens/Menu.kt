package com.example.finanse.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun Menu(navController: NavController) {
    Column {
        MenuButton("Summary", navController, "summary")
        MenuButton("Incomes", navController, "incomes")
        MenuButton("Expenses", navController, "expenses")
        MenuButton("Categories", navController, "categories")
        MenuButton("Settings", navController, "settings")
    }
}

@Composable
fun MenuButton(name: String, navController: NavController, screen: String){
    Button(onClick = {
        navController.navigate(screen)
    })
    {
        Text(text = name)
    }
}