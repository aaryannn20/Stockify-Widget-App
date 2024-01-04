package com.starorigins.stockify.widgetapp.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.widget.RemoteViewsService

class RemoteStockProviderService : RemoteViewsService() {

  override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
    val appWidgetId =
      intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
    return RemoteStockViewAdapter(appWidgetId)
  }
}