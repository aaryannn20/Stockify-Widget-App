package com.starorigins.stockify.widgetapp.news

import com.starorigins.stockify.widgetapp.network.data.NewsArticle
import com.starorigins.stockify.widgetapp.network.data.Quote

sealed class NewsFeedItem {
  class ArticleNewsFeed(val article: NewsArticle) : NewsFeedItem()
  class TrendingStockNewsFeed(val quotes: List<Quote>) : NewsFeedItem()
}