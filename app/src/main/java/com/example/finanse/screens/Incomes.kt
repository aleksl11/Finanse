package com.example.finanse.screens

import android.app.DatePickerDialog
import android.content.Context
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
import com.example.finanse.R
import com.example.finanse.TopNavBar
import com.example.finanse.ValidateInputs
import com.example.finanse.events.IncomeEvent
import com.example.finanse.sortTypes.IncomeSortType
import com.example.finanse.states.AccountState
import com.example.finanse.states.IncomeState
import com.example.finanse.viewModels.AlbumViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomesScreen(
    navController: NavController,
    state: IncomeState,
    accountState: AccountState,
    onEvent: (IncomeEvent) -> Unit
){
    val mAccounts = accountState.account
    val context = LocalContext.current
    Scaffold (
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(IncomeEvent.ShowDialog) },
                shape = MaterialTheme.shapes.medium,
                contentColor = Color.White,
                modifier = Modifier.size(56.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.add_income_desc))
            }
        },
    ) {padding ->
        if(state.isAddingIncome) {
            AddIncomeDialog(context = context, state = state, accountState = accountState, onEvent = onEvent)
        }

        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item{
                TopNavBar(navController, "incomes","menu")
            }
            item {
                var expanded by remember { mutableStateOf(false) } // Control dropdown menu state
                var selectedSortType by remember { mutableStateOf(state.incomeSortType) } // Track selected sort type

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
                                value = getSortTypeName(context, selectedSortType), // Show the selected sort type's name
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
                                IncomeSortType.entries.forEach { incomeSortType ->
                                    DropdownMenuItem(
                                        text = { Text(text = getSortTypeName(context, incomeSortType)) },
                                        onClick = {
                                            selectedSortType = incomeSortType
                                            onEvent(IncomeEvent.SortIncomes(incomeSortType)) // Trigger event on selection
                                            expanded = false // Close dropdown
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            items(state.income){income ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .clickable {
                            navController.navigate("incomeDetails/${income.id}")
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
                                text = "${income.title}: ${"%.2f".format(income.amount)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = income.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
                                        "\n${DisplayFormat().getAccountName(income.account.toString(), mAccounts)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            income.description?.let {
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
                            onEvent(IncomeEvent.SetId(income.id))
                            onEvent(IncomeEvent.GetData(income.id))
                            onEvent(IncomeEvent.ShowDialog)
                        }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = stringResource(R.string.edit_income_desc))
                        }
                        ConfirmPopup().DeleteIconButton(stringResource(R.string.delete_name), stringResource(R.string.delete_message_income)) {
                            onEvent(IncomeEvent.DeleteIncome(income))
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncomeDialog(
    context: Context,
    state: IncomeState,
    accountState: AccountState,
    onEvent: (IncomeEvent) -> Unit,
){
    val text = remember { mutableStateOf("") }
    val validate = ValidateInputs()
    var expandedAccount by remember { mutableStateOf(false) }
    val mAccounts = accountState.account
    val accountName = remember { mutableStateOf(DisplayFormat().getAccountName(state.account, mAccounts)) }
    val icon = if (expandedAccount)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    // Date Picker State
    val calendar = Calendar.getInstance()
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }
    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _, year, month, dayOfMonth ->
            val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)

            // Format the date to DD.MM.YYYY and update the state
            onEvent(IncomeEvent.SetDate(selectedDate.format(dateFormatter)))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    BasicAlertDialog(onDismissRequest = { onEvent(IncomeEvent.HideDialog) }) {
        LazyColumn(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.medium)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 8.dp) // Optional: Extra padding
        ) {
            item {
                Text(
                    text = stringResource(R.string.add_income_dialog),
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            item {
                OutlinedTextField(
                    value = state.title,
                    onValueChange = { onEvent(IncomeEvent.SetTitle(it)) },
                    label = { Text(text = stringResource(R.string.title_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            item {
                OutlinedTextField(
                    value = state.amount,
                    onValueChange = {
                        if (validate.isAmountValid(it)) onEvent(IncomeEvent.SetAmount(it))
                    },
                    label = { Text(text = stringResource(R.string.amount_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
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
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = stringResource(R.string.select_date_label)
                    )
                }
            }
            item {
                OutlinedTextField(
                    value = accountName.value,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.account_label)) },
                    trailingIcon = {
                        Icon(
                            icon,
                            "drop down menu arrow",
                            Modifier.clickable { expandedAccount = !expandedAccount })
                    },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(
                    expanded = expandedAccount,
                    onDismissRequest = { expandedAccount = false }
                ) {
                    mAccounts.forEach { a ->
                        DropdownMenuItem(onClick = {
                            expandedAccount = false
                            onEvent(IncomeEvent.SetAccount(a.id.toString()))
                            accountName.value = a.name
                        },
                            text = { Text(text = a.name) }
                        )
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = state.description ?: "",
                    onValueChange = {
                        val description = it.ifEmpty { null }
                        onEvent(IncomeEvent.SetDescription(description))
                    },
                    label = { Text(text = stringResource(R.string.description_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }

            item {
                val coroutineScope = rememberCoroutineScope()
                val coroutineContext = coroutineScope.coroutineContext
                AlbumScreen(viewModel = AlbumViewModel(coroutineContext))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            if (state.title.isEmpty()) text.value =
                                context.getString(R.string.no_title_error)
                            else if (state.amount.isEmpty()) text.value =
                                context.getString(R.string.no_amount_error)
                            else if (state.account.isEmpty()) text.value =
                                context.getString(R.string.no_account_error)
                            else if (validate.isDateValid(state.date)) {
                                text.value = ""
                                onEvent(IncomeEvent.SaveIncome)
                            } else text.value = context.getString(R.string.date_format_error)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = stringResource(R.string.save))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = { onEvent(IncomeEvent.HideDialog) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }
                }
            }

            if (text.value.isNotEmpty()) {
                item {
                    Text(text = text.value, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

fun getSortTypeName(context: Context, name: IncomeSortType): String{
    return when (name) {
        IncomeSortType.AMOUNT-> context.getString(R.string.sort_by_amount)
        IncomeSortType.DATE_ADDED -> context.getString(R.string.sort_by_default)
        IncomeSortType.DATE_OF_INCOME -> context.getString(R.string.sort_by_date)
    }
}