package com.smartlife_solutions.android.navara_store.MainBackFragments


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.smartlife_solutions.android.navara_store.LocationActivity

import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation

class MainBack4 : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main_back4, container, false)
        val locationBTN = view.findViewById<Button>(R.id.mainLocationBTN)
        locationBTN.typeface = StaticInformation().myFont(context)
        locationBTN.setOnClickListener {
            startActivity(Intent(context, LocationActivity::class.java))
        }

        return view
    }


}
