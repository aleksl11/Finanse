package com.example.finanse.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
 import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun Menu(navController: NavController) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        MenuButton("Summary", navController, "summary")
        MenuButton("Incomes", navController, "incomes")
        MenuButton("Expenses", navController, "expenses")
        MenuButton("Categories", navController, "categories")
        MenuButton("Accounts", navController, "account")
        MenuButton("Settings", navController, "settings")
    }
}

@Composable
fun MenuButton(name: String, navController: NavController, screen: String){
    Spacer(modifier = Modifier.height(25.dp))
    Button(modifier = Modifier.fillMaxWidth()
        .height(60.dp),
        shape = RectangleShape,
        onClick = {
        navController.navigate(screen)
    })
    {
        Text(text = name, fontSize = 25.sp)
    }
}