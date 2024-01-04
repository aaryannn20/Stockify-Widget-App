package com.starorigins.stockify.widgetapp.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starorigins.stockify.widgetapp.model.HistoryProvider
import com.starorigins.stockify.widgetapp.model.StocksProvider
import com.starorigins.stockify.widgetapp.network.data.DataPoint
import com.starorigins.stockify.widgetapp.network.data.Quote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GraphViewModel @Inject constructor(
  private val stocksProvider: StocksProvider,
  private val historyProvider: HistoryProvider
): ViewModel() {

  private val _quote = MutableLiveData<Quote>()
  val quote: LiveData<Quote>
    get() = _quote
  private val _error = MutableLiveData<Throwable>()
  val error: LiveData<Throwable>
    get() = _error
  private val _data = MutableLiveData<List<DataPoint>>()
  val data: LiveData<List<DataPoint>>
    get() = _data

  

  fun fetchStock(ticker: String) {
    viewModelScope.launch {
      val fetchResult = stocksProvider.fetchStock(ticker)
      if (!fetchResult.wasSuccessful) {
        _error.value = fetchResult.error
      } else {
        _quote.value = fetchResult.data
      }
    }
  }

  fun fetchHistoricalDataByRange(ticker: String, range: HistoryProvider.Range) {
    viewModelScope.launch {
      val result = historyProvider.fetchDataByRange(ticker, range)
      if (result.wasSuccessful) {
        _data.value = result.data
      } else {
        _error.value = result.error
      }
    }
  }
}