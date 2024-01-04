package com.starorigins.stockify.widgetapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.starorigins.stockify.widgetapp.analytics.Analytics
import com.starorigins.stockify.widgetapp.components.Injector
import com.starorigins.stockify.widgetapp.notifications.NotificationsHandler
import com.starorigins.stockify.widgetapp.widget.WidgetDataProvider
import com.google.android.material.color.DynamicColors
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
open class StocksApp : Application() {

  @Inject lateinit var analytics: Analytics
  @Inject lateinit var appPreferences: com.starorigins.stockify.widgetapp.AppPreferences
  @Inject lateinit var notificationsHandler: NotificationsHandler
  @Inject lateinit var widgetDataProvider: WidgetDataProvider

  override fun onCreate() {
    initLogger()
    initThreeTen()
    Injector.init(this)
    super.onCreate()
    DynamicColors.applyToActivitiesIfAvailable(this)
    AppCompatDelegate.setDefaultNightMode(appPreferences.nightMode)
    initNotificationHandler()
    widgetDataProvider.widgetDataList()
  }

  protected open fun initNotificationHandler() {
    notificationsHandler.initialize()
  }

  protected open fun initThreeTen() {
    AndroidThreeTen.init(this)
  }

  protected open fun initLogger() {
    Timber.plant(com.starorigins.stockify.widgetapp.components.LoggingTree())
  }
}