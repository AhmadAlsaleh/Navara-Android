package com.smartlife_solutions.android.navara_store.MainBackFragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.smartlife_solutions.android.navara_store.R

@SuppressLint("ValidFragment")
class MainBack4(private val isRTL: Boolean = false) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_main_back4, container, false)
        if (isRTL) {
            view.rotationY = 180F
        }
        return view
    }


}
