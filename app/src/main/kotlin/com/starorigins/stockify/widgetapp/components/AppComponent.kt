package com.starorigins.stockify.widgetapp.components

import com.starorigins.stockify.widgetapp.UpdateReceiver
import com.starorigins.stockify.widgetapp.model.RefreshWorker
import com.starorigins.stockify.widgetapp.notifications.DailySummaryNotificationReceiver
import com.starorigins.stockify.widgetapp.widget.RefreshReceiver
import com.starorigins.stockify.widgetapp.widget.RemoteStockViewAdapter
import com.starorigins.stockify.widgetapp.widget.StockWidget
import com.starorigins.stockify.widgetapp.widget.WidgetClickReceiver
import com.starorigins.stockify.widgetapp.widget.WidgetData
import com.google.gson.Gson
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

interface LegacyComponent {
  fun gson(): Gson

  fun inject(widget: StockWidget)

  fun inject(data: WidgetData)

  fun inject(adapter: RemoteStockViewAdapter)

  fun inject(receiver: WidgetClickReceiver)
  fun inject(receiver: RefreshReceiver)
  fun inject(receiver: UpdateReceiver)
  fun inject(receiver: DailySummaryNotificationReceiver)
  fun inject(refreshWorker: RefreshWorker)
}

@InstallIn(SingletonComponent::class)
@EntryPoint
interface AppEntryPoint : LegacyComponent
