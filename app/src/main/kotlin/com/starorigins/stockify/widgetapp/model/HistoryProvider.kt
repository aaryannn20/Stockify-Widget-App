package com.starorigins.stockify.widgetapp.model

import com.starorigins.stockify.widgetapp.model.HistoryProvider.Range.Companion.FIVE_YEARS
import com.starorigins.stockify.widgetapp.model.HistoryProvider.Range.Companion.ONE_DAY
import com.starorigins.stockify.widgetapp.model.HistoryProvider.Range.Companion.ONE_MONTH
import com.starorigins.stockify.widgetapp.model.HistoryProvider.Range.Companion.ONE_WEEK
import com.starorigins.stockify.widgetapp.model.HistoryProvider.Range.Companion.ONE_YEAR
import com.starorigins.stockify.widgetapp.model.HistoryProvider.Range.Companion.THREE_MONTH
import com.starorigins.stockify.widgetapp.network.ChartApi
import com.starorigins.stockify.widgetapp.network.data.DataPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import timber.log.Timber
import java.io.Serializable
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryProvider @Inject constructor(
  private val chartApi: ChartApi
) {

  private var cachedData: WeakReference<Pair<String, List<DataPoint>>>? = null

  suspend fun fetchDataShort(symbol: String): FetchResult<List<DataPoint>> = withContext(Dispatchers.IO) {
    val dataPoints =  try {
      if (symbol == cachedData?.get()?.first) {
        cachedData!!.get()!!.second.filter {
          it.getDate().isAfter(Range.ONE_DAY.end)
        }.toMutableList().sorted()
      } else {
        val fetchDataByRange = fetchDataByRange(symbol, Range.ONE_DAY)
        if (fetchDataByRange.wasSuccessful) {
          cachedData = WeakReference(Pair(symbol, fetchDataByRange.data))
          fetchDataByRange.data
        } else {
          return@withContext FetchResult.failure(
              FetchException("Error fetching datapoints", fetchDataByRange.error)
          )
        }
      }
    } catch (ex: Exception) {
      return@withContext FetchResult.failure(
          FetchException("Error fetching datapoints", ex)
      )
    }
    return@withContext FetchResult.success(dataPoints)
  }

  suspend fun fetchDataByRange(
    symbol: String,
    range: Range
  ): FetchResult<List<DataPoint>> = withContext(Dispatchers.IO) {
    val dataPoints = try {
      val historicalData =
        chartApi.fetchChartData(
            symbol = symbol, interval = range.intervalParam(), range = range.rangeParam()
        )
      with(historicalData.chart.result.first()) {
        timestamp.mapIndexed { i, stamp ->
          val dataQuote = indicators.quote.first()
        if (dataQuote.high[i] === null
            || dataQuote.low[i] === null
            || dataQuote.open[i] === null
            || dataQuote.close[i] === null) {
            null
          } else {
            DataPoint(
              stamp.toFloat(),
              dataQuote.high[i]!!.toFloat(),
              dataQuote.low[i]!!.toFloat(),
              dataQuote.open[i]!!.toFloat(),
              dataQuote.close[i]!!.toFloat()
            )
          }
        }.filterNotNull()
      }.toMutableList().sorted()
    } catch (ex: Exception) {
      Timber.w(ex)
      return@withContext FetchResult.failure(
          FetchException("Error fetching datapoints", ex)
      )
    }
    return@withContext FetchResult.success(dataPoints)
  }

  private fun Range.intervalParam(): String {
    return when(this) {
      ONE_DAY -> "1h"
      ONE_WEEK -> "1h"
      else -> "1d"
    }
  }

  private fun Range.rangeParam(): String {
    return when (this) {
      ONE_DAY -> "1d"
      ONE_WEEK -> "7d"
      ONE_MONTH -> "1mo"
      THREE_MONTH -> "3mo"
      ONE_YEAR -> "1y"
      FIVE_YEARS -> "5y"
      else -> "max"
    }
  }

  sealed class Range(val duration: Duration) : Serializable {
    val end = LocalDate.now().minusDays(duration.toDays())
    class DateRange(duration: Duration) : Range(duration)
    companion object {
      val ONE_DAY = DateRange(Duration.ofDays(1))
      val ONE_WEEK = DateRange(Duration.ofDays(7))
      val ONE_MONTH = DateRange(Duration.ofDays(30))
      val THREE_MONTH = DateRange(Duration.ofDays(90))
      val ONE_YEAR = DateRange(Duration.ofDays(365))
      val FIVE_YEARS = DateRange(Duration.ofDays(5 * 365))
    }
  }
}