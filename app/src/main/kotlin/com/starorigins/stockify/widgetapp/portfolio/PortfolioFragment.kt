package com.starorigins.stockify.widgetapp.portfolio

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.starorigins.stockify.widgetapp.analytics.ClickEvent
import com.starorigins.stockify.widgetapp.base.BaseFragment
import com.starorigins.stockify.widgetapp.components.InAppMessage
import com.starorigins.stockify.widgetapp.home.ChildFragment
import com.starorigins.stockify.widgetapp.network.data.Quote
import com.starorigins.stockify.widgetapp.news.QuoteDetailActivity
import com.starorigins.stockify.widgetapp.portfolio.StocksAdapter.QuoteClickListener
import com.starorigins.stockify.widgetapp.portfolio.drag_drop.OnStartDragListener
import com.starorigins.stockify.widgetapp.portfolio.drag_drop.SimpleItemTouchHelperCallback
import com.starorigins.stockify.widgetapp.ui.SpacingDecoration
import com.starorigins.stockify.widgetapp.viewBinding
import com.starorigins.stockify.widgetapp.R
import com.starorigins.stockify.widgetapp.databinding.FragmentPortfolioBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PortfolioFragment : BaseFragment<FragmentPortfolioBinding>(), ChildFragment, QuoteClickListener, OnStartDragListener {
	override val binding: (FragmentPortfolioBinding) by viewBinding(FragmentPortfolioBinding::inflate)
  interface Parent {
    fun onDragStarted()
    fun onDragEnded()
  }

  companion object {
    private const val LIST_INSTANCE_STATE = "LIST_INSTANCE_STATE"
    private const val KEY_WIDGET_ID = "KEY_WIDGET_ID"

    fun newInstance(widgetId: Int): PortfolioFragment {
      val fragment = PortfolioFragment()
      val args = Bundle()
      args.putInt(KEY_WIDGET_ID, widgetId)
      fragment.arguments = args
      return fragment
    }

    fun newInstance(): PortfolioFragment {
      val fragment = PortfolioFragment()
      val args = Bundle()
      args.putInt(KEY_WIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
      fragment.arguments = args
      return fragment
    }
  }

  override val simpleName: String = "PortfolioFragment"
  private val viewModel: PortfolioViewModel by viewModels()
  private val parent: Parent
    get() = parentFragment as Parent
  private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID
  private val stocksAdapter by lazy {
    val widgetData = viewModel.dataForWidgetId(widgetId)
    StocksAdapter(widgetData, this as QuoteClickListener, this as OnStartDragListener)
  }
  private var itemTouchHelper: ItemTouchHelper? = null

  override fun onOpenQuote(
      view: View,
      quote: Quote,
      position: Int
  ) {
    analytics.trackClickEvent(ClickEvent("InstrumentClick"))
    val intent = Intent(view.context, QuoteDetailActivity::class.java)
    intent.putExtra(QuoteDetailActivity.TICKER, quote.symbol)
    startActivity(intent)
  }

  override fun onClickQuoteOptions(
      view: View,
      quote: Quote,
      position: Int
  ) {
    val popupWindow = PopupMenu(view.context, view)
    popupWindow.menuInflater.inflate(R.menu.menu_portfolio, popupWindow.menu)
    popupWindow.setOnMenuItemClickListener { menuItem ->
      when (menuItem.itemId) {
        R.id.remove -> {
          remove(quote)
        }
      }
      true
    }
    popupWindow.show()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    widgetId = requireArguments().getInt(KEY_WIDGET_ID)
  }

  override fun onViewCreated(
      view: View,
      savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    binding.stockList.addItemDecoration(
        SpacingDecoration(requireContext().resources.getDimensionPixelSize(R.dimen.list_spacing_double))
    )
    val gridLayoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 1)
    binding.stockList.layoutManager = gridLayoutManager
    binding.stockList.adapter = stocksAdapter
    val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(stocksAdapter)
    itemTouchHelper = ItemTouchHelper(callback)
    itemTouchHelper?.attachToRecyclerView(binding.stockList)

    savedInstanceState?.let { state ->
      val listViewState = state.getParcelable<Parcelable>(LIST_INSTANCE_STATE)
      listViewState?.let { binding.stockList.layoutManager?.onRestoreInstanceState(it) }
    }
    val widgetData = viewModel.dataForWidgetId(widgetId)
    if (widgetData.getTickers().isEmpty()) {
      binding.viewFlipper.displayedChild = 0
    } else {
      binding.viewFlipper.displayedChild = 1
    }
    viewModel.portfolio.observe(viewLifecycleOwner) {
      stocksAdapter.refresh()
    }
    viewModel.fetchPortfolioInRealTime()
    lifecycleScope.launch {
      widgetData.autoSortEnabled.collect {
        update()
      }
    }
  }

  private fun update() {
    stocksAdapter.refresh()
  }

  private fun remove(quote: Quote) {
    viewModel.removeStock(widgetId, quote.symbol)
    stocksAdapter.refresh()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    val listViewState = binding.stockList.layoutManager?.onSaveInstanceState()
    listViewState?.let {
      outState.putParcelable(LIST_INSTANCE_STATE, it)
    }
  }

  override fun onStartDrag(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
    parent.onDragStarted()
    val widgetData = viewModel.dataForWidgetId(widgetId)
    itemTouchHelper?.startDrag(viewHolder)
  }

  override fun onStopDrag() {
    parent.onDragEnded()
    val widgetData = viewModel.dataForWidgetId(widgetId)
    widgetData.setAutoSort(false)
    viewModel.broadcastUpdateWidget(widgetId)
  }

  override fun setData(bundle: Bundle) {
  }

  override fun scrollToTop() {
    binding.stockList.smoothScrollToPosition(0)
  }
}