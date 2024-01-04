package com.starorigins.stockify.widgetapp.network

import com.starorigins.stockify.widgetapp.network.data.TrendingResult
import retrofit2.http.GET

interface ApeWisdom {

  @GET("filter/stocks")
  suspend fun getTrendingStocks(): TrendingResult
}