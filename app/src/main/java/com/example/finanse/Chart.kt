package com.example.finanse

import android.graphics.Typeface
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

class Chart {

    @Composable
    fun PieChart(
        data: PieData
    ){
        val appBackgroundColor = MaterialTheme.colorScheme.background.toArgb()
        val appTextColor = MaterialTheme.colorScheme.onBackground.toArgb()

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            factory = { context ->
                val chart = PieChart(context).apply {
                    // Set background and hole color to match the app theme
                    setBackgroundColor(appBackgroundColor)
                    setHoleColor(appBackgroundColor)
                    setTransparentCircleColor(appBackgroundColor)
                    // Style entry labels
                    setDrawEntryLabels(true)
                    setEntryLabelColor(Color.Black.toArgb())
                    setEntryLabelTextSize(12f)
                    setEntryLabelTypeface(Typeface.DEFAULT_BOLD)

                    // Use percent values
                    setUsePercentValues(true)

                    // Disable the default description
                    description.isEnabled = false

                    // Set initial center text properties
                    centerText = "Select a slice"
                    setCenterTextColor(appTextColor)
                    setCenterTextSize(16f)
                    isRotationEnabled = true

                    // Set the chart's legend
                    legend.textColor = appTextColor
                    legend.form = Legend.LegendForm.CIRCLE
                    legend.formSize = 16f
                    legend.textSize = 16f
                    legend.orientation = Legend.LegendOrientation.HORIZONTAL
                    legend.isWordWrapEnabled = true
                }

                chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        if (e is PieEntry) {
                            chart.centerText = "${e.label}:\n${String.format("%.2f", e.data)} PLN"
                        }
                    }

                    override fun onNothingSelected() {
                        chart.centerText = "Select a slice"
                    }
                })
                chart.data = data
                chart.invalidate()
                chart.animateY(1000)
                chart
                      },
            update = { chart ->
                chart.data = data
                chart.invalidate()
            }
        )
    }

    @Composable
    fun BarChart(data: BarData) {
        val monthNames = arrayOf(
            stringResource(R.string.january), stringResource(R.string.february), stringResource(R.string.march),
            stringResource(R.string.april), stringResource(R.string.may), stringResource(R.string.june),
            stringResource(R.string.july), stringResource(R.string.august), stringResource(R.string.september),
            stringResource(R.string.october), stringResource(R.string.november), stringResource(R.string.december)
        )
        val appBackgroundColor = MaterialTheme.colorScheme.background.toArgb()
        val appTextColor = MaterialTheme.colorScheme.onBackground.toArgb()
        val barWidth = 0.35f
        val groupSpace = 0.25f
        val barSpace = 0.05f

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            factory = { context ->
                BarChart(context).apply {
                    setBackgroundColor(appBackgroundColor)
                    data.dataSets.forEach { it.setDrawValues(false) }
                    this.data = data
                    barData.barWidth = barWidth
                    description.isEnabled = false
                    legend.apply {
                        textColor = appTextColor
                        form = Legend.LegendForm.CIRCLE
                        formSize = 16f
                        textSize = 16f
                        orientation = Legend.LegendOrientation.HORIZONTAL
                        isWordWrapEnabled = true
                    }
                    axisLeft.textColor = appTextColor
                    axisRight.isEnabled = false
                    xAxis.apply {
                        textColor = appTextColor
                        valueFormatter = IndexAxisValueFormatter(monthNames)
                        position = XAxis.XAxisPosition.BOTTOM
                        granularity = 1f
                        isGranularityEnabled = true
                        setDrawGridLines(false)
                        setCenterAxisLabels(true)

                        axisMinimum = 0f
                        axisMaximum = data.xMax + 1.5f
                        labelCount = monthNames.size
                        labelRotationAngle = -45f
                    }
                    axisLeft.apply {
                        textColor = appTextColor
                        setDrawGridLines(true)
                    }
                    barData.barWidth = barWidth
                    setFitBars(true)
                    groupBars(0f, groupSpace, barSpace)
                    resetZoom()
                    invalidate()
                    animateY(1000)
                    marker = MyMarkerView(context)
                }
            }
        )
    }

}