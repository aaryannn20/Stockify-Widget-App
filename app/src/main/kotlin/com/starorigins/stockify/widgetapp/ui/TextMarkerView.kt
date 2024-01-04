package com.starorigins.stockify.widgetapp.ui

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.starorigins.stockify.widgetapp.AppPreferences.Companion.DATE_FORMATTER
import com.starorigins.stockify.widgetapp.network.data.DataPoint
import com.starorigins.stockify.widgetapp.R

class TextMarkerView(context: Context) : MarkerView(context, R.layout.text_marker_layout) {

  private var tvContent: TextView = findViewById(R.id.tvContent)
  private val offsetPoint by lazy {
    MPPointF(-(width / 2).toFloat(), -height.toFloat())
  }

  override fun refreshContent(
    e: Entry?,
    highlight: Highlight?
  ) {
    if (e is DataPoint) {
      val price = com.starorigins.stockify.widgetapp.AppPreferences.DECIMAL_FORMAT.format(e.y)
      val date = e.getDate().format(DATE_FORMATTER)
      tvContent.text = "${price}\n$date"
    } else {
      tvContent.text = ""
    }
    super.refreshContent(e, highlight)
  }

  override fun getOffset(): MPPointF = offsetPoint
}