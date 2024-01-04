package com.starorigins.stockify.widgetapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.starorigins.stockify.widgetapp.components.Injector
import com.starorigins.stockify.widgetapp.model.StocksProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class UpdateReceiver : BroadcastReceiver() {

  @Inject internal lateinit var stocksProvider: StocksProvider
  @Inject internal lateinit var coroutineScope: CoroutineScope

  override fun onReceive(
      context: Context,
      intent: Intent
  ) {
    Injector.appComponent().inject(this)
    val pendingResult = goAsync()
    coroutineScope.launch(Dispatchers.Main) {
      stocksProvider.fetch()
      pendingResult.finish()
    }
  }
}