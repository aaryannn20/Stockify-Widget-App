package com.starorigins.stockify.widgetapp.network.data

import com.google.gson.annotations.SerializedName

data class SuggestionsNet(
  @SerializedName("count") var count: Int,
  @SerializedName("quotes") var result: List<SuggestionNet>? = null) {

  data class SuggestionNet(
    @SerializedName("symbol") var symbol: String = ""
  ) {
    @SerializedName("shortname") var name: String = ""
    @SerializedName("longname") var longName: String = ""
    @SerializedName("exchange") var exch: String = ""
    @SerializedName("quoteType") var type: String = ""
    @SerializedName("exchDisp") var exchDisp: String = ""
    @SerializedName("typeDisp") var typeDisp: String = ""
    @SerializedName("score") var score: Long = 0
    @SerializedName("isYahooFinance") var isYahooFinance: Boolean = false
  }
}

