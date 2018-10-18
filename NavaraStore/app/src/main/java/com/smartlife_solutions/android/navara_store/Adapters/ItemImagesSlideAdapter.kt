package com.smartlife_solutions.android.navara_store.Adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class ItemImagesSlideAdapter(manager: FragmentManager): FragmentPagerAdapter(manager) {

    private val mFragmentsList: MutableList<Fragment> = mutableListOf()

    fun addFragment(fragment: Fragment) {
        mFragmentsList.add(fragment)
    }

    override fun getCount(): Int = mFragmentsList.size

    override fun getItem(p0: Int): Fragment = mFragmentsList[p0]

}