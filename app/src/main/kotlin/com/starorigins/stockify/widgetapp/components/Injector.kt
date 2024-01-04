package com.starorigins.stockify.widgetapp.components

import com.starorigins.stockify.widgetapp.StocksApp
import dagger.hilt.EntryPoints


object Injector {

  private lateinit var app: StocksApp

  fun init(app: StocksApp) {
    this.app = app
  }

  fun appComponent(): AppEntryPoint {
    return EntryPoints.get(app, AppEntryPoint::class.java)
  }
}