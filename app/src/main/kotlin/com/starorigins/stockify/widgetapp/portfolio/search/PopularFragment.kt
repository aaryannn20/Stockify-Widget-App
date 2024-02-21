package com.starorigins.stockify.widgetapp.portfolio.search

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.starorigins.stockify.widgetapp.R
import com.starorigins.stockify.widgetapp.analytics.ClickEvent
import com.starorigins.stockify.widgetapp.base.BaseFragment
import com.starorigins.stockify.widgetapp.components.InAppMessage
import com.starorigins.stockify.widgetapp.databinding.FragmentPopularBinding
import com.starorigins.stockify.widgetapp.databinding.FragmentSearchBinding
import com.starorigins.stockify.widgetapp.home.ChildFragment
import com.starorigins.stockify.widgetapp.isNetworkOnline
import com.starorigins.stockify.widgetapp.news.QuoteDetailActivity
import com.starorigins.stockify.widgetapp.portfolio.PortfolioFragment
import com.starorigins.stockify.widgetapp.portfolio.drag_drop.SimpleItemTouchHelperCallback
import com.starorigins.stockify.widgetapp.showDialog
import com.starorigins.stockify.widgetapp.ui.SpacingDecoration
import com.starorigins.stockify.widgetapp.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PopularFragment: BaseFragment<FragmentPopularBinding>(), ChildFragment, TextWatcher {
    override val binding: (FragmentPopularBinding) by viewBinding(FragmentPopularBinding::inflate)

    companion object {
        private const val ARG_WIDGET_ID = AppWidgetManager.EXTRA_APPWIDGET_ID
        private const val ARG_SHOW_NAV_ICON = "SHOW_NAV_ICON"

        fun newInstance(
            widgetId: Int,
            showNavIcon: Boolean = false
        ): PopularFragment {
            val fragment = PopularFragment()
            val args = Bundle()
            args.putInt(ARG_WIDGET_ID, widgetId)
            args.putBoolean(ARG_SHOW_NAV_ICON, showNavIcon)
            fragment.arguments = args
            return fragment
        }
    }

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var trendingAdapter: TrendingStocksAdapter
    private var selectedWidgetId: Int = -1
    override val simpleName: String = "Popular Fragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            setData(it)
        }
        firebaseAnalytics = Firebase.analytics
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trendingAdapter = TrendingStocksAdapter { quote ->
            analytics.trackClickEvent(ClickEvent("InstrumentClick"))
            val intent = Intent(requireContext(), QuoteDetailActivity::class.java)
            intent.putExtra(QuoteDetailActivity.TICKER, quote.symbol)
            startActivity(intent)

            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "TrendingStock")
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "TrendingStockClicked")
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)

        }


            binding.trendingRV.layoutManager = GridLayoutManager(activity, 1)
            binding.trendingRV.addItemDecoration(
            SpacingDecoration(requireContext().resources.getDimensionPixelSize(R.dimen.list_spacing_double))
            )
            binding.trendingRV.adapter = trendingAdapter


        viewModel.fetchTrendingStocks().observe(viewLifecycleOwner) { quotes ->
            if (quotes.isNotEmpty()) {

                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "TrendingStock")
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "TrendingStockClicked")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)

                trendingAdapter.setData(quotes)
            }
        }

    }

    override fun scrollToTop() {
        binding.trendingRV.smoothScrollToPosition(0)

    }
    override fun setData(bundle: Bundle) {
        selectedWidgetId = bundle.getInt(ARG_WIDGET_ID, -1)
    }

    override fun beforeTextChanged(
        s: CharSequence,
        start: Int,
        count: Int,
        after: Int
    ) {
        // Do nothing.
    }

    override fun onTextChanged(
        s: CharSequence,
        start: Int,
        before: Int,
        count: Int
    ) {
        // Do nothing.
    }


    override fun afterTextChanged(s: Editable) {
        val query = s.toString()
            .trim { it <= ' ' }
            .replace(" ".toRegex(), "")
        if (query.isNotEmpty()) {
            binding.trendingHolder.isVisible = false
            if (requireActivity().isNetworkOnline()) {
                viewModel.fetchResults(query)
            } else {
                InAppMessage.showToast(requireActivity(), R.string.no_network_message)
            }
        } else {
            binding.trendingHolder.isVisible = true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(ARG_WIDGET_ID, selectedWidgetId)
        super.onSaveInstanceState(outState)
    }

}