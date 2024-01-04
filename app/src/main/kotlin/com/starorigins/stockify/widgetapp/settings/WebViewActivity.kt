package com.starorigins.stockify.widgetapp.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import com.starorigins.stockify.widgetapp.base.BaseActivity
import com.starorigins.stockify.widgetapp.viewBinding
import com.starorigins.stockify.widgetapp.databinding.ActivityWebviewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebViewActivity : BaseActivity<ActivityWebviewBinding>() {
  override val binding by viewBinding(ActivityWebviewBinding::inflate)
  override var simpleName = "WebViewActivity"
  private lateinit var url: String

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    url = intent.getStringExtra(EXTRA_URL).orEmpty()
    binding.toolbar.setNavigationOnClickListener {
      finish()
    }
    binding.urlText.text = url
    binding.webview.apply {
      this.settings.loadsImagesAutomatically = true
      this.settings.javaScriptEnabled = true
      this.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
      this.webViewClient = WebViewClient()
    }
    binding.webview.loadUrl(url)
  }

  companion object {
    private const val EXTRA_URL = "URL"

    fun newIntent(context: Context, url: String): Intent {
      return Intent(context, WebViewActivity::class.java)
        .putExtra(EXTRA_URL, url)
    }
  }
}