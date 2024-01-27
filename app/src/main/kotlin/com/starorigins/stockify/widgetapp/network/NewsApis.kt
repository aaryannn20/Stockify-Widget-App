package com.starorigins.stockify.widgetapp.network

import com.starorigins.stockify.widgetapp.network.data.NewsRssFeed
import org.jsoup.nodes.Document
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleNewsApi {
  
  @GET("rss/search/")
  suspend fun getNewsFeed(@Query(value = "q") query: String): NewsRssFeed

  @GET("news/rss/headlines/section/topic/BUSINESS")
  suspend fun getBusinessNews(): NewsRssFeed
}

interface YahooFinanceNewsApi {
  @GET("rssindex")
  suspend fun getNewsFeed(): NewsRssFeed
}

interface YahooFinanceMostActive {
  @GET("most-active")
  suspend fun getMostActive(): Response<Document>
}

interface YahooIndianStockMostActive {
  @GET("screener/unsaved/6101cb2d-0b45-40db-9202-688781758718")
  suspend fun getIndianMostActive(): Response<Document>
}


interface YahooFinanceCrypto {
  @GET("crypto")
  suspend fun getMostCrypto(): Response<Document>
}

interface YahooFinanceIndices {
  @GET("world-indices")
  suspend fun getMostIndices(): Response<Document>
}
