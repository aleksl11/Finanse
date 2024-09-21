package com.example.finanse.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.example.finanse.TopNavBar
import com.example.finanse.entities.Category
import com.example.finanse.entities.Expense
import com.example.finanse.entities.Income
import com.example.finanse.sortTypes.SummaryTimePeriod
import com.example.finanse.states.CategoryState
import com.example.finanse.states.ExpenseState
import com.example.finanse.states.IncomeState
import java.time.LocalDate


@Composable
fun SummaryScreen(
    navController: NavController,
    incomeState: IncomeState,
    expenseState: ExpenseState,
    categoryState: CategoryState
    ){
    var chosenTimePeriod by remember { mutableStateOf(SummaryTimePeriod.ALL_TIME)}
    val incomes = incomeState.income
    val expenses = expenseState.expense

    var incomesTotal = 0.0
    incomes.forEach{i ->
        incomesTotal += i.amount
    }
    var expensesTotal = 0.0
    expenses.forEach{e ->
        expensesTotal += e.amount
    }

    Column{
        TopNavBar(navController, "summary","menu")
        if(incomes.isEmpty() && expenses.isEmpty()) NoRecordsInDb()
        else LazyColumn {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .border(1.dp, Color.Black),
                    horizontalAlignment = Alignment.Start
                ) {

                    SummaryTimePeriod.entries.forEach { timePeriod ->
                        Row (verticalAlignment = Alignment.CenterVertically){
                            RadioButton(
                                selected = chosenTimePeriod == timePeriod,
                                onClick = { chosenTimePeriod = timePeriod }
                            )
                            Text(text = getTimePeriodName(timePeriod))
                        }
                    }
                }
            }
            val dateNow = LocalDate.now()
            var sortedExpenses: List<Expense>
            var sortedIncomes: List<Income>
            item{
                when(chosenTimePeriod){
                    SummaryTimePeriod.THIS_MONTH -> {
                        sortedExpenses = expenses.filter { e ->
                            e.date.month == dateNow.month && e.date.year == dateNow.year
                        }
                        sortedIncomes = incomes.filter { i ->
                            i.date.month == dateNow.month && i.date.year == dateNow.year
                        }
                        if(sortedExpenses.isEmpty() && sortedIncomes.isEmpty()) NoRecordsInLists()
                        else TimePeriodSummary(sortedExpenses = sortedExpenses, sortedIncomes = sortedIncomes, categoryState)
                    }
                    SummaryTimePeriod.THIS_YEAR -> {
                        sortedExpenses = expenses.filter { e ->
                            e.date.year == dateNow.year
                        }
                        sortedIncomes = incomes.filter { i ->
                            i.date.year == dateNow.year
                        }
                        if(sortedExpenses.isEmpty() && sortedIncomes.isEmpty()) NoRecordsInLists()
                        else TimePeriodSummary(sortedExpenses = sortedExpenses, sortedIncomes = sortedIncomes, categoryState)
                    }
                    SummaryTimePeriod.ALL_TIME -> {
                        sortedExpenses = expenses
                        sortedIncomes = incomes
                        if(sortedExpenses.isEmpty() && sortedIncomes.isEmpty()) NoRecordsInLists()
                        else TimePeriodSummary(sortedExpenses = sortedExpenses, sortedIncomes = sortedIncomes, categoryState)
                    }

                }
            }

        }
    }
}

@Composable
fun NoRecordsInDb(){
    Text(text = "No records added to database")
}

@Composable
fun NoRecordsInLists(){
    Text(text = "No records added to database in selected time period")
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

    val pieChartDataList: MutableList<PieChartData.Slice> = mutableListOf()
    expensesGrouped.forEach { (category, sum) ->
        pieChartDataList.add(PieChartData.Slice(
            color = Color(getColorByCategory(categoryState.category, category)),
            label = category,
            value = ((sum/expensesTotal)*100).toFloat()
            )
        )
    }

    val pieChartData = PieChartData(
        slices = pieChartDataList,
        plotType = PlotType.Pie
    )

    val pieChartConfig = PieChartConfig(
        labelColor = Color.Black,
        labelVisible = true,
        isAnimationEnable = true,
        showSliceLabels = false,
        animationDuration = 1500,
        backgroundColor = MaterialTheme.colorScheme.background
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
        ,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.Start
    ){
        Text("Total incomes: $incomesTotal PLN")
        Text("Total time expenses: $expensesTotal PLN")
        Text("Balance: ${incomesTotal-expensesTotal} PLN")
        Text(
            "Expenses by categories",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        DonutPieChart(
            modifier = Modifier
                .width(400.dp)
                .height(400.dp)
                .padding(5.dp),
            pieChartData,
            pieChartConfig
        )
        ChartLegend(categories = categoryState.category)
    }
}

@Composable
fun ChartLegend(categories: List<Category>){
    Column (
        horizontalAlignment = Alignment.Start
    ){
        categories.forEach { c ->
            Row (verticalAlignment = Alignment.CenterVertically){
                Box(modifier = Modifier
                    .background(Color(c.color))
                    .size(40.dp)
                    .border(1.dp, Color.Black))
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = c.name)
            }
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

fun getColorByCategory(categories: List<Category>, categoryName: String): Int {
    val category = categories.find { it.name == categoryName }
    if (category != null) return category.color
    return Color.Gray.hashCode()
}

fun getTimePeriodName(time: SummaryTimePeriod): String{
    return when (time) {
        SummaryTimePeriod.ALL_TIME -> "All time"
        SummaryTimePeriod.THIS_MONTH -> "This month"
        SummaryTimePeriod.THIS_YEAR -> "This year"
    }
}