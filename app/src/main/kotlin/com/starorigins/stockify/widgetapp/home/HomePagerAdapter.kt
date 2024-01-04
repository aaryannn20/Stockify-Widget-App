package com.starorigins.stockify.widgetapp.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.starorigins.stockify.widgetapp.portfolio.PortfolioFragment
import com.starorigins.stockify.widgetapp.widget.WidgetData

class HomePagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {

  private var widgetDataList = emptyList<WidgetData>()

  fun setData(widgetData: List<WidgetData>) {
    this.widgetDataList = widgetData
    notifyDataSetChanged()
  }

  override fun getItemCount(): Int {
    return widgetDataList.size
  }

  override fun getItemId(position: Int): Long {
    return super.getItemId(position)
  }

  override fun createFragment(position: Int): Fragment {
    return if (widgetDataList.isEmpty()) {
      PortfolioFragment.newInstance()
    } else {
      PortfolioFragment.newInstance(widgetDataList[position].widgetId)
    }
  }
}