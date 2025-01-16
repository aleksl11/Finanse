package com.example.finanse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun TopNavBar(navController: NavController, currentScreen: String, previousScreen: String){
    val stringResId = when (currentScreen.lowercase()) {
        "summary" -> R.string.summary
        "settings" -> R.string.settings
        "expenses" -> R.string.expenses
        "incomes" -> R.string.incomes
        "categories" -> R.string.categories
        "accounts" -> R.string.accounts
        else -> {R.string.app_name}
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colorScheme.primary) // Set your desired background color here
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { navController.navigate(previousScreen) }) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back arrow", tint = MaterialTheme.colorScheme.onPrimary)
            }
            Text(stringResource(stringResId), color = MaterialTheme.colorScheme.onPrimary)
            IconButton(onClick = { navController.navigate("settings") }) {
                Icon(imageVector = Icons.Filled.Settings, contentDescription = "Side Menu", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}


