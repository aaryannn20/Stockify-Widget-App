package com.starorigins.stockify.widgetapp.widget

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.starorigins.stockify.widgetapp.analytics.Analytics
import com.starorigins.stockify.widgetapp.analytics.ClickEvent
import com.starorigins.stockify.widgetapp.components.Injector
import com.starorigins.stockify.widgetapp.home.MainActivity
import javax.inject.Inject

class WidgetClickReceiver : BroadcastReceiver() {

  @Inject internal lateinit var widgetDataProvider: WidgetDataProvider
  @Inject internal lateinit var analytics: Analytics

  override fun onReceive(
    context: Context,
    intent: Intent
  ) {
    Injector.appComponent().inject(this)
    if (intent.getBooleanExtra(FLIP, false)) {
      val widgetId = intent.getIntExtra(WIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
      val widgetData = widgetDataProvider.dataForWidgetId(widgetId)
      widgetData.flipChange()
      widgetDataProvider.broadcastUpdateWidget(widgetId)
      analytics.trackClickEvent(ClickEvent("WidgetFlipClick"))
    } else {
      val startActivityIntent = Intent(context, MainActivity::class.java)
      startActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
      context.startActivity(startActivityIntent)
      analytics.trackClickEvent(ClickEvent("WidgetClick"))
    }
  }

  companion object {

    const val CLICK_BCAST_INTENTFILTER = "com.starorigins.stockify.widgetapp.WIDGET_CLICK"
    const val FLIP = "FLIP"
    const val WIDGET_ID = AppWidgetManager.EXTRA_APPWIDGET_ID
  }
}