package com.starorigins.stockify.widgetapp.network.data

class PriceFormat(
  val currencyCode: String,
  val symbol: String,
  val prefix: Boolean = true
) {
  fun format(price: Float): String {
    val priceString = com.starorigins.stockify.widgetapp.AppPreferences.SELECTED_DECIMAL_FORMAT.format(price)
    return if (prefix) {
      "$symbol$priceString"
    } else {
      "$priceString$symbol"
    }
  }
}