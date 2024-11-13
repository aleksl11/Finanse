package com.example.finanse.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.yml.charts.axis.AxisData
import co.yml.charts.common.components.Legends
import co.yml.charts.common.model.LegendLabel
import co.yml.charts.common.model.LegendsConfig
import co.yml.charts.common.model.PlotType
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.GroupBarChart
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarPlotData
import co.yml.charts.ui.barchart.models.BarStyle
import co.yml.charts.ui.barchart.models.GroupBar
import co.yml.charts.ui.barchart.models.GroupBarChartData
import co.yml.charts.ui.barchart.models.GroupSeparatorConfig
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
import java.time.Month
import kotlin.math.roundToInt


@Composable
fun SummaryScreen(
    navController: NavController,
    incomeState: IncomeState,
    expenseState: ExpenseState,
    categoryState: CategoryState
    ){
    var chosenTimePeriod by remember { mutableStateOf(SummaryTimePeriod.THIS_MONTH)}
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

    val dateNow = LocalDate.now()
    var sortedExpenses: List<Expense>
    var sortedIncomes: List<Income>

    Column(modifier = Modifier.fillMaxSize()) {
        TopNavBar(navController, "summary","menu")
        if(incomes.isEmpty() && expenses.isEmpty()) NoRecordsInDb()
        else LazyColumn(
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(text = "Yearly expenses and incomes",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center)
            }
            item {
                YearlyBarChart(incomes = incomes.filter { i ->
                    i.date.year == dateNow.year
                }, expenses = expenses.filter { e ->
                    e.date.year == dateNow.year
                })
            }
            item {
                Text(
                    text = "Detailed Summary",
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
                            Text(text = getTimePeriodName(timePeriod), fontSize = 16.sp)
                        }
                    }
                }
            }
            item{
                when(chosenTimePeriod){
                    SummaryTimePeriod.THIS_MONTH -> {
                        sortedExpenses = expenses.filter { e ->
                            e.date.month == dateNow.month && e.date.year == dateNow.year
                        }
                        sortedIncomes = incomes.filter { i ->
                            i.date.month == dateNow.month && i.date.year == dateNow.year
                        }
                        System.out.println("sorted month")
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
                        System.out.println("sorted year")
                        if(sortedExpenses.isEmpty()) NoRecordsInLists()
                        else TimePeriodSummary(sortedExpenses = sortedExpenses, sortedIncomes = sortedIncomes, categoryState)
                    }
                    SummaryTimePeriod.ALL_TIME -> {
                        sortedExpenses = expenses
                        sortedIncomes = incomes
                        System.out.println("sorted all")
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
        text = "No records added to database",
        fontSize = 16.sp,
        modifier = Modifier.padding(16.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
fun NoRecordsInLists(){
    Text(
        text = "No expenses in the selected time period",
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

/*    val categories = categoryState.category
    val legendsConfig = LegendsConfig(
        legendLabelList = categories.map { c ->
            LegendLabel(Color(c.color), c.name) },
        gridColumnCount = categories.size
    )*/

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
        ,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.Start
    ){
        Text("Total incomes: ${"%.2f".format(incomesTotal)} PLN")
        Text("Total time expenses: ${"%.2f".format(expensesTotal)} PLN")
        Text("Balance: ${"%.2f".format(incomesTotal-expensesTotal)} PLN")
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
        /*Legends(
            legendsConfig = legendsConfig
        )*/
    }
}

@Composable
fun ChartLegend(categories: List<Category>){
    Column (
        horizontalAlignment = Alignment.Start
    ){
        categories.forEach { c ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier// Transparent background
                        .size(30.dp)
                        .padding(2.dp), // Padding for spacing
                    shape = MaterialTheme.shapes.small, // Add some elevation for shadow
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardColors(Color(c.color),Color(c.color),Color(c.color),Color(c.color))
                ){}
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp) // Add space between color box and text
                ) {
                    Text(text = c.name, fontSize = 20.sp)
                }
            }
        }
    }
}

