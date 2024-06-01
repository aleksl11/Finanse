package com.example.finanse.screens

import android.provider.MediaStore.Audio.Radio
import android.widget.CalendarView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.finanse.IncomeEvent
import com.example.finanse.IncomeSortType
import com.example.finanse.IncomeState
import com.example.finanse.TopNavBar
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun IncomesScreen(
    navController: NavController,
    state: IncomeState,
    onEvent: (IncomeEvent) -> Unit
){
    Scaffold (
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onEvent(IncomeEvent.ShowDialog)
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add income")
            }
        },
        modifier = Modifier.padding(16.dp)
    ) {padding ->

        if(state.isAddingIncome) {
            AddIncomeDialog(state = state, onEvent = onEvent)
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
                            Text(text = incomeSortType.name)
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
    onEvent: (IncomeEvent) -> Unit,
){
    AlertDialog(
        onDismissRequest = { onEvent(IncomeEvent.HideDialog) },
        ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Add income")
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
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
                value = state.amount.toString(),
                onValueChange = {
                    onEvent(IncomeEvent.SetAmount(it.toDouble()))
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
                    Text(text = "Date")
                }
            )
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
                Button(onClick = { onEvent(IncomeEvent.SaveIncome) }) {
                    Text(text = "Save")
                }
            }
        }

    }
    
}

@Composable
fun ShowDatePicker() {
    AndroidView(
        { CalendarView(it) },
        modifier = Modifier.wrapContentWidth(),
        update = { views ->
            views.setOnDateChangeListener { calendarView, year, month, dayOfMonth ->
            }
        }
    )
}