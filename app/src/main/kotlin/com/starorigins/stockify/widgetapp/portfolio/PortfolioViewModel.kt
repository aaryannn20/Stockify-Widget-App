package com.starorigins.stockify.widgetapp.portfolio

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.starorigins.stockify.widgetapp.model.StocksProvider
import com.starorigins.stockify.widgetapp.network.data.Quote
import com.starorigins.stockify.widgetapp.widget.WidgetData
import com.starorigins.stockify.widgetapp.widget.WidgetDataProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor(
  private val widgetDataProvider: WidgetDataProvider,
  private val stocksProvider: StocksProvider
) : ViewModel() {

  val portfolio: LiveData<List<Quote>> by lazy {
    stocksProvider.portfolio.asLiveData()
  }

  fun dataForWidgetId(widgetId: Int): WidgetData {
    return widgetDataProvider.dataForWidgetId(widgetId)
  }

  fun removeStock(widgetId: Int, ticker: String) {
    viewModelScope.launch {
      dataForWidgetId(widgetId).removeStock(ticker)
      if (!widgetDataProvider.containsTicker(ticker)) {
        stocksProvider.removeStock(ticker)
      }
    }
  }

  fun broadcastUpdateWidget(widgetId: Int) {
    widgetDataProvider.broadcastUpdateWidget(widgetId)
  }

  fun fetchPortfolioInRealTime() {
    viewModelScope.launch(Dispatchers.Default) {
      do {
        var isMarketOpen = false
        val result = stocksProvider.fetch(false)
        if (result.wasSuccessful) {
          isMarketOpen = result.data.any { it.isMarketOpen }
        }
        delay(StocksProvider.DEFAULT_INTERVAL_MS)
      } while (result.wasSuccessful && isMarketOpen)
    }
  }
}