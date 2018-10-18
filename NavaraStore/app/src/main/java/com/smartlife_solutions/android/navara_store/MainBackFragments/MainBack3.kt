package com.smartlife_solutions.android.navara_store.MainBackFragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.LoginRegisterActivity
import com.smartlife_solutions.android.navara_store.OrdersActivity

import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation

class MainBack3 : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main_back3, container, false)
        val orderBTN = view.findViewById<Button>(R.id.mainOrderBTN)
        orderBTN.typeface = StaticInformation().myFont(context)
        orderBTN.setOnClickListener {
            activity?.finish()
            try {
                if (DatabaseHelper(context).userModelIntegerRuntimeException.queryForAll()[0].token.isNotEmpty()) {
                    startActivity(Intent(context, OrdersActivity::class.java).putExtra("done", true))
                } else {
                    startActivity(Intent(context, LoginRegisterActivity::class.java))
                }
            } catch (err: Exception) {
                startActivity(Intent(context, LoginRegisterActivity::class.java))
            }
        }

        return view
    }


}
