package com.smartlife_solutions.android.navara_store.MainBackFragments


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.smartlife_solutions.android.navara_store.OffersActivity

import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation

class MainBack2 : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main_back2, container, false)
        val offerBTN = view.findViewById<Button>(R.id.mainOffersBTN)
        offerBTN.typeface = StaticInformation().myFont(context)
        offerBTN.setOnClickListener {
            startActivity(Intent(context, OffersActivity::class.java))
        }

        return view
    }


}
