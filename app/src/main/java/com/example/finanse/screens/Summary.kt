package com.example.finanse.screens

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.finanse.Chart
import com.example.finanse.R
import com.example.finanse.TopNavBar
import com.example.finanse.entities.Account
import com.example.finanse.entities.Category
import com.example.finanse.entities.Expense
import com.example.finanse.entities.Income
import com.example.finanse.sortTypes.SummaryTimePeriod
import com.example.finanse.states.AccountState
import com.example.finanse.states.CategoryState
import com.example.finanse.states.ExpenseState
import com.example.finanse.states.IncomeState
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    navController: NavController,
    incomeState: IncomeState,
    expenseState: ExpenseState,
    categoryState: CategoryState,
    accountState: AccountState
){
    var chosenTimePeriod by remember { mutableStateOf(SummaryTimePeriod.THIS_MONTH)}
    var year by remember { mutableStateOf(LocalDate.now().year) }
    val accounts = listOf(Account(id = -1, name = stringResource(R.string.all_accounts), balance = 0.0)) + accountState.account
    var selectedAccount by remember { mutableStateOf(accounts.first()) }
    var expanded by remember { mutableStateOf(false) }
    val incomes  = incomeState.income
    val expenses = expenseState.expense
    val filteredIncomes = remember(selectedAccount, incomeState.income) {
        if (selectedAccount.id == -1)
            incomeState.income
        else
            incomeState.income.filter { it.account == selectedAccount.id }
    }
    val filteredExpenses = remember(selectedAccount, expenseState.expense) {
        if (selectedAccount.id == -1)
            expenseState.expense
        else
            expenseState.expense.filter { it.account == selectedAccount.id }
    }

    val context = LocalContext.current
    var incomesTotal = 0.0
    filteredIncomes.forEach{i ->
        incomesTotal += i.amount
    }
    var expensesTotal = 0.0
    filteredExpenses.forEach{e ->
        expensesTotal += e.amount
    }

    val dateNow = LocalDate.now()
    var sortedExpenses: List<Expense>
    var sortedIncomes: List<Income>

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopNavBar(navController, "summary","menu")
        if(incomes.isEmpty() && expenses.isEmpty()) NoRecordsInDb()
        else LazyColumn(
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically, // Align items vertically centered
                    modifier = Modifier.padding(vertical = 8.dp) // Space around the row
                ) {
                    Text(
                        text = stringResource(R.string.choose_account) + ": ", // Label for the dropdown
                        fontSize = 16.sp, // Font size
                        modifier = Modifier.padding(end = 8.dp) // Space between label and dropdown
                    )

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded } // Toggle dropdown
                    ) {
                        // The TextField that shows the currently selected sort type
                        OutlinedTextField(
                            value = selectedAccount.name, // Show the selected sort type's name
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
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false } // Close the dropdown menu
                        ) {
                            accounts.forEach { account ->
                                DropdownMenuItem(
                                    text = { Text(text = account.name) },
                                    onClick = {
                                        selectedAccount = account
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            item {
                Text(text = stringResource(R.string.yearly_summary),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center)
            }
            item{
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Button(onClick = { year -=1 }) {
                        Text(text = "<")
                    }
                    Text(text = "$year", modifier = Modifier.padding(horizontal = 16.dp))
                    Button(
                        enabled = year<LocalDate.now().year,
                        onClick = {
                        year +=1
                    }) {
                        Text(text = ">")
                    }
                }
            }
            item {
                YearlyBarChart(
                    year = year,
                    incomes = filteredIncomes,
                    expenses = filteredExpenses
                )
            }
            item {
                Text(
                    text = stringResource(R.string.detailed_summary),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .border(1.dp, Color.Gray)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.Start
                ) {

                    SummaryTimePeriod.entries.forEach { timePeriod ->
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ){
                            RadioButton(
                                selected = chosenTimePeriod == timePeriod,
                                onClick = { chosenTimePeriod = timePeriod },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                    unselectedColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(text = getTimePeriodName(context, timePeriod), fontSize = 16.sp)
                        }
                    }
                }
            }
            item{
                when(chosenTimePeriod){
                    SummaryTimePeriod.THIS_MONTH -> {
                        sortedExpenses = filteredExpenses.filter { e ->
                            e.date.month == dateNow.month && e.date.year == dateNow.year
                        }
                        sortedIncomes = filteredIncomes.filter { i ->
                            i.date.month == dateNow.month && i.date.year == dateNow.year
                        }
                        if(sortedExpenses.isEmpty()) NoRecordsInLists()
                        else TimePeriodSummary(sortedExpenses = sortedExpenses, sortedIncomes = sortedIncomes, categoryState)
                    }
                    SummaryTimePeriod.THIS_YEAR -> {
                        sortedExpenses = expenses.filter { e ->
                            e.date.year == dateNow.year
                        }
                        sortedIncomes = incomes.filter { i ->
                            i.date.year == dateNow.year
                        }
                        if(sortedExpenses.isEmpty()) NoRecordsInLists()
                        else TimePeriodSummary(sortedExpenses = sortedExpenses, sortedIncomes = sortedIncomes, categoryState)
                    }
                    SummaryTimePeriod.ALL_TIME -> {
                        sortedExpenses = filteredExpenses
                        sortedIncomes = filteredIncomes
                        if(sortedExpenses.isEmpty()) NoRecordsInLists()
                        else TimePeriodSummary(sortedExpenses = sortedExpenses, sortedIncomes = sortedIncomes, categoryState)
                    }

                }
            }
        }
    }
}

