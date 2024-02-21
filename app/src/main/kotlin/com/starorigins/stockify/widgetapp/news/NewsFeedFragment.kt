package com.starorigins.stockify.widgetapp.news

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.starorigins.stockify.widgetapp.CustomTabs
import com.starorigins.stockify.widgetapp.analytics.ClickEvent
import com.starorigins.stockify.widgetapp.base.BaseFragment
import com.starorigins.stockify.widgetapp.components.InAppMessage
import com.starorigins.stockify.widgetapp.home.ChildFragment
import com.starorigins.stockify.widgetapp.network.data.NewsArticle
import com.starorigins.stockify.widgetapp.network.data.Quote
import com.starorigins.stockify.widgetapp.ui.SpacingDecoration
import com.starorigins.stockify.widgetapp.viewBinding
import com.starorigins.stockify.widgetapp.R
import com.starorigins.stockify.widgetapp.R.dimen
import com.starorigins.stockify.widgetapp.databinding.FragmentNewsFeedBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsFeedFragment : BaseFragment<FragmentNewsFeedBinding>(), ChildFragment, TrendingAdapter.TrendingListener {

  companion object {
    private const val INDEX_PROGRESS = 0
    private const val INDEX_ERROR = 1
    private const val INDEX_EMPTY = 2
    private const val INDEX_DATA = 3
  }

  override val binding: (FragmentNewsFeedBinding) by viewBinding(FragmentNewsFeedBinding::inflate)
  private lateinit var adapter: TrendingAdapter
  private lateinit var firebaseAnalytics: FirebaseAnalytics
  private val viewModel: NewsFeedViewModel by viewModels()
  override val simpleName: String
    get() = "NewsFeedFragment"

  override fun onViewCreated(
      view: View,
      savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
      binding.toolbar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
        this.topMargin = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
      }
      insets
    }

    firebaseAnalytics = Firebase.analytics

    adapter = TrendingAdapter(this)
    binding.recyclerView.layoutManager = LinearLayoutManager(activity)
    binding.recyclerView.addItemDecoration(
        SpacingDecoration(requireContext().resources.getDimensionPixelSize(dimen.list_spacing_double))
    )
    binding.recyclerView.adapter = adapter
    binding.swipeContainer!!.setOnRefreshListener { refreshNews() }
    viewModel.newsFeed.observe(viewLifecycleOwner) {
      if (it.wasSuccessful) {
        if (it.data.isEmpty()) {
          binding.viewFlipper!!.displayedChild = INDEX_EMPTY
        } else {
          adapter.setData(it.data)
          binding.viewFlipper!!.displayedChild = INDEX_DATA
        }
      } else {
        InAppMessage.showMessage(requireActivity(), R.string.news_fetch_failed, error = true)
        if (adapter.itemCount == 0) {
          binding.viewFlipper!!.displayedChild = INDEX_ERROR
        } else {
          binding.viewFlipper!!.displayedChild = INDEX_DATA
        }
      }
      binding.swipeContainer!!.isRefreshing = false
    }
  }

  override fun onStart() {
    super.onStart()
    binding.viewFlipper!!.displayedChild = INDEX_PROGRESS
    viewModel.fetchNews()
  }

  private fun refreshNews() {
    viewModel.fetchNews(forceRefresh = true)
  }

  // TrendingAdapter.TrendingListener

  override fun onClickNewsArticle(article: NewsArticle) {
    CustomTabs.openTab(requireContext(), article.url)
    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM){
      param(FirebaseAnalytics.Param.METHOD,"newsClicked")
    }
  }

  // Child Fragment
  override fun scrollToTop() {
    binding.recyclerView.smoothScrollToPosition(0)
  }
}