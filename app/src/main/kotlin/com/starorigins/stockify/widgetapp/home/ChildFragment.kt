package com.starorigins.stockify.widgetapp.home

import android.os.Bundle

interface ChildFragment {
  fun setData(bundle: Bundle) {}
  fun scrollToTop() {}
}