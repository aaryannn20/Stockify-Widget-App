package com.starorigins.stockify.widgetapp.portfolio.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.starorigins.stockify.widgetapp.model.FetchResult
import com.starorigins.stockify.widgetapp.model.StocksProvider
import com.starorigins.stockify.widgetapp.network.NewsProvider
import com.starorigins.stockify.widgetapp.network.StocksApi
import com.starorigins.stockify.widgetapp.network.data.Quote
import com.starorigins.stockify.widgetapp.network.data.Suggestion
import com.starorigins.stockify.widgetapp.network.data.SuggestionsNet.SuggestionNet
import com.starorigins.stockify.widgetapp.widget.WidgetData
import com.starorigins.stockify.widgetapp.widget.WidgetDataProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
  private val stocksApi: StocksApi,
  private val widgetDataProvider: WidgetDataProvider,
  private val stocksProvider: StocksProvider,
  private val newsProvider: NewsProvider
) : ViewModel() {

  val searchResult: LiveData<FetchResult<List<Suggestion>>>
    get() = _searchResult
  private val _searchResult: MutableLiveData<FetchResult<List<Suggestion>>> = MutableLiveData()
  private var searchJob: Job? = null

  fun fetchResults(query: String) {
    if (query.isEmpty()) return
    searchJob?.cancel()
    searchJob = viewModelScope.launch(Dispatchers.Default) {
      delay(500L)
      val suggestions = stocksApi.getSuggestions(query)
      if (suggestions.wasSuccessful) {
        val suggestionList = suggestions.data.toMutableList()
        val querySuggestion = SuggestionNet(query.uppercase())
        if (!suggestionList.contains(querySuggestion)) {
          suggestionList.add(querySuggestion)
        }
        val data = suggestionList.map {
          ensureActive()
          val sug = Suggestion.fromSuggestionNet(it)
          sug.exists = doesSuggestionExist(sug)
          sug
        }
        _searchResult.postValue(FetchResult.success(data))
      } else {
        Timber.w(suggestions.error)
        _searchResult.postValue(FetchResult.failure(suggestions.error))
      }
    }
  }

  fun fetchTrendingStocks(): LiveData<List<Quote>> = liveData {
    val trendingResult = newsProvider.fetchTrendingStocks(true)
    if (trendingResult.wasSuccessful) {
      emit(trendingResult.data)
    } else emit(emptyList())
  }

  fun fetchIndianStocks(): LiveData<List<Quote>> = liveData {
    val trendingResult = newsProvider.fetchIndianStocks(true)
    if (trendingResult.wasSuccessful) {
      emit(trendingResult.data)
    } else emit(emptyList())
  }

  fun fetchIndices(): LiveData<List<Quote>> = liveData {
    val trendingResult = newsProvider.fetchIndices(true)
    if (trendingResult.wasSuccessful) {
      emit(trendingResult.data)
    } else emit(emptyList())
  }


  fun fetchCrypto(): LiveData<List<Quote>> = liveData {
    val trendingResult = newsProvider.fetchTrendingCrypto(true)
    if (trendingResult.wasSuccessful) {
      emit(trendingResult.data)
    } else emit(emptyList())
  }

  fun doesSuggestionExist(sug: Suggestion) =
    stocksProvider.hasTicker(sug.symbol) && widgetDataProvider.widgetCount <= 1

  fun addTickerToWidget(
    ticker: String,
    widgetId: Int
  ): Boolean {
    val widgetData = widgetDataProvider.dataForWidgetId(widgetId)
    return if (!widgetData.hasTicker(ticker)) {
      widgetData.addTicker(ticker)
      widgetDataProvider.broadcastUpdateWidget(widgetId)
      true
    } else {
      false
    }
  }

  fun removeStock(
    ticker: String,
    selectedWidgetId: Int
  ) {
    if (selectedWidgetId > 0) {
      val widgetData = widgetDataProvider.dataForWidgetId(selectedWidgetId)
      widgetData.removeStock(ticker)
    } else {
      val widgetData = widgetDataProvider.widgetDataWithStock(ticker)
      widgetData.forEach { it.removeStock(ticker) }
    }
  }

  fun getWidgetDatas(): List<WidgetData> {
    val widgetIds = widgetDataProvider.getAppWidgetIds()
    return widgetIds.map { widgetDataProvider.dataForWidgetId(it) }
        .sortedBy { it.widgetName() }
  }

  fun hasWidget(): Boolean {
    return widgetDataProvider.hasWidget()
  }
}