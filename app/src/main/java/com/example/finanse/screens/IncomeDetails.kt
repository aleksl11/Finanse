package com.example.finanse.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finanse.DisplayFormat
import com.example.finanse.TopNavBar
import com.example.finanse.entities.Income
import com.example.finanse.states.AccountState
import java.time.format.DateTimeFormatter

@Composable
fun IncomeDetails(income: Income, navController: NavController, accountState: AccountState) {
    val mAccounts = accountState.account
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp), // Add spacing between items
        ) {
            item {
                TopNavBar(navController, "incomeDetails", "Incomes")
            }
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp), // Add padding around the card
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface, // Background color for the card
                        contentColor = MaterialTheme.colorScheme.onSurface // Text color for the content
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp), // Add padding inside the card
                        verticalArrangement = Arrangement.spacedBy(12.dp) // Add spacing between rows
                    ) {
                        Text(
                            text = "Title:",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp), // Increased font size
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = income.title,
                            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp) // Bigger and bold for emphasis
                        )

                        Text(
                            text = "Amount:",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "%.2f".format(income.amount),
                            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp)
                        )

                        Text(
                            text = "Date:",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = income.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp)
                        )

                        Text(
                            text = "Account:",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = DisplayFormat().getAccountName(income.account.toString(), mAccounts),
                            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp)
                        )
                        income.description?.let {
                            Text(
                                text = "Description:",
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = it,
                                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp)
                            )
                        }
                    }
                }
            }
        }
    }
}