package com.starorigins.stockify.widgetapp.settings

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.starorigins.stockify.widgetapp.AppPreferences
import com.starorigins.stockify.widgetapp.R
import com.starorigins.stockify.widgetapp.components.InAppMessage
import com.starorigins.stockify.widgetapp.hasNotificationPermission
import com.starorigins.stockify.widgetapp.home.ChildFragment
import com.starorigins.stockify.widgetapp.home.MainViewModel
import com.starorigins.stockify.widgetapp.model.StocksProvider
import com.starorigins.stockify.widgetapp.notifications.NotificationsHandler
import com.starorigins.stockify.widgetapp.repo.QuotesDB
import com.starorigins.stockify.widgetapp.settings.PreferenceChangeListener
import com.starorigins.stockify.widgetapp.widget.WidgetDataProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.format.TextStyle
import timber.log.Timber
import java.io.File
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat(), ChildFragment,
    ActivityCompat.OnRequestPermissionsResultCallback {

    @Inject
    internal lateinit var stocksProvider: StocksProvider
    @Inject
    internal lateinit var widgetDataProvider: WidgetDataProvider
    @Inject
    internal lateinit var preferences: SharedPreferences
    @Inject
    internal lateinit var appPreferences: AppPreferences
    @Inject
    internal lateinit var db: QuotesDB
    @Inject
    internal lateinit var notificationsHandler: NotificationsHandler

    private val mainViewModel: MainViewModel by activityViewModels()

    // ChildFragment

    override fun scrollToTop() {
        listView.smoothScrollToPosition(0)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        if (resources.getBoolean(R.bool.isTablet)) {
            listView.layoutParams.width = resources.getDimensionPixelSize(R.dimen.tablet_width)
            (listView.layoutParams as FrameLayout.LayoutParams).gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
        }
        listView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        listView.isVerticalScrollBarEnabled = false
        setupSimplePreferencesScreen()
    }

    override fun onPause() {
        super.onPause()
        broadcastUpdateWidget()
    }

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        setPreferencesFromResource(R.xml.prefs, rootKey)
    }

    private fun setupSimplePreferencesScreen() {
        run {
            val themePref = findPreference<ListPreference>(AppPreferences.SETTING_APP_THEME)
            val selectedPref = appPreferences.themePref
            themePref.setValueIndex(selectedPref)
            themePref.summary = themePref.entries[selectedPref]
            themePref.onPreferenceChangeListener = object : PreferenceChangeListener() {
                override fun onPreferenceChange(
                    preference: Preference,
                    newValue: Any
                ): Boolean {
                    val stringValue = newValue.toString()
                    val listPreference = preference as ListPreference
                    val index = listPreference.findIndexOfValue(stringValue)
                    appPreferences.themePref = index
                    themePref.summary = listPreference.entries[index]
                    AppCompatDelegate.setDefaultNightMode(appPreferences.nightMode)
                    requireActivity().recreate()
                    return true
                }
            }
        }

        run {
            val refreshPreference = findPreference(AppPreferences.UPDATE_INTERVAL) as ListPreference
            val refreshIndex = preferences.getInt(AppPreferences.UPDATE_INTERVAL, 1)
            refreshPreference.setValueIndex(refreshIndex)
            refreshPreference.summary = refreshPreference.entries[refreshIndex]
            refreshPreference.onPreferenceChangeListener = object : PreferenceChangeListener() {
                override fun onPreferenceChange(
                    preference: Preference,
                    newValue: Any
                ): Boolean {
                    val stringValue = newValue.toString()
                    val listPreference = preference as ListPreference
                    val index = listPreference.findIndexOfValue(stringValue)
                    preferences.edit()
                        .putInt(AppPreferences.UPDATE_INTERVAL, index)
                        .apply()
                    stocksProvider.schedule()
                    refreshPreference.summary = refreshPreference.entries[index]
                    InAppMessage.showMessage(requireView(), R.string.refresh_updated_message)
                    broadcastUpdateWidget()
                    return true
                }
            }
        }

        run {
            val fontSizePreference = findPreference(AppPreferences.FONT_SIZE) as ListPreference
            val size = preferences.getInt(AppPreferences.FONT_SIZE, 1)
            fontSizePreference.setValueIndex(size)
            fontSizePreference.summary = fontSizePreference.entries[size]
            fontSizePreference.onPreferenceChangeListener = object : PreferenceChangeListener() {
                override fun onPreferenceChange(
                    preference: Preference,
                    newValue: Any
                ): Boolean {
                    val stringValue = newValue.toString()
                    val listPreference = preference as ListPreference
                    val index = listPreference.findIndexOfValue(stringValue)
                    preferences.edit()
                        .remove(AppPreferences.FONT_SIZE)
                        .putInt(AppPreferences.FONT_SIZE, index)
                        .apply()
                    broadcastUpdateWidget()
                    fontSizePreference.summary = fontSizePreference.entries[index]
                    InAppMessage.showMessage(requireView(), R.string.text_size_updated_message)
                    return true
                }
            }
        }

        run {
            val notifPref = findPreference<CheckBoxPreference>(AppPreferences.SETTING_NOTIFICATION_ALERTS)
            notifPref.isChecked = appPreferences.notificationAlerts()
            notifPref.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    appPreferences.setNotificationAlerts(newValue as Boolean)
                    if (newValue == true && Build.VERSION.SDK_INT >= 33 && !requireContext().hasNotificationPermission()) {
                        mainViewModel.requestNotificationPermission()
                    }
                    true
                }
        }
    }

    private fun <T : Preference> findPreference(key: String): T {
        return super.findPreference(key)!!
    }

    private fun needsPermissionGrant(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return false
        }
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    }

    private fun askForExternalStoragePermissions(reqCode: Int) {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ), reqCode
        )
    }

    private fun broadcastUpdateWidget() {
        widgetDataProvider.broadcastUpdateAllWidgets()
    }
}