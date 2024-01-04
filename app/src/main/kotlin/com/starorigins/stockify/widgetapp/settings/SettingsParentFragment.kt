package com.starorigins.stockify.widgetapp.settings

import com.starorigins.stockify.widgetapp.base.BaseFragment
import com.starorigins.stockify.widgetapp.home.ChildFragment
import com.starorigins.stockify.widgetapp.viewBinding
import com.starorigins.stockify.widgetapp.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsParentFragment : BaseFragment<FragmentSettingsBinding>(), ChildFragment {
  override val simpleName: String = "SettingsParentFragment"
  override val binding: (FragmentSettingsBinding) by viewBinding(FragmentSettingsBinding::inflate)

}