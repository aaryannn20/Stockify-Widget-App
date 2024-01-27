package com.starorigins.stockify.widgetapp.portfolio.search

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter (fm:FragmentManager):FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return when(position){
            0->{
                IndicesFragment()
            }
            1->{
                IndFragment()
            }

            else -> {
                PopularFragment()

            }
        }
    }


}

//class ViewPagerAdapter (fm:FragmentManager):FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
//
//    val fragmentList = mutableListOf<Fragment>()
//
//    val titleList = mutableListOf<String>()
//
//    override fun getCount(): Int {
//        return fragmentList.size
//    }
//
//    override fun getItem(position: Int): Fragment {
//        return fragmentList[position]
//    }
//
//    override fun getPageTitle(position: Int): CharSequence? {
//        return titleList[position]
//    }
//
//    fun addFragments(fragment: Fragment,title: String){
//        fragmentList.add(fragment)
//        titleList.add(title)
//    }
//}
