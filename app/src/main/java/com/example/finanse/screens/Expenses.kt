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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
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
import com.example.finanse.events.ExpenseEvent
import com.example.finanse.sortTypes.ExpenseSortType
import com.example.finanse.states.CategoryState
import com.example.finanse.states.ExpenseState

@Composable
fun ExpensesScreen(
    navController: NavController,
    state: ExpenseState,
    categoryState: CategoryState,
    onEvent: (ExpenseEvent) -> Unit
){
    Scaffold (
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onEvent(ExpenseEvent.ShowDialog)
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add an expense")
            }
        },
        modifier = Modifier.padding(16.dp)
    ) {padding ->

        if(state.isAddingExpense) {
            AddExpenseDialog(state = state, categoryState = categoryState, onEvent = onEvent)
        }

        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item{
                TopNavBar(navController, "Expenses","menu")
            }
            item{
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    ExpenseSortType.entries.forEach { expenseSortType ->
                        Row(
                            modifier = Modifier
                                .clickable {
                                    onEvent(ExpenseEvent.SortExpenses(expenseSortType))
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            RadioButton(selected = state.expenseSortType == expenseSortType,
                                onClick = {
                                    onEvent(ExpenseEvent.SortExpenses(expenseSortType))
                                }
                            )
                            Text(text = expenseSortType.name)
                        }
                    }
                }
            }
            items(state.expense){expense ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                ){
                    Column(
                        modifier = Modifier.weight(1f)
                    ){
                        Text(text = "${expense.title}: ${"%.2f".format(expense.amount)}", fontSize = 20.sp)
                        Text(text = "${expense.date.toString()}\n${expense.category}", fontSize = 16.sp)
                        expense.description?.let { Text(text = it, fontSize = 12.sp) }
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(
    state: ExpenseState,
    categoryState: CategoryState,
    onEvent: (ExpenseEvent) -> Unit,
){
    val text = remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val mCategories = categoryState.category
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    val validate = ValidateInputs()
    AlertDialog(
        onDismissRequest = { onEvent(ExpenseEvent.HideDialog) },
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .background(Color.Gray)
                .padding(8.dp)
        ) {
            Text(text = "Add expense")

            TextField(
                value = state.title,
                onValueChange = {
                    onEvent(ExpenseEvent.SetTitle(it))
                },
                placeholder = {
                    Text(text = "Title")
                }
            )
            TextField(
                value = state.amount,
                onValueChange = {
                    if(validate.isAmountValid(it))onEvent(ExpenseEvent.SetAmount(it))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                placeholder = {
                    Text(text = "0.00")
                }
            )
            TextField(
                value = state.date,
                onValueChange = {
                    onEvent(ExpenseEvent.SetDate(it))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                placeholder = {
                    Text(text = "DD.MM.YYYY")
                }
            )
            TextField(
                value = state.category,
                onValueChange = {
                    onEvent(ExpenseEvent.SetCategory(it))
                },
                label = {Text("Category")},
                trailingIcon = {
                    Icon(icon,"drop down menu arrow",
                        Modifier.clickable { expanded = !expanded })
                },
                readOnly = true
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                mCategories.forEach { c ->
                    DropdownMenuItem(onClick = {
                        expanded = false
                        onEvent(ExpenseEvent.SetCategory(c.name))
                    },
                        text = {Text(text = c.name)}
                    )
                }
            }
            TextField(
                value = state.description ?: "",
                onValueChange = {
                    val description = it.ifEmpty { null }
                    onEvent(ExpenseEvent.SetDescription(description))
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
                    if (validate.isDateValid(state.date)){
                        text.value = ""
                        onEvent(ExpenseEvent.SaveExpense)
                    }
                    else text.value = "Incorrect date. Make sure it is in DD.MM.YYYY format"
                    if (state.category == "") text.value = "Category field must be filled in"
                    if (state.title == "") text.value = "Title field must be filled in"
                }) {
                    Text(text = "Save")
                }
            }
            Text(text = text.value, color = Color.Red)
        }

    }

}