@Composable
fun NoRecordsInDb(){
    Text(
        text = stringResource(R.string.empty_db_warning),
        fontSize = 16.sp,
        modifier = Modifier.padding(16.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
fun NoRecordsInLists(){
    Text(
        text = stringResource(R.string.empty_list_warning),
        fontSize = 16.sp,
        modifier = Modifier.padding(16.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
fun TimePeriodSummary(sortedExpenses: List<Expense>, sortedIncomes: List<Income>, categoryState: CategoryState){
    var incomesTotal = 0.0
    sortedIncomes.forEach{i ->
        incomesTotal += i.amount
    }
    var expensesTotal = 0.0
    sortedExpenses.forEach{e ->
        expensesTotal += e.amount
    }

    val expensesGrouped = sortedExpenses.groupBy { it.category }
        .mapValues { e -> e.value.sumOf { it.amount } }

    val pieEntries = mutableListOf<PieEntry>()
    val colors = mutableListOf<Int>()

    expensesGrouped.forEach { (category, sum) ->
        val percent = ((sum / expensesTotal) * 100).toFloat()
        pieEntries.add(PieEntry(percent, category, sum))
        colors.add(getColorByCategory(categoryState.category, category))
    }

    val dataSet = PieDataSet(pieEntries, "").apply {
        this.colors = colors
        valueTextSize = 12f
        sliceSpace = 2f
        setDrawValues(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
        ,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.Start
    ){
        Text(stringResource(R.string.total_incomes) + ": ${"%.2f".format(incomesTotal)} PLN")
        Text(stringResource(R.string.total_expenses) + ": ${"%.2f".format(expensesTotal)} PLN")
        Text(stringResource(R.string.balance) + ": ${"%.2f".format(incomesTotal-expensesTotal)} PLN")
        Text(
            stringResource(R.string.category_summary),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Chart().PieChart(data = PieData(dataSet))
    }
}

@Composable
fun YearlyBarChart(year: Int, incomes: List<Income>, expenses: List<Expense>){
    val filteredIncomes = incomes.filter { it.date.year == year }
    val filteredExpenses = expenses.filter { it.date.year == year }

    val monthlyIncomeMap = filteredIncomes.groupBy { it.date.monthValue - 1 }
        .mapValues { it.value.sumOf { income -> income.amount } }
    val monthlyExpenseMap = filteredExpenses.groupBy { it.date.monthValue - 1 }
        .mapValues { it.value.sumOf { expense -> expense.amount } }

    val entriesIncome = ArrayList<BarEntry>()
    val entriesExpense = ArrayList<BarEntry>()
    val barWidth = 0.4f  // Reduce bar width to fit within the group

    val barSpace = 0.05f


    for (i in 0 until 12) {
        val income = monthlyIncomeMap[i]?.toFloat() ?: 0f
        val expense = monthlyExpenseMap[i]?.toFloat() ?: 0f

        val startX = i.toFloat()  // Use direct index for clarity

        entriesIncome.add(BarEntry(startX, income))
        entriesExpense.add(BarEntry(startX, expense))
    }

    val incomeDataSet = BarDataSet(entriesIncome, stringResource(R.string.incomes)).apply {
        color = Color.Green.toArgb()
        valueTextSize = 12f
        setDrawValues(true)
    }
    val expenseDataSet = BarDataSet(entriesExpense, stringResource(R.string.expenses)).apply {
        color = Color.Red.toArgb()
        valueTextSize = 12f
        setDrawValues(true)
    }

    val barData = BarData(incomeDataSet, expenseDataSet)

    Chart().BarChart(barData)
}

fun getColorByCategory(categories: List<Category>, categoryName: String): Int {
    val category = categories.find { it.name == categoryName }
    if (category != null) return category.color
    return Color.Gray.hashCode()
}

fun getTimePeriodName(context: Context, time: SummaryTimePeriod): String{
    return when (time) {
        SummaryTimePeriod.ALL_TIME -> context.getString(R.string.all_time)
        SummaryTimePeriod.THIS_MONTH -> context.getString(R.string.this_month)
        SummaryTimePeriod.THIS_YEAR -> context.getString(R.string.this_year)
    }
}