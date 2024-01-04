package com.starorigins.stockify.widgetapp.model

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.starorigins.stockify.widgetapp.components.Injector
import com.starorigins.stockify.widgetapp.isNetworkOnline
import javax.inject.Inject

class RefreshWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

  companion object {
    const val TAG = "RefreshWorker"
    const val TAG_PERIODIC = "RefreshWorker_Periodic"
  }

  @Inject internal lateinit var stocksProvider: StocksProvider

  override suspend fun doWork(): Result {
    return if (applicationContext.isNetworkOnline()) {
      Injector.appComponent().inject(this)
      val result = stocksProvider.fetch()
      if (result.hasError) {
        Result.retry()
      } else {
        Result.success()
      }
    } else {
      Result.retry()
    }
  }
}