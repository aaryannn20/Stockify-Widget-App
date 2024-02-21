package com.starorigins.stockify.widgetapp.portfolio.search

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.starorigins.stockify.widgetapp.base.BaseActivity
import com.starorigins.stockify.widgetapp.viewBinding
import com.starorigins.stockify.widgetapp.R
import com.starorigins.stockify.widgetapp.databinding.ActivitySearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : BaseActivity<ActivitySearchBinding>() {
	override val binding: (ActivitySearchBinding) by viewBinding(ActivitySearchBinding::inflate)
  override val simpleName: String = "SearchActivity"
  private lateinit var firebaseAnalytics: FirebaseAnalytics

  companion object {
    const val ARG_WIDGET_ID = AppWidgetManager.EXTRA_APPWIDGET_ID

    fun launchIntent(
      context: Context,
      widgetId: Int
    ): Intent {
      val intent = Intent(context, SearchActivity::class.java)
      intent.putExtra(ARG_WIDGET_ID, widgetId)
      return intent
    }
  }

  var widgetId: Int = -1

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    widgetId = intent.getIntExtra(ARG_WIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
    firebaseAnalytics = FirebaseAnalytics.getInstance(this)

    if (savedInstanceState == null) {
      supportFragmentManager.beginTransaction()
          .add(R.id.fragment_container, SearchFragment.newInstance(widgetId, showNavIcon = true))
          .commit()
    }

    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
      param(FirebaseAnalytics.Param.ITEM_ID, "SearchActivity")
      param(FirebaseAnalytics.Param.ITEM_NAME, "stockSearch")
      param(FirebaseAnalytics.Param.CONTENT_TYPE, "searchTab")
    }
  }
}