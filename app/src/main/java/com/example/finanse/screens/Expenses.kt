package com.example.finanse.screens

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finanse.ConfirmPopup
import com.example.finanse.DisplayFormat
import com.example.finanse.InternalStorage
import com.example.finanse.R
import com.example.finanse.TopNavBar
import com.example.finanse.ValidateInputs
import com.example.finanse.events.ExpenseEvent
import com.example.finanse.sortTypes.ExpenseSortType
import com.example.finanse.states.AccountState
import com.example.finanse.states.CategoryState
import com.example.finanse.states.ExpenseState
import com.example.finanse.viewModels.AlbumViewModel
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
    val context = LocalContext.current
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(ExpenseEvent.ShowDialog) },
                shape = MaterialTheme.shapes.medium,
                contentColor = Color.White,
                modifier = Modifier.size(56.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.add_expense_desc))
            }
        },
    ) { padding ->
        if (state.isAddingExpense) {
            AddExpenseDialog(
                context = context,
                state = state,
                categoryState = categoryState,
                accountState = accountState,
                onEvent = onEvent
            )
        }
        Column {
            TopNavBar(navController, "Expenses", "menu")
            LazyColumn(
                contentPadding = padding,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
                                text = stringResource(R.string.sort_by) + ": ", // Label for the dropdown
                                fontSize = 16.sp, // Font size
                                modifier = Modifier.padding(end = 8.dp) // Space between label and dropdown
                            )

                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded } // Toggle dropdown
                            ) {
                                // The TextField that shows the currently selected sort type
                                OutlinedTextField(
                                    value = getSortTypeName(
                                        context,
                                        selectedSortType
                                    ), // Show the selected sort type's name
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
                                    onDismissRequest = {
                                        expanded = false
                                    } // Close the dropdown menu
                                ) {
                                    ExpenseSortType.entries.forEach { expenseSortType ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = getSortTypeName(
                                                        context,
                                                        expenseSortType
                                                    )
                                                )
                                            },
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
                            .padding(horizontal = 8.dp)
                            .clickable {
                                navController.navigate("expenseDetails/${expense.id}")
                            },
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface, // Background color
                            contentColor = MaterialTheme.colorScheme.onSurface  // Text and icon color
                        )
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
                                            "\n${
                                                DisplayFormat().getAccountName(
                                                    expense.account.toString(),
                                                    mAccounts
                                                )
                                            }" +
                                            "\n${expense.category}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                expense.description?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            IconButton(onClick = {
                                onEvent(ExpenseEvent.SetId(expense.id))
                                onEvent(ExpenseEvent.GetData(expense.id, context))
                                onEvent(ExpenseEvent.ShowDialog)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = stringResource(R.string.edit_expense_desc)
                                )
                            }
                            ConfirmPopup().DeleteIconButton(
                                stringResource(R.string.delete_name),
                                stringResource(R.string.delete_message_expense)
                            ) {
                                onEvent(ExpenseEvent.DeleteExpense(expense))
                            }
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
    context: Context,
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
            onEvent(ExpenseEvent.SetDate(selectedDate.format(dateFormatter)))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    BasicAlertDialog(onDismissRequest = { onEvent(ExpenseEvent.HideDialog(context)) }) {
        // Wrapping the entire dialog content in `LazyColumn` for scrolling
        LazyColumn(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.medium)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 8.dp) // Optional: Extra padding
        ) {
            item {
                Text(
                    text = stringResource(R.string.add_expense_dialog),
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Title Input
            item {
                OutlinedTextField(
                    value = state.title,
                    onValueChange = { onEvent(ExpenseEvent.SetTitle(it)) },
                    label = { Text(text = stringResource(R.string.title_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // Amount Input
            item {
                OutlinedTextField(
                    value = state.amount,
                    onValueChange = {
                        if (validate.isAmountValid(it)) onEvent(ExpenseEvent.SetAmount(it))
                    },
                    label = { Text(text = stringResource(R.string.amount_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // Date Picker
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = state.date.ifEmpty { stringResource(R.string.select_date_label) },
                        modifier = Modifier.padding(8.dp)
                    )
                    Icon(Icons.Default.DateRange, contentDescription = stringResource(R.string.select_date_label))
                }
            }

            // Category Dropdown
            item {
                OutlinedTextField(
                    value = state.category,
                    onValueChange = { onEvent(ExpenseEvent.SetCategory(it)) },
                    label = { Text(stringResource(R.string.category_label)) },
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
            }

            // Account Dropdown
            item {
                OutlinedTextField(
                    value = accountName.value,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.account_label)) },
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
            }

            // Description Input
            item {
                OutlinedTextField(
                    value = state.description ?: "",
                    onValueChange = {
                        val description = it.ifEmpty { null }
                        onEvent(ExpenseEvent.SetDescription(description))
                    },
                    label = { Text(text = stringResource(R.string.description_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }

            //Photos
            item {
                val coroutineScope = rememberCoroutineScope()
                val coroutineContext = coroutineScope.coroutineContext
                AlbumScreen(viewModel = AlbumViewModel(coroutineContext))
            }

            // Error Message
            if (text.value.isNotEmpty()) {
                item {
                    Text(text = text.value, color = MaterialTheme.colorScheme.error)
                }
            }

            // Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            if (state.title.isEmpty()) text.value = context.getString(R.string.no_title_error)
                            else if (state.amount.isEmpty()) text.value = context.getString(R.string.no_amount_error)
                            else if (state.category.isEmpty()) text.value = context.getString(R.string.no_category_error)
                            else if (state.account.isEmpty()) text.value = context.getString(R.string.no_account_error)
                            else if (validate.isDateValid(state.date)) {
                                text.value = ""

                                val cacheDir = context.cacheDir
                                val cachedImages = cacheDir.listFiles { file ->
                                    file.name.endsWith(".jpg") // Assuming images are saved with .jpg extension
                                }?.toList() ?: emptyList()
                                Log.d("MoveImage", "Selected pictures: ${cachedImages.size}")
                                // Move images from cache to internal storage
                                val newPaths = mutableListOf<String>()
                                for (imageFile in cachedImages) {
                                    val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                                    val newPath = InternalStorage().moveImageToInternalStorage(context, bitmap, "Expense")
                                    if (newPath != null) {
                                        newPaths.add(newPath)
                                    }
                                }

                                // Update state with new paths
                                onEvent(ExpenseEvent.SetPhotoPaths(newPaths))
                                InternalStorage().cleanCache(cacheDir)
                                onEvent(ExpenseEvent.SaveExpense)
                            } else text.value = context.getString(R.string.date_format_error)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = stringResource(R.string.save))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = { onEvent(ExpenseEvent.HideDialog(context)) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }
                }
            }
        }
    }
}

fun getSortTypeName(context: Context, name: ExpenseSortType): String{
    return when (name) {
        ExpenseSortType.AMOUNT-> context.getString(R.string.sort_by_amount)
        ExpenseSortType.CATEGORY -> context.getString(R.string.sort_by_category)
        ExpenseSortType.DATE_ADDED -> context.getString(R.string.sort_by_default)
        ExpenseSortType.DATE_OF_INCOME -> context.getString(R.string.sort_by_date)
    }
}