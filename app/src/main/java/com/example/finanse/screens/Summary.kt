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
            item{
                YearlyBarChart(incomes = incomes.filter { i ->
                    i.date.year == dateNow.year
                }, expenses = expenses.filter { e ->
                    e.date.year == dateNow.year
                })
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

    val categories = categoryState.category
    val legendsConfig = LegendsConfig(
        legendLabelList = categories.map { c ->
            LegendLabel(Color(c.color), c.name) },
        gridColumnCount = categories.size
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

@Composable
fun YearlyBarChart(incomes: List<Income>, expenses: List<Expense>){
    val monthNames = mapOf(
        Month.JANUARY to "styczeń",
        Month.FEBRUARY to "luty",
        Month.MARCH to "marzec",
        Month.APRIL to "kwiecień",
        Month.MAY to "maj",
        Month.JUNE to "czerwiec",
        Month.JULY to "lipiec",
        Month.AUGUST to "sierpień",
        Month.SEPTEMBER to "wrzesień",
        Month.OCTOBER to "październik",
        Month.NOVEMBER to "listopad",
        Month.DECEMBER to "grudzień"
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
        val totalExpenses = monthlyExpenseMap[month] ?: 0f
        val totalIncomes = monthlyIncomeMap[month] ?: 0f

        GroupBar(
            monthName,
            listOf(
                // First BarData corresponds to expenses
                BarData(Point(5F, totalExpenses.toFloat(), "Expenses"), Color.Red),
                // Second BarData corresponds to incomes
                BarData(Point(5F, totalIncomes.toFloat(), "Incomes"), Color.Green)
            )
        )
    }

    val maxIncome = monthlyIncomeMap.values.maxOrNull() ?: 0.0
    val maxExpense = monthlyExpenseMap.values.maxOrNull() ?: 0.0
    val maxRange = if (maxIncome>maxExpense) maxIncome.roundToInt() else maxExpense.roundToInt()

    val yStepSize = 10
    val xAxisData = AxisData.Builder()
        .axisStepSize(30.dp)
        .bottomPadding(5.dp)
        .startDrawPadding(48.dp)
        .labelData { index -> monthIndexMap[index] ?: "error" }
        .build()
    val yAxisData = AxisData.Builder()
        .steps(yStepSize)
        .labelAndAxisLinePadding(20.dp)
        .axisOffset(20.dp)
        .labelData { index -> (index * (maxRange / yStepSize)).toString() }
        .build()
    val colorPaletteList =  listOf(Color.Green, Color.Red)

    val legendsConfig = LegendsConfig(
        legendLabelList = listOf(LegendLabel(Color.Green, "przychody"),LegendLabel(Color.Red, "wydatki")),
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