@Composable
fun YearlyBarChart(incomes: List<Income>, expenses: List<Expense>){
    val monthNames = mapOf(
        Month.JANUARY to "January",
        Month.FEBRUARY to "February",
        Month.MARCH to "March",
        Month.APRIL to "April",
        Month.MAY to "May",
        Month.JUNE to "June",
        Month.JULY to "July",
        Month.AUGUST to "August",
        Month.SEPTEMBER to "September",
        Month.OCTOBER to "October",
        Month.NOVEMBER to "November",
        Month.DECEMBER to "December"
    )
    // Get unique months from both lists
    val uniqueMonths = (incomes.map { it.date.month } + expenses.map { it.date.month })
        .distinct() // Remove duplicates
        .sorted()   // Sort by month

    val monthlyIncomeMap = incomes
        .groupBy { it.date.month }
        .mapValues { (_, incomeList) ->
            incomeList.sumOf { it.amount }
        }
    val monthlyExpenseMap = expenses
        .groupBy { it.date.month }
        .mapValues { (_, expenseList) ->
            expenseList.sumOf { it.amount }
        }

    val uniqueMonthsMap: Map<Int, Month> = uniqueMonths.mapIndexed { index, month ->
        index to (month ?: Month.DECEMBER)
    }.toMap()
    val monthIndexMap: Map<Int, String> = uniqueMonths.mapIndexed { index, month ->
        index to (monthNames[month] ?: "error")
    }.toMap()

    val groupBarData: List<GroupBar> = uniqueMonthsMap.values.map { month ->
        val monthName = monthNames[month] ?: "error"
        val totalExpenses = monthlyExpenseMap[month]?.times(100) ?: 0f
        val totalIncomes = monthlyIncomeMap[month]?.times(100) ?: 0f

        GroupBar(
            monthName,
            listOf(
                // First BarData corresponds to expenses
                BarData(Point(5F, totalExpenses.toFloat(), "Expenses"), Color.Red, "%.2f".format(totalExpenses.toDouble()/100.0)),
                // Second BarData corresponds to incomes
                BarData(Point(5F, totalIncomes.toFloat(), "Incomes"), Color.Green, "%.2f".format(totalIncomes.toDouble()/100.0))
            )
        )
    }

    val maxIncome = monthlyIncomeMap.values.maxOrNull() ?: 0.0
    val maxExpense = monthlyExpenseMap.values.maxOrNull() ?: 0.0
    val maxRange = if (maxIncome>maxExpense) maxIncome.roundToInt().times(10) else maxExpense.roundToInt().times(10)

    val yStepSize = 10
    val xAxisData = AxisData.Builder()
        .axisStepSize(30.dp)
        .bottomPadding(5.dp)
        .startDrawPadding(15.dp)
        .labelData { index -> monthIndexMap[index] ?: "error" }
        .axisLabelColor(MaterialTheme.colorScheme.primary)
        .axisLineColor(MaterialTheme.colorScheme.primary)
        .build()
    val yAxisData = AxisData.Builder()
        .steps(yStepSize)
        .labelAndAxisLinePadding(20.dp)
        .axisOffset(20.dp)
        .labelData { index -> (index * (maxRange / yStepSize)/10).toString() }
        .axisLabelColor(MaterialTheme.colorScheme.primary)
        .axisLineColor(MaterialTheme.colorScheme.primary)
        .build()
    val colorPaletteList =  listOf(Color.Red, Color.Green)

    val legendsConfig = LegendsConfig(
        legendLabelList = listOf(LegendLabel(Color.Green, "Incomes"),LegendLabel(Color.Red, "Expenses")),
        gridColumnCount = 2
    )
    val groupBarPlotData = BarPlotData(
        groupBarList = groupBarData,
        barStyle = BarStyle(barWidth = 35.dp),
        barColorPaletteList = colorPaletteList
    )
    val groupBarChartData = GroupBarChartData(
        barPlotData = groupBarPlotData,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        groupSeparatorConfig = GroupSeparatorConfig(1.dp),
        backgroundColor = MaterialTheme.colorScheme.background
    )
    Column(
        Modifier
            .height(450.dp)
    ) {
        GroupBarChart(
            modifier = Modifier
                .height(400.dp),
            groupBarChartData = groupBarChartData
        )
        Legends(
            legendsConfig = legendsConfig
        )
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