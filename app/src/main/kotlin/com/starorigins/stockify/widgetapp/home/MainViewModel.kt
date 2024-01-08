package com.starorigins.stockify.widgetapp.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

  val requestNotificationPermission: LiveData<Boolean?>
    get() = _requestNotificationPermission
  private val _requestNotificationPermission = MutableLiveData<Boolean?>()

  val openSearchWidgetId: LiveData<Int?>
    get() = _openSearchWidgetId
  private val _openSearchWidgetId = MutableLiveData<Int?>()

  fun requestNotificationPermission() {
    _requestNotificationPermission.value = true
  }

  fun resetRequestNotificationPermission() {
    _requestNotificationPermission.value = null
  }

  fun openSearch(widgetId: Int) {
    _openSearchWidgetId.value = widgetId
  }

  fun resetOpenSearch() {
    _openSearchWidgetId.value = null
  }


}