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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finanse.DisplayFormat
import com.example.finanse.R
import com.example.finanse.TopNavBar
import com.example.finanse.entities.Expense
import com.example.finanse.photos.PhotoGallery
import com.example.finanse.states.AccountState
import java.time.format.DateTimeFormatter

@Composable
fun ExpenseDetails(expense: Expense, navController: NavController, accountState: AccountState) {
    val mAccounts = accountState.account
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp), // Add spacing between items
        ) {
            item {
                TopNavBar(navController, "expenseDetails", "expenses")
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
                            text = stringResource(R.string.title_label)+":",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp), // Increased font size
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = expense.title,
                            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp) // Bigger and bold for emphasis
                        )

                        Text(
                            text = stringResource(R.string.amount_label)+":",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "%.2f".format(expense.amount),
                            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp)
                        )

                        Text(
                            text = stringResource(R.string.date_label)+":",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = expense.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp)
                        )

                        Text(
                            text = stringResource(R.string.account_label)+":",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = DisplayFormat().getAccountName(expense.account.toString(), mAccounts),
                            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp)
                        )

                        Text(
                            text = stringResource(R.string.category_label)+":",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = expense.category,
                            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp)
                        )

                        expense.description?.let {
                            Text(
                                text = stringResource(R.string.description_label)+":",
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = it,
                                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp)
                            )
                        }

                        expense.photos?.let {
                            Text(
                                text = stringResource(R.string.photos_label)+":",
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
                                color = MaterialTheme.colorScheme.primary
                            )
                            PhotoGallery(it)
                        }
                    }
                }
            }
        }
    }
}