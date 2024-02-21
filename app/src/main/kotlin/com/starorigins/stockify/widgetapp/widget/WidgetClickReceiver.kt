package com.starorigins.stockify.widgetapp.widget

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.starorigins.stockify.widgetapp.analytics.Analytics
import com.starorigins.stockify.widgetapp.analytics.ClickEvent
import com.starorigins.stockify.widgetapp.components.Injector
import com.starorigins.stockify.widgetapp.home.MainActivity
import javax.inject.Inject


class WidgetClickReceiver : BroadcastReceiver() {

  @Inject internal lateinit var widgetDataProvider: WidgetDataProvider
  @Inject internal lateinit var analytics: Analytics
  private lateinit var firebaseAnalytics: FirebaseAnalytics

  override fun onReceive(
    context: Context,
    intent: Intent
  ) {
    firebaseAnalytics = Firebase.analytics
    Injector.appComponent().inject(this)
    if (intent.getBooleanExtra(FLIP, false)) {
      val widgetId = intent.getIntExtra(WIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
      val widgetData = widgetDataProvider.dataForWidgetId(widgetId)
      widgetData.flipChange()
      widgetDataProvider.broadcastUpdateWidget(widgetId)
      analytics.trackClickEvent(ClickEvent("WidgetFlipClick"))
      firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM){
        param(FirebaseAnalytics.Param.ITEM_NAME,"WidgetFlipClick")
      }

    } else {
      val startActivityIntent = Intent(context, MainActivity::class.java)
      startActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
      context.startActivity(startActivityIntent)
      analytics.trackClickEvent(ClickEvent("WidgetClick"))
      firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM){
        param(FirebaseAnalytics.Param.ITEM_NAME,"WidgetClick")
      }

    }
  }

  companion object {

    const val CLICK_BCAST_INTENTFILTER = "com.starorigins.stockify.widgetapp.WIDGET_CLICK"
    const val FLIP = "FLIP"
    const val WIDGET_ID = AppWidgetManager.EXTRA_APPWIDGET_ID
  }
}