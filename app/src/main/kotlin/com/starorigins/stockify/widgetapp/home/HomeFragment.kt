package com.starorigins.stockify.widgetapp.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.starorigins.stockify.widgetapp.base.BaseFragment
import com.starorigins.stockify.widgetapp.components.InAppMessage
import com.starorigins.stockify.widgetapp.isNetworkOnline
import com.starorigins.stockify.widgetapp.portfolio.PortfolioFragment
import com.starorigins.stockify.widgetapp.viewBinding
import com.starorigins.stockify.widgetapp.widget.WidgetDataProvider
import com.starorigins.stockify.widgetapp.R
import com.starorigins.stockify.widgetapp.databinding.FragmentHomeBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.internal.ViewUtils
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(), ChildFragment, PortfolioFragment.Parent {
	override val binding: (FragmentHomeBinding) by viewBinding(FragmentHomeBinding::inflate)
  companion object {
    private const val MAX_FETCH_COUNT = 3
  }

  @Inject internal lateinit var widgetDataProvider: WidgetDataProvider
  override val simpleName: String = "HomeFragment"
  private var attemptingFetch = false
  private var fetchCount = 0
  private lateinit var adapter: HomePagerAdapter
  private val viewModel: HomeViewModel by viewModels()


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
      }

  @SuppressLint("RestrictedApi")
  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    ViewUtils.doOnApplyWindowInsets(view) { _, insets, _ ->
      val statusBarSize = insets.systemWindowInsetTop
      (binding.toolbar.layoutParams as MarginLayoutParams).topMargin = statusBarSize
      (binding.subtitle.layoutParams as MarginLayoutParams).topMargin = statusBarSize
      insets
    }
    binding.swipeContainer.setOnRefreshListener { fetch() }
    adapter = HomePagerAdapter(childFragmentManager, lifecycle)
    binding.viewPager.adapter = adapter
    TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
      tab.text = widgetDataProvider.widgetDataList()[position].widgetName()
    }.attach()
    binding.appBarLayout.addOnOffsetChangedListener(offsetChangedListener)
    viewModel.fetchState.observe(viewLifecycleOwner) {
      updateHeader()
    }
  }

  override fun onDestroyView() {
    binding.appBarLayout.removeOnOffsetChangedListener(offsetChangedListener)
    super.onDestroyView()
  }

  override fun onHiddenChanged(hidden: Boolean) {
    super.onHiddenChanged(hidden)
    if (!hidden) updateHeader()
  }

  private fun updateHeader() {
    binding.tabs.visibility = if (widgetDataProvider.hasWidget()) View.VISIBLE else View.INVISIBLE
    adapter.setData(widgetDataProvider.widgetDataList())
  }


  private fun fetch() {
    if (!attemptingFetch) {
      if (requireActivity().isNetworkOnline()) {
        fetchCount++
        if (fetchCount <= MAX_FETCH_COUNT) {
          attemptingFetch = true
          viewModel.fetch().observe(viewLifecycleOwner) { success ->
            attemptingFetch = false
            binding.swipeContainer.isRefreshing = false
            if (success) {
              update()
            }
          }
        } else {
          attemptingFetch = false
          InAppMessage.showMessage(requireActivity(), R.string.refresh_failed, error = true)
          binding.swipeContainer.isRefreshing = false
        }
      } else {
        attemptingFetch = false
        InAppMessage.showMessage(requireActivity(), R.string.no_network_message, error = true)
        binding.swipeContainer.isRefreshing = false
      }
    }
  }

  private fun update() {
    adapter.notifyDataSetChanged()
    updateHeader()
    fetchCount = 0
  }



  // PortfolioFragment.Parent

  override fun onDragStarted() {
    binding.swipeContainer.isEnabled = false
  }

  override fun onDragEnded() {
    binding.swipeContainer.isEnabled = true
  }

  private val offsetChangedListener = object : AppBarLayout.OnOffsetChangedListener {
    private var isTitleShowing = true

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
      val show = verticalOffset > -binding.tabs.height / 2
      if (show && !isTitleShowing) {
        binding.subtitle.animate().alpha(1f).start()
        binding.tabs.animate().alpha(1f).start()
        isTitleShowing = true
      } else if (!show && isTitleShowing) {
        binding.subtitle.animate().alpha(0f).start()
        binding.tabs.animate().alpha(0f).start()
        isTitleShowing = false
      }
    }
  }

  // ChildFragment

  override fun scrollToTop() {
    val fragment = childFragmentManager.findFragmentByTag("f${binding.viewPager.currentItem}")
    (fragment  as? ChildFragment)?.scrollToTop()
    binding.appBarLayout.setExpanded(true, true)
  }
}