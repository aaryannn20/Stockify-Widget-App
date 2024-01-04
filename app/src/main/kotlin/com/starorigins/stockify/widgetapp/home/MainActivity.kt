package com.starorigins.stockify.widgetapp.home

import android.Manifest
import android.os.Build.VERSION
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.starorigins.stockify.widgetapp.analytics.ClickEvent
import com.starorigins.stockify.widgetapp.base.BaseActivity
import com.starorigins.stockify.widgetapp.components.InAppMessage
import com.starorigins.stockify.widgetapp.hasNotificationPermission
import com.starorigins.stockify.widgetapp.network.NewsProvider
import com.starorigins.stockify.widgetapp.news.NewsFeedFragment
import com.starorigins.stockify.widgetapp.notifications.NotificationsHandler
import com.starorigins.stockify.widgetapp.portfolio.search.SearchFragment
import com.starorigins.stockify.widgetapp.settings.SettingsParentFragment
import com.starorigins.stockify.widgetapp.showDialog
import com.starorigins.stockify.widgetapp.viewBinding
import com.starorigins.stockify.widgetapp.widget.WidgetDataProvider
import com.starorigins.stockify.widgetapp.widget.WidgetsFragment
import com.starorigins.stockify.widgetapp.BuildConfig
import com.starorigins.stockify.widgetapp.R
import com.starorigins.stockify.widgetapp.databinding.ActivityMainBinding
import com.google.android.material.elevation.SurfaceColors
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

  companion object {
    private const val DIALOG_SHOWN: String = "DIALOG_SHOWN"
    private val FRAGMENT_MAP =
      mapOf<Int, String>(R.id.action_portfolio to HomeFragment::class.java.name,
          R.id.action_widgets to WidgetsFragment::class.java.name,
          R.id.action_search to SearchFragment::class.java.name,
          R.id.action_settings to SettingsParentFragment::class.java.name,
          R.id.action_feed to NewsFeedFragment::class.java.name)
  }

  @Inject internal lateinit var widgetDataProvider: WidgetDataProvider
  @Inject internal lateinit var newsProvider: NewsProvider
  @Inject internal lateinit var appReviewManager: IAppReviewManager
  @Inject internal lateinit var notificationsHandler: NotificationsHandler

  private var currentChild: ChildFragment? = null
  private var rateDialogShown = false
  override val simpleName: String = "MainActivity"
  override val binding: ActivityMainBinding by viewBinding(ActivityMainBinding::inflate)
  private val viewModel: MainViewModel by viewModels()
  private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()
    super.onCreate(savedInstanceState)
        initCaches()
    window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
        (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    window.navigationBarColor = SurfaceColors.SURFACE_2.getColor(this)
    savedInstanceState?.let { rateDialogShown = it.getBoolean(DIALOG_SHOWN, false) }

    binding.bottomNavigation.setOnItemSelectedListener { onNavigationItemSelected(it) }
    binding.bottomNavigation.setOnItemReselectedListener { onNavigationItemReselected(it) }

    currentChild = if (savedInstanceState == null) {
      val fragment = HomeFragment()
      supportFragmentManager.beginTransaction()
          .add(R.id.fragment_container, fragment, fragment.javaClass.name)
          .commit()
      fragment
    } else {
      supportFragmentManager.findFragmentById(R.id.fragment_container) as ChildFragment
    }

    val tutorialShown = appPreferences.tutorialShown()
    if (!tutorialShown) {
      showTutorial()
    }
    if (VERSION.SDK_INT >= 33) {
      requestPermissionLauncher = registerForActivityResult(RequestPermission()) { granted ->
        if (granted) {
          notificationsHandler.initialize()
        } else {
          InAppMessage.showMessage(this, R.string.notification_alerts_required_message)
        }
      }
      viewModel.requestNotificationPermission.observe(this) {
        if (it == true) {
          requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
          viewModel.resetRequestNotificationPermission()
        }
      }
    }
    if (VERSION.SDK_INT >= 33 && appPreferences.notificationAlerts() && !hasNotificationPermission()) {
      requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
    viewModel.showTutorial.observe(this) {
      if (it == true) {
        showTutorial()
        viewModel.resetShowTutorial()
      }
    }
    viewModel.showWhatsNew.observe(this) {
      if (it == true) {
        viewModel.resetShowWhatsNew()
      }
    }
    viewModel.openSearchWidgetId.observe(this) {
      it?.let {
        openSearch(it)
        viewModel.resetOpenSearch()
      }
    }
  }

  override fun onResume() {
    super.onResume()
    binding.bottomNavigation.menu.findItem(R.id.action_widgets).isEnabled = widgetDataProvider.hasWidget()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    outState.putBoolean(DIALOG_SHOWN, rateDialogShown)
    super.onSaveInstanceState(outState)
  }

  private fun initCaches() {
    // not sure why news provider is not getting initialised, so added this check
    if (::newsProvider.isInitialized) newsProvider.initCache()
    if (appPreferences.getLastSavedVersionCode() < BuildConfig.VERSION_CODE) {
    }
  }

  private fun onNavigationItemSelected(item: MenuItem): Boolean {
    val itemId = item.itemId
    var fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_MAP[itemId])
    if (fragment == null) {
      fragment = when (itemId) {
        R.id.action_portfolio -> HomeFragment()
        R.id.action_widgets -> WidgetsFragment()
        R.id.action_search -> SearchFragment()
        R.id.action_settings -> SettingsParentFragment()
        R.id.action_feed -> NewsFeedFragment()
        else -> {
          throw IllegalStateException("Unknown bottom nav itemId: $itemId - ${item.title}")
        }
      }
      if (fragment != currentChild) {
        supportFragmentManager.beginTransaction()
          .replace(R.id.fragment_container, fragment, fragment::class.java.name)
          .commit()
      } else return false
    }
    currentChild = fragment as ChildFragment
    analytics.trackClickEvent(
      ClickEvent("BottomNavClick")
        .addProperty("NavItem", item.title.toString())
    )

    if (!rateDialogShown && !appPreferences.shouldPromptRate()) {
      appReviewManager.launchReviewFlow(this)
      rateDialogShown = true
    }

    return true
  }

  private fun onNavigationItemReselected(item: MenuItem) {
    val itemId = item.itemId
    val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_MAP[itemId])
    (fragment as? ChildFragment)?.scrollToTop()
  }

  private fun showTutorial() {
    showDialog(getString(R.string.how_to_title), getString(R.string.how_to))
    appPreferences.setTutorialShown(true)
  }



  private fun openSearch(widgetId: Int) {
    binding.bottomNavigation.selectedItemId = R.id.action_search
  }
}