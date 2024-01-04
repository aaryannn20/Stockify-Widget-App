package com.starorigins.stockify.widgetapp.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.starorigins.stockify.widgetapp.analytics.Analytics
import com.starorigins.stockify.widgetapp.model.StocksProvider
import com.starorigins.stockify.widgetapp.model.StocksProvider.FetchState
import com.starorigins.stockify.widgetapp.R
import com.google.android.material.color.DynamicColors
import com.starorigins.stockify.widgetapp.showDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

abstract class BaseActivity<T: ViewBinding> : AppCompatActivity() {

  abstract val simpleName: String
  abstract val binding: T
  open val subscribeToErrorEvents = true
  private var isErrorDialogShowing = false
  @Inject lateinit var analytics: Analytics
  @Inject lateinit var stocksProvider: StocksProvider
  @Inject lateinit var appPreferences: com.starorigins.stockify.widgetapp.AppPreferences

  override fun onCreate(
      savedInstanceState: Bundle?
  ) {
    super.onCreate(savedInstanceState)
    DynamicColors.applyToActivityIfAvailable(this)
    if (appPreferences.themePref == com.starorigins.stockify.widgetapp.AppPreferences.JUST_BLACK_THEME) {
      theme.applyStyle(R.style.AppTheme_Overlay_JustBlack, true)
    }
    setContentView(binding.root)
    savedInstanceState?.let { isErrorDialogShowing = it.getBoolean(IS_ERROR_DIALOG_SHOWING, false) }
  }

  override fun onPostCreate(savedInstanceState: Bundle?) {
    super.onPostCreate(savedInstanceState)
    analytics.trackScreenView(simpleName, this)
  }

  override fun onResume() {
    super.onResume()
    if (subscribeToErrorEvents) {
      lifecycleScope.launch {
        stocksProvider.fetchState.collect { state ->
          if (state is FetchState.Failure) {
            if (this.isActive && !isErrorDialogShowing && !isFinishing) {
              isErrorDialogShowing = true
              showDialog(state.displayString).setOnDismissListener { isErrorDialogShowing = false }
              delay(500L)
            }
          }
        }
      }
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putBoolean(IS_ERROR_DIALOG_SHOWING, isErrorDialogShowing)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    try {
      super.onRestoreInstanceState(savedInstanceState)
    } catch (ex: Throwable) {
      // android bug
      Timber.w(ex)
    }
  }

  override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
    if (item.itemId == android.R.id.home) {
      onBackPressed()
      return true
    }
    return super.onOptionsItemSelected(item)
  }

  companion object {
    private const val IS_ERROR_DIALOG_SHOWING = "IS_ERROR_DIALOG_SHOWING"
  }
}
