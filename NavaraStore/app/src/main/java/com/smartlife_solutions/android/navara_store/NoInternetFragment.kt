package com.smartlife_solutions.android.navara_store

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class NoInternetFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_no_internet, container, false)

        val myFont = StaticInformation().myFont(context)!!
        view.findViewById<TextView>(R.id.loadingNoNetTV).typeface = myFont

        return view
    }


}
