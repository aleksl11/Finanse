package com.example.finanse.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import com.example.finanse.TopNavBar
import com.example.finanse.ValidateInputs
import com.example.finanse.events.IncomeEvent
import com.example.finanse.sortTypes.IncomeSortType
import com.example.finanse.states.AccountState
import com.example.finanse.states.IncomeState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun IncomesScreen(
    navController: NavController,
    state: IncomeState,
    accountState: AccountState,
    onEvent: (IncomeEvent) -> Unit
){
    Scaffold (
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(IncomeEvent.ShowDialog) },
                shape = MaterialTheme.shapes.medium,
                contentColor = Color.White,
                modifier = Modifier.size(56.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add an income")
            }
        },
        modifier = Modifier.padding(16.dp)
    ) {padding ->
        if(state.isAddingIncome) {
            AddIncomeDialog(state = state, accountState = accountState, onEvent = onEvent)
        }

        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item{
                TopNavBar(navController, "incomes","menu")
            }
            item{
                // Sort options row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    IncomeSortType.entries.forEach { incomeSortType ->
                        Row(
                            modifier = Modifier
                                .clickable {
                                    onEvent(IncomeEvent.SortIncomes(incomeSortType))
                                }
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            RadioButton(
                                selected = state.incomeSortType == incomeSortType,
                                onClick = { onEvent(IncomeEvent.SortIncomes(incomeSortType)) }
                            )
                            Text(text = getSortTypeName(incomeSortType), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
            items(state.income){income ->
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
                                text = "${income.title}: ${"%.2f".format(income.amount)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = income.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
                                        "\n${income.account}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            income.description?.let {
                                Text(text = it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        IconButton(onClick = {
                            onEvent(IncomeEvent.SetId(income.id))
                            onEvent(IncomeEvent.GetData(income.id))
                            onEvent(IncomeEvent.ShowDialog)
                        }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit income")
                        }
                        IconButton(onClick = {
                            onEvent(IncomeEvent.DeleteIncome(income))
                        }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete income")
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
    state: IncomeState,
    accountState: AccountState,
    onEvent: (IncomeEvent) -> Unit,
){
    val text = remember { mutableStateOf("") }
    val validate = ValidateInputs()
    var expandedAccount by remember { mutableStateOf(false) }
    val accountName = remember { mutableStateOf("") }
    val mAccounts = accountState.account
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
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.medium)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Add or Edit Income", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = state.title,
                onValueChange = { onEvent(IncomeEvent.SetTitle(it)) },
                label = { Text(text = "Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = state.amount,
                onValueChange = {
                    if (validate.isAmountValid(it)) onEvent(IncomeEvent.SetAmount(it))
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
                    DropdownMenuItem(onClick = {
                        expandedAccount = false
                        onEvent(IncomeEvent.SetAccount(a.id.toString()))
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
                    onEvent(IncomeEvent.SetDescription(description))
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
                        if (state.title.isEmpty()) text.value = "Title must be included"
                        if (state.amount.isEmpty()) text.value = "Amount field cannot be empty"
                        if (state.account.isEmpty()) text.value = "Account must be selected"
                        if (validate.isDateValid(state.date)) {
                            text.value = ""
                            onEvent(IncomeEvent.SaveIncome)
                        } else text.value = "Incorrect date format"
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Save")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(onClick = { onEvent(IncomeEvent.HideDialog) }, modifier = Modifier.weight(1f)) {
                    Text(text = "Cancel")
                }
            }

            if (text.value.isNotEmpty()) {
                Text(text = text.value, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

fun getSortTypeName(name: IncomeSortType): String{
    return when (name) {
        IncomeSortType.AMOUNT-> "Amount"
        IncomeSortType.DATE_ADDED -> "Default"
        IncomeSortType.DATE_OF_INCOME -> "Date"
    }
}