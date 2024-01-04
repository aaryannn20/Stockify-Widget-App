package com.starorigins.stockify.widgetapp.network

import com.starorigins.stockify.widgetapp.network.data.SuggestionsNet
import retrofit2.http.GET
import retrofit2.http.Query

interface SuggestionApi {

  @GET("search?quotesCount=20&newsCount=0&listsCount=0&enableFuzzyQuery=false")
  suspend fun getSuggestions(@Query("q") query: String): SuggestionsNet

}
