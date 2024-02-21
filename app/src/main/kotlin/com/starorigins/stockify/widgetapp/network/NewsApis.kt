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
  @GET("screener/predefined/09a035cc-f535-4249-bfae-ed95e7a9e12e?.tsrc=fin-srch")
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
