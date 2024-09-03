package com.example.finanse.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@Composable
fun IncomesScreen(
    navController: NavController,
    state: IncomeState,
    accountState: AccountState,
    onEvent: (IncomeEvent) -> Unit
){
    Scaffold (
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onEvent(IncomeEvent.ShowDialog)
            }) {
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    IncomeSortType.entries.forEach { incomeSortType ->
                        Row(
                            modifier = Modifier
                                .clickable {
                                    onEvent(IncomeEvent.SortIncomes(incomeSortType))
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            RadioButton(selected = state.incomeSortType == incomeSortType,
                                onClick = {
                                    onEvent(IncomeEvent.SortIncomes(incomeSortType))
                                }
                            )
                            Text(text = getSortTypeName(incomeSortType))
                        }
                    }
                }
            }
            items(state.income){income ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                ){
                    Column(
                        modifier = Modifier.weight(1f)
                    ){
                        Text(text = "${income.title}: ${"%.2f".format(income.amount)}", fontSize = 20.sp)
                        Text(text = income.date.toString(), fontSize = 16.sp)
                        income.description?.let { Text(text = it, fontSize = 12.sp) }
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

    BasicAlertDialog(onDismissRequest = { onEvent(IncomeEvent.HideDialog) }) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.background(Color.Gray)
                .padding(8.dp)
        ) {
            Text(text = "Income")
            TextField(
                value = state.title,
                onValueChange = {
                    onEvent(IncomeEvent.SetTitle(it))
                },
                placeholder = {
                    Text(text = "Title")
                }
            )
            TextField(
                value = state.amount,
                onValueChange = {
                    if (validate.isAmountValid(it)) onEvent(IncomeEvent.SetAmount(it))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                placeholder = {
                    Text(text = "0.00")
                }
            )
            TextField(
                value = state.date,
                onValueChange = {
                    onEvent(IncomeEvent.SetDate(it))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = {
                    Text(text = "DD.MM.YYYY")
                }
            )
            TextField(
                value = accountName.value,
                onValueChange = {},
                label = { Text("Account") },
                trailingIcon = {
                    Icon(icon, "drop down menu arrow",
                        Modifier.clickable { expandedAccount = !expandedAccount })
                },
                readOnly = true
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
            TextField(
                value = state.description ?: "",
                onValueChange = {
                    val description = it.ifEmpty { null }
                    onEvent(IncomeEvent.SetDescription(description))
                },
                placeholder = {
                    Text(text = "Description")
                }
            )
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(onClick = {
                    if (validate.isDateValid(state.date)) {
                        text.value = ""
                        onEvent(IncomeEvent.SaveIncome)
                    } else text.value = "Incorrect date. Make sure it is in DD.MM.YYYY format"
                    if (state.title == "") text.value = "Title field must be filled in"
                }) {
                    Text(text = "Save")
                }
            }
            Text(text = text.value, color = Color.Red)
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