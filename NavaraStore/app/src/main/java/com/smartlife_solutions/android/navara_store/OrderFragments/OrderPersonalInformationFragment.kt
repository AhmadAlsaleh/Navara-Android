package com.smartlife_solutions.android.navara_store.OrderFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.smartlife_solutions.android.navara_store.OrdersActivity
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation

@SuppressLint("ValidFragment")
class OrderPersonalInformationFragment(var activity: OrdersActivity) : Fragment() {

    lateinit var name: EditText
    lateinit var phone: EditText
    lateinit var remark: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_personal_information, container, false)

        // region font
        val myFont = StaticInformation().myFont(context)
        view.findViewById<TextView>(R.id.contactTitle).typeface = myFont
        view.findViewById<TextView>(R.id.nameTitle).typeface = myFont
        view.findViewById<TextView>(R.id.phoneTitle).typeface = myFont
        view.findViewById<TextView>(R.id.remarkTitle).typeface = myFont
        name = view.findViewById(R.id.addNameET)
        name.typeface = myFont
        phone = view.findViewById(R.id.addPhoneET)
        phone.typeface = myFont
        remark = view.findViewById(R.id.remarkET)
        remark.typeface = myFont
        name.setText(activity.personName)
        phone.setText(activity.personPhone)
        // endregion

        return view
    }
}
