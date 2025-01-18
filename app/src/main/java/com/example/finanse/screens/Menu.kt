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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finanse.R

@Composable
fun Menu(navController: NavController) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        MenuButton(stringResource(R.string.summary), navController, "summary")
        MenuButton(stringResource(R.string.incomes), navController, "incomes")
        MenuButton(stringResource(R.string.expenses), navController, "expenses")
        MenuButton(stringResource(R.string.categories), navController, "categories")
        MenuButton(stringResource(R.string.accounts), navController, "account")
        MenuButton(stringResource(R.string.settings), navController, "settings")
    }
}

@Composable
fun MenuButton(name: String, navController: NavController, screen: String){
    Spacer(modifier = Modifier.height(25.dp))
    Button(modifier = Modifier
        .fillMaxWidth()
        .height(60.dp),
        shape = RectangleShape,
        onClick = {
        navController.navigate(screen)
    })
    {
        Text(text = name, fontSize = 25.sp)
    }
}