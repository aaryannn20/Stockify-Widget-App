package com.starorigins.stockify.widgetapp.debug

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.starorigins.stockify.widgetapp.base.BaseActivity
import com.starorigins.stockify.widgetapp.viewBinding
import com.starorigins.stockify.widgetapp.databinding.ActivityDbViewerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DbViewerActivity : BaseActivity<ActivityDbViewerBinding>() {
	override val binding: (ActivityDbViewerBinding) by viewBinding(ActivityDbViewerBinding::inflate)

  override val simpleName = "DebugViewerActivity"
  override val subscribeToErrorEvents = false

  private val viewModel: DbViewerViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding.toolbar.setNavigationOnClickListener {
      finish()
    }
    binding.webview.settings.allowFileAccess = true

    viewModel.htmlFile.observe(this) {
      binding.webview.loadUrl("file://${it.absolutePath}")
    }

    viewModel.showProgress.observe(this) { show ->
      binding.progress.visibility = if (show) View.VISIBLE else View.GONE
    }

    viewModel.generateDatabaseHtml()
  }
}