package com.starorigins.stockify.widgetapp.portfolio

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.starorigins.stockify.widgetapp.AppPreferences
import com.starorigins.stockify.widgetapp.base.BaseActivity
import com.starorigins.stockify.widgetapp.components.InAppMessage
import com.starorigins.stockify.widgetapp.dismissKeyboard
import com.starorigins.stockify.widgetapp.network.data.Holding
import com.starorigins.stockify.widgetapp.network.data.Quote
import com.starorigins.stockify.widgetapp.viewBinding
import com.starorigins.stockify.widgetapp.R
import com.starorigins.stockify.widgetapp.databinding.ActivityPositionsBinding
import com.starorigins.stockify.widgetapp.databinding.LayoutPositionHoldingBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat

@AndroidEntryPoint
class AddPositionActivity : BaseActivity<ActivityPositionsBinding>() {
    override val binding: (ActivityPositionsBinding) by viewBinding(ActivityPositionsBinding::inflate)

    companion object {
        const val TICKER = "TICKER"
    }

    internal lateinit var ticker: String
    override val simpleName = "AddPositionActivity"
    private val viewModel: AddPositionViewModel by viewModels()
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        if (intent.hasExtra(TICKER) && intent.getStringExtra(TICKER) != null) {
            ticker = intent.getStringExtra(TICKER)!!
        } else {
            ticker = ""
            InAppMessage.showToast(this, R.string.error_symbol)
            finish()
            return
        }
        viewModel.loadQuote(ticker)
        viewModel.quote.observe(this) {
            updateTotal(it)
        }
        binding.tickerName.text = ticker

        binding.addButton.setOnClickListener { onAddClicked() }

        binding.positionsHolder.removeAllViews()
        val position = viewModel.getPosition(ticker)
        position?.let {
            for (holding in position.holdings) {
                addPositionView(holding)
            }
        }
    firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    private fun onAddClicked() {
        val sharesView = binding.shares
        val priceView = binding.price
        val priceText = priceView.text.toString()
        val sharesText = sharesView.text.toString()
        if (priceText.isNotEmpty() && sharesText.isNotEmpty()) {
            var price = 0f
            var shares = 0f
            var success = true
            try {
                val numberFormat: NumberFormat = NumberFormat.getNumberInstance()
                price = numberFormat.parse(priceText)!!
                    .toFloat()
            } catch (e: NumberFormatException) {
                binding.priceInputLayout.error = getString(R.string.invalid_number)
                success = false
            }
            try {
                val numberFormat: NumberFormat = NumberFormat.getNumberInstance()
                shares = numberFormat.parse(sharesText)!!
                    .toFloat()
            } catch (e: NumberFormatException) {
                binding.sharesInputLayout.error = getString(R.string.invalid_number)
                success = false
            }
            // Check for zero shares.
            if (success) {
                if (shares == 0.0f) {
                    binding.sharesInputLayout.error = getString(R.string.invalid_number)
                    success = false
                }
            }
            if (success) {
                binding.priceInputLayout.error = null
                binding.sharesInputLayout.error = null
                viewModel.addHolding(ticker, shares, price).observe(this) { holding ->
                    priceView.setText("")
                    sharesView.setText("")
                    addPositionView(holding)
                    updateActivityResult()
                }
            }
        }
        dismissKeyboard()

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "AddPortfolio")
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "portfolio")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    private fun updateActivityResult() {
        val data = Intent()
        setResult(Activity.RESULT_OK, data)
    }

    private fun addPositionView(holding: Holding) {
        val positionBinding = LayoutPositionHoldingBinding.inflate(layoutInflater)
        val positionNumShares = positionBinding.positionShares
        val positionPrice = positionBinding.positionPrice
        val positionTotalValue = positionBinding.positionTotalValue
        positionNumShares.text = AppPreferences.DECIMAL_FORMAT.format(holding.shares)
        positionPrice.text = AppPreferences.DECIMAL_FORMAT.format(holding.price)
        positionTotalValue.text = AppPreferences.DECIMAL_FORMAT.format(holding.totalValue())
        binding.positionsHolder.addView(positionBinding.root)
        positionBinding.root.tag = holding
        // Remove entry when right side 'x' icon is clicked.
        positionBinding.removePosition.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.remove)
                .setMessage(getString(R.string.remove_holding, "${holding.shares}@${holding.price}"))
                .setPositiveButton(R.string.remove) { dialog, _ ->
                    viewModel.removePosition(ticker, holding)
                    binding.positionsHolder.removeView(positionBinding.root)
                    updateActivityResult()
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    private fun updateTotal(quote: Quote) {
        binding.totalShares.text = quote.numSharesString()
        binding.averagePrice.text = quote.averagePositionPrice()
        binding.totalValue.text = quote.totalSpentString()
    }
}