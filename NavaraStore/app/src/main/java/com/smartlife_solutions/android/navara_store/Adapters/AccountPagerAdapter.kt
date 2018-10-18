package com.smartlife_solutions.android.navara_store.Adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class AccountPagerAdapter(manager: FragmentManager): FragmentPagerAdapter(manager) {

    private val mFragmentsList: MutableList<Fragment> = mutableListOf()
    private val mFragmentTitleList: MutableList<String> = mutableListOf()

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentsList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getCount(): Int = mFragmentsList.size

    override fun getItem(p0: Int): Fragment = mFragmentsList[p0]

    override fun getPageTitle(position: Int): CharSequence? = mFragmentTitleList[position]

}