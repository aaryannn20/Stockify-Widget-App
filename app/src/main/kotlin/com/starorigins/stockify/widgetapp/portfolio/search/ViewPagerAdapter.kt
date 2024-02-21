package com.starorigins.stockify.widgetapp.portfolio.search

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ViewPagerAdapter(fm : FragmentManager, val fragmentCount : Int): FragmentStatePagerAdapter(fm) {

    private val fragmentTitleList = mutableListOf("Trending", "Indices","BSE/NSE", "Crypto")

    override fun getCount(): Int = fragmentCount

    override fun getItem(position: Int): Fragment {
        when(position){
            0-> return PopularFragment()
            1-> return IndicesFragment()
            2-> return IndFragment()
            3-> return CryptoFragment()
            else -> return PopularFragment()
        }
    }

    override fun getPageTitle(position: Int):CharSequence?{
        return fragmentTitleList[position]
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
