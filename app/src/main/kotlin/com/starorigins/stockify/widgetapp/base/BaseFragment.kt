package com.starorigins.stockify.widgetapp.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.starorigins.stockify.widgetapp.analytics.Analytics
import javax.inject.Inject


abstract class BaseFragment<T: ViewBinding> : Fragment() {

  @Inject internal lateinit var analytics: Analytics

  abstract val simpleName: String
  abstract val binding: T

  final override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return binding.root
  }
}
