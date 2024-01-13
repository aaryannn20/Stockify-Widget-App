package com.starorigins.stockify.widgetapp.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starorigins.stockify.widgetapp.model.StocksProvider
import com.starorigins.stockify.widgetapp.network.data.Properties
import com.starorigins.stockify.widgetapp.network.data.Quote
import com.starorigins.stockify.widgetapp.repo.StocksStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertsViewModel @Inject constructor(
  private val stocksProvider: StocksProvider,
  private val stocksStorage: StocksStorage
): ViewModel() {

  lateinit var symbol: String
  val quote: Quote?
    get() = stocksProvider.getStock(symbol)

  

  fun setAlerts(
    alertAbove: Float,
    alertBelow: Float
  ) {
    viewModelScope.launch {
      quote?.let {
        val properties = it.properties ?: Properties(
            symbol
        )
        it.properties = properties.apply {
          this.alertAbove = alertAbove
          this.alertBelow = alertBelow
        }
        stocksStorage.saveQuoteProperties(properties)
      }
    }
  }
}