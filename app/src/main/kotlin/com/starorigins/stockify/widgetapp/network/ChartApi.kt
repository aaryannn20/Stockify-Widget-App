package com.starorigins.stockify.widgetapp.network

import com.starorigins.stockify.widgetapp.network.data.HistoricalDataResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ChartApi {

  @GET("chart/{symbol}")
  suspend fun fetchChartData(
    @Path("symbol") symbol: String,
    @Query(value = "interval") interval: String,
    @Query(value = "range") range: String
  ): HistoricalDataResult
}