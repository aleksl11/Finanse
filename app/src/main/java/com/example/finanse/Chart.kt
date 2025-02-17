package com.example.finanse

import android.graphics.Typeface
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieEntry
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
            modifier = Modifier.fillMaxWidth()
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
                chart.animate()
                chart
                      },
            update = { chart ->
                chart.data = data
                chart.invalidate()
            }
        )
    }
}