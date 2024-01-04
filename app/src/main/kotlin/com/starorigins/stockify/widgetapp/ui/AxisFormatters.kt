package com.starorigins.stockify.widgetapp.ui

import android.graphics.Canvas
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

class DateAxisFormatter : IAxisValueFormatter {

  override fun getFormattedValue(
    value: Float,
    axis: AxisBase
  ): String {
    val date = LocalDateTime.ofInstant(Instant.ofEpochSecond(value.toLong()), ZoneId.systemDefault()).toLocalDate()
    return date.format(com.starorigins.stockify.widgetapp.AppPreferences.AXIS_DATE_FORMATTER)
  }
}

class HourAxisFormatter : IAxisValueFormatter {

  override fun getFormattedValue(
          value: Float,
          axis: AxisBase?
  ): String {
    val hour = LocalDateTime.ofInstant(Instant.ofEpochSecond(value.toLong()), ZoneId.systemDefault()).toLocalTime()
    return hour.format(com.starorigins.stockify.widgetapp.AppPreferences.TIME_FORMATTER)
  }
}

class ValueAxisFormatter : IAxisValueFormatter {

  override fun getFormattedValue(
    value: Float,
    axis: AxisBase
  ): String =
    com.starorigins.stockify.widgetapp.AppPreferences.DECIMAL_FORMAT.format(value)
}

class MultilineXAxisRenderer(
  viewPortHandler: ViewPortHandler?,
  xAxis: XAxis?,
  trans: Transformer?
) : XAxisRenderer(viewPortHandler, xAxis, trans) {

  override fun drawLabel(
    c: Canvas,
    formattedLabel: String,
    x: Float,
    y: Float,
    anchor: MPPointF,
    angleDegrees: Float
  ) {
    val lines = formattedLabel.split("-")
    for (i in 0 until lines.size) {
      val vOffset = i * mAxisLabelPaint.textSize
      Utils.drawXAxisValue(c, lines[i], x, y + vOffset, mAxisLabelPaint, anchor, angleDegrees)
    }
  }
}