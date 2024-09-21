package com.example.finanse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun TopNavBar(navController: NavController, currentScreen: String, previousScreen: String){
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.navigate(previousScreen) }) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back arrow")
        }
        Text(currentScreen)
        IconButton(onClick = { navController.navigate("settings") }) {
            Icon(imageVector = Icons.Filled.Settings, contentDescription = "Side Menu")
        }
    }
}


