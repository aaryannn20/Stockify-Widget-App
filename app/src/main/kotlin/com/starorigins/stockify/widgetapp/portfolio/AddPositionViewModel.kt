package com.starorigins.stockify.widgetapp.portfolio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.starorigins.stockify.widgetapp.model.StocksProvider
import com.starorigins.stockify.widgetapp.network.data.Holding
import com.starorigins.stockify.widgetapp.network.data.Position
import com.starorigins.stockify.widgetapp.network.data.Quote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPositionViewModel @Inject constructor(private val stocksProvider: StocksProvider) : ViewModel() {

  val quote: LiveData<Quote>
    get() = _quote
  private val _quote = MutableLiveData<Quote>()

  fun getPosition(symbol: String): Position? {
    return stocksProvider.getPosition(symbol)
  }

  fun loadQuote(symbol: String) = viewModelScope.launch {
    _quote.value = getQuote(symbol)
  }

  fun removePosition(symbol: String, holding: Holding) {
    viewModelScope.launch {
      stocksProvider.removePosition(symbol, holding)
      loadQuote(symbol)
    }
  }

  fun addHolding(symbol: String, shares: Float, price: Float) = liveData {
    val holding = stocksProvider.addHolding(symbol, shares, price)
    emit(holding)
    loadQuote(symbol)
  }

  private fun getQuote(symbol: String): Quote {
    return checkNotNull(stocksProvider.getStock(symbol))
  }
}