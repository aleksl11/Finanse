package com.example.finanse.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finanse.DisplayFormat
import com.example.finanse.TopNavBar
import com.example.finanse.ValidateInputs
import com.example.finanse.events.ExpenseEvent
import com.example.finanse.sortTypes.ExpenseSortType
import com.example.finanse.states.AccountState
import com.example.finanse.states.CategoryState
import com.example.finanse.states.ExpenseState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    navController: NavController,
    state: ExpenseState,
    categoryState: CategoryState,
    accountState: AccountState,
    onEvent: (ExpenseEvent) -> Unit
) {
    val mAccounts = accountState.account
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(ExpenseEvent.ShowDialog) },
                shape = MaterialTheme.shapes.medium,
                contentColor = Color.White,
                modifier = Modifier.size(56.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add an expense")
            }
        },
        modifier = Modifier.padding(16.dp)
    ) { padding ->
        if (state.isAddingExpense) {
            AddExpenseDialog(state = state, categoryState = categoryState, accountState = accountState, onEvent = onEvent)
        }

        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TopNavBar(navController, "Expenses", "menu")
            }
            item {
                var expanded by remember { mutableStateOf(false) } // Control dropdown menu state
                var selectedSortType by remember { mutableStateOf(state.expenseSortType) } // Track selected sort type

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp) // Add padding around sorting options
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically, // Align items vertically centered
                        modifier = Modifier.padding(vertical = 8.dp) // Space around the row
                    ) {
                        Text(
                            text = "Sort type: ", // Label for the dropdown
                            fontSize = 16.sp, // Font size
                            modifier = Modifier.padding(end = 8.dp) // Space between label and dropdown
                        )

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded } // Toggle dropdown
                        ) {
                            // The TextField that shows the currently selected sort type
                            OutlinedTextField(
                                value = getSortTypeName(selectedSortType), // Show the selected sort type's name
                                onValueChange = {},
                                readOnly = true, // Prevent editing
                                trailingIcon = {
                                    Icon(
                                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                        contentDescription = null
                                    )
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor() // Open dropdown when clicked
                            )

                            // The DropdownMenu with all sorting options
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false } // Close the dropdown menu
                            ) {
                                ExpenseSortType.entries.forEach { expenseSortType ->
                                    DropdownMenuItem(
                                        text = { Text(text = getSortTypeName(expenseSortType)) },
                                        onClick = {
                                            selectedSortType = expenseSortType
                                            onEvent(ExpenseEvent.SortExpenses(expenseSortType)) // Trigger event on selection
                                            expanded = false // Close dropdown
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            items(state.expense) { expense ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "${expense.title}: ${"%.2f".format(expense.amount)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = expense.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
                                        "\n${DisplayFormat().getAccountName(expense.account.toString(), mAccounts)}" +
                                        "\n${expense.category}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            expense.description?.let {
                                Text(text = it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        IconButton(onClick = {
                            onEvent(ExpenseEvent.SetId(expense.id))
                            onEvent(ExpenseEvent.GetData(expense.id))
                            onEvent(ExpenseEvent.ShowDialog)
                        }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit expense")
                        }
                        IconButton(onClick = {
                            onEvent(ExpenseEvent.DeleteExpense(expense))
                        }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete expense")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(
    state: ExpenseState,
    categoryState: CategoryState,
    accountState: AccountState,
    onEvent: (ExpenseEvent) -> Unit,
) {
    val text = remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var expandedAccount by remember { mutableStateOf(false) }
    val mCategories = categoryState.category
    val mAccounts = accountState.account
    val accountName = remember { mutableStateOf(DisplayFormat().getAccountName(state.account, mAccounts)) }
    val icon = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown

    val validate = ValidateInputs()

    // Date Picker State
    val calendar = Calendar.getInstance()
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }
    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _, year, month, dayOfMonth ->
            val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)

            // Format the date to DD.MM.YYYY and update the state
            onEvent(ExpenseEvent.SetDate(selectedDate.format(dateFormatter)))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    BasicAlertDialog(onDismissRequest = { onEvent(ExpenseEvent.HideDialog) }) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.medium)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Add or Edit Expense", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = state.title,
                onValueChange = { onEvent(ExpenseEvent.SetTitle(it)) },
                label = { Text(text = "Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = state.amount,
                onValueChange = {
                    if (validate.isAmountValid(it)) onEvent(ExpenseEvent.SetAmount(it))
                },
                label = { Text(text = "Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = state.date.ifEmpty { "Select Date" }, modifier = Modifier.padding(8.dp))
                Icon(Icons.Default.DateRange, contentDescription = "Select Date")
            }

            OutlinedTextField(
                value = state.category,
                onValueChange = { onEvent(ExpenseEvent.SetCategory(it)) },
                label = { Text("Category") },
                trailingIcon = {
                    Icon(icon, "drop down menu arrow", Modifier.clickable { expanded = !expanded })
                },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                mCategories.forEach { c ->
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            onEvent(ExpenseEvent.SetCategory(c.name))
                        },
                        text = { Text(text = c.name) }
                    )
                }
            }

            OutlinedTextField(
                value = accountName.value,
                onValueChange = {},
                label = { Text("Account") },
                trailingIcon = {
                    Icon(icon, "drop down menu arrow", Modifier.clickable { expandedAccount = !expandedAccount })
                },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenu(
                expanded = expandedAccount,
                onDismissRequest = { expandedAccount = false }
            ) {
                mAccounts.forEach { a ->
                    DropdownMenuItem(
                        onClick = {
                            expandedAccount = false
                            onEvent(ExpenseEvent.SetAccount(a.id.toString()))
                            accountName.value = a.name
                        },
                        text = { Text(text = a.name) }
                    )
                }
            }

            OutlinedTextField(
                value = state.description ?: "",
                onValueChange = {
                    val description = it.ifEmpty { null }
                    onEvent(ExpenseEvent.SetDescription(description))
                },
                label = { Text(text = "Description") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        if (state.title.isEmpty()) text.value = "Title must be filled in"
                        else if (state.amount.isEmpty()) text.value = "Amount field cannot be empty"
                        else if (state.category.isEmpty()) text.value = "Category must be selected"
                        else if (state.account.isEmpty()) text.value = "Account must be selected"
                        else if (validate.isDateValid(state.date)) {
                            text.value = ""
                            onEvent(ExpenseEvent.SaveExpense)
                        } else text.value = "Incorrect date format"
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Save")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(onClick = { onEvent(ExpenseEvent.HideDialog) }, modifier = Modifier.weight(1f)) {
                    Text(text = "Cancel")
                }
            }

            if (text.value.isNotEmpty()) {
                Text(text = text.value, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

fun getSortTypeName(name: ExpenseSortType): String{
    return when (name) {
        ExpenseSortType.AMOUNT-> "Amount"
        ExpenseSortType.CATEGORY -> "Category"
        ExpenseSortType.DATE_ADDED -> "Default"
        ExpenseSortType.DATE_OF_INCOME -> "Date"
    }
}