package com.starorigins.stockify.widgetapp.settings


internal open class PreferenceChangeListener :
    android.preference.Preference.OnPreferenceChangeListener,
    androidx.preference.Preference.OnPreferenceChangeListener {

  override fun onPreferenceChange(
    preference: android.preference.Preference,
    newValue: Any
  ): Boolean = false

  override fun onPreferenceChange(
    preference: androidx.preference.Preference,
    newValue: Any
  ): Boolean = false
}