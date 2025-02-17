package com.example.finanse

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.github.mikephil.charting.components.IMarker
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

class MyMarkerView(context: Context) : IMarker {

    // Paint objects for drawing text and background.
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 40f  // Adjust text size as needed
    }
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#AA000000")  // Semi-transparent black
    }
    private val padding = 16f  // Padding in pixels

    private var markerText: String = ""

    override fun refreshContent(e: Entry, highlight: Highlight) {
        markerText = "${e.y} PLN"
    }

    override fun getOffset(): MPPointF {
        // Measure the text dimensions.
        val textWidth = textPaint.measureText(markerText)
        val fm = textPaint.fontMetrics
        val textHeight = fm.descent - fm.ascent

        // Compute full marker dimensions (text + padding on all sides).
        val markerWidth = textWidth + 2 * padding
        val markerHeight = textHeight + 2 * padding

        // Return an offset that centers the marker horizontally and places it above the bar.
        return MPPointF(-markerWidth / 2, -markerHeight)
    }

    override fun getOffsetForDrawingAtPoint(posX: Float, posY: Float): MPPointF {
        TODO("Not yet implemented")
    }

    fun getOffsetForDrawingAtPos(posX: Float, posY: Float): MPPointF {
        return getOffset()
    }


    override fun draw(canvas: Canvas, posX: Float, posY: Float) {
        // Get the offset so that the marker is drawn above the tapped bar.
        val offset = getOffsetForDrawingAtPos(posX, posY)
        val x = posX + offset.x
        val y = posY + offset.y

        // Measure text dimensions.
        val textWidth = textPaint.measureText(markerText)
        val fm = textPaint.fontMetrics
        val textHeight = fm.descent - fm.ascent

        // Calculate the marker’s width and height.
        val markerWidth = textWidth + 2 * padding
        val markerHeight = textHeight + 2 * padding

        // Draw the background rectangle.
        canvas.drawRect(x, y, x + markerWidth, y + markerHeight, backgroundPaint)

        // Draw the text so that it is centered within the background.
        // The y-position for text drawing uses (y + padding - fm.ascent) to align the text baseline correctly.
        canvas.drawText(markerText, x + padding, y + padding - fm.ascent, textPaint)
    }
}