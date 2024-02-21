package com.starorigins.stockify.widgetapp.analytics

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext

class AnalyticsImpl(
  @ApplicationContext context: Context,
  generalProperties: dagger.Lazy<GeneralProperties>
) : Analytics