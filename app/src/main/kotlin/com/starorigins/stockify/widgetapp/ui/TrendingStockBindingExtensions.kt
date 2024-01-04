package com.starorigins.stockify.widgetapp.ui

import android.content.res.Resources.Theme
import android.util.TypedValue
import androidx.core.content.ContextCompat
import com.starorigins.stockify.widgetapp.network.data.Quote
import com.starorigins.stockify.widgetapp.R
import com.starorigins.stockify.widgetapp.databinding.ItemTrendingStockBinding
import com.robinhood.ticker.TickerUtils

fun ItemTrendingStockBinding.bindStock(
  quote: Quote,
  listener: (Quote) -> Unit
) {
  val positiveColor: Int = ContextCompat.getColor(root.context, R.color.positive_green)
  val negativeColor: Int = ContextCompat.getColor(root.context, R.color.negative_red)
  val neutralColor: Int by lazy {
    try {
      val typedValue = TypedValue()
      val theme: Theme = root.context.theme
      theme.resolveAttribute(
          com.google.android.material.R.attr.colorOnSurfaceVariant,
          typedValue,
          true
      )
      ContextCompat.getColor(root.context, typedValue.data)
    } catch (e: Exception) {
      ContextCompat.getColor(root.context, R.color.text_2)
    }
  }
  this.changePercent.setCharacterLists(TickerUtils.provideNumberList())
  this.totalValue.setCharacterLists(TickerUtils.provideNumberList())
  
  root.setOnClickListener {
    listener.invoke(quote)
  }

  val tickerView = ticker
  val nameView = name

  tickerView.text = quote.symbol
  nameView.text = quote.name

  val totalValueText = totalValue
  totalValueText.text = quote.priceFormat.format(quote.lastTradePrice)

  val change: Float = quote.change
  val changePercent: Float = quote.changeInPercent
  val color = when {
    (change < 0f || changePercent < 0f) -> {
      negativeColor
    }
    (change == 0f) -> {
      neutralColor
    }
    else -> {
      positiveColor
    }
  }
  val changeInPercentView = this.changePercent
  changeInPercentView.text = quote.changePercentStringWithSign()
  changeInPercentView.setTextColor(color)
}