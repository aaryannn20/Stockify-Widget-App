package com.starorigins.stockify.widgetapp.portfolio.search

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.starorigins.stockify.widgetapp.R
import com.starorigins.stockify.widgetapp.analytics.ClickEvent
import com.starorigins.stockify.widgetapp.base.BaseFragment
import com.starorigins.stockify.widgetapp.components.InAppMessage
import com.starorigins.stockify.widgetapp.databinding.FragmentIndBinding
import com.starorigins.stockify.widgetapp.databinding.FragmentPopularBinding
import com.starorigins.stockify.widgetapp.home.ChildFragment
import com.starorigins.stockify.widgetapp.isNetworkOnline
import com.starorigins.stockify.widgetapp.news.QuoteDetailActivity
import com.starorigins.stockify.widgetapp.ui.SpacingDecoration
import com.starorigins.stockify.widgetapp.viewBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class IndFragment: BaseFragment<FragmentIndBinding>(), ChildFragment, TextWatcher {
    override val binding: (FragmentIndBinding) by viewBinding(FragmentIndBinding::inflate)

    companion object {
        private const val ARG_WIDGET_ID = AppWidgetManager.EXTRA_APPWIDGET_ID
        private const val ARG_SHOW_NAV_ICON = "SHOW_NAV_ICON"

        fun newInstance(
            widgetId: Int,
            showNavIcon: Boolean = false
        ): IndFragment {
            val fragment = IndFragment()
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
    override val simpleName: String = "India Fragment"

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
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "IndianStocks")
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "IndianStockClicked")
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
        }


        binding.trendingRVIND.layoutManager = GridLayoutManager(activity, 1)
        binding.trendingRVIND.addItemDecoration(
            SpacingDecoration(requireContext().resources.getDimensionPixelSize(R.dimen.list_spacing_double))
        )
        binding.trendingRVIND.adapter = trendingAdapter


        viewModel.fetchIndianStocks().observe(viewLifecycleOwner) { quotes ->
            if (quotes.isNotEmpty()) {
                trendingAdapter.setData(quotes)

                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "IndianStock")
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "IndianStockClicked")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
            }
        }

    }

    override fun scrollToTop() {
        binding.trendingRVIND.smoothScrollToPosition(0)

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
            binding.trendingIND.isVisible = false
            if (requireActivity().isNetworkOnline()) {
                viewModel.fetchResults(query)
            } else {
                InAppMessage.showToast(requireActivity(), R.string.no_network_message)
            }
        } else {
            binding.trendingIND.isVisible = true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(ARG_WIDGET_ID, selectedWidgetId)
        super.onSaveInstanceState(outState)

    }

}