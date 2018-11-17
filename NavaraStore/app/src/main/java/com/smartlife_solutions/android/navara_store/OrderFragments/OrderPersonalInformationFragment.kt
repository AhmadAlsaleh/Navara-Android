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
import com.smartlife_solutions.android.navara_store.Statics

@SuppressLint("ValidFragment")
class OrderPersonalInformationFragment(var activity: OrdersActivity) : Fragment() {

    lateinit var name: EditText
    lateinit var phone: EditText
    lateinit var remark: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_personal_information, container, false)

        val lang = Statics.getLanguageJSONObject(activity).getJSONObject("makeOrderActivity").getJSONObject("informationFragment")
        // region font
        val myFont = StaticInformation().myFont(context)
        val titleContact = view.findViewById<TextView>(R.id.contactTitle)
        titleContact.typeface = myFont
        titleContact.text = lang.getString("title")
        val nameTV = view.findViewById<TextView>(R.id.nameTitle)
        nameTV.typeface = myFont
        nameTV.text = lang.getString("name")
        val phoneTV = view.findViewById<TextView>(R.id.phoneTitle)
        phoneTV.typeface = myFont
        phoneTV.text = lang.getString("phone")
        val remarkTV = view.findViewById<TextView>(R.id.remarkTitle)
        remarkTV.typeface = myFont
        remarkTV.text = lang.getString("remark")
        name = view.findViewById(R.id.addNameET)
        name.typeface = myFont
        phone = view.findViewById(R.id.addPhoneET)
        phone.typeface = myFont
        remark = view.findViewById(R.id.remarkET)
        remark.typeface = myFont
        name.setText(activity.personName)
        if (activity.personPhone.length < 3) {
            phone.setText("")
        } else {
            phone.setText(activity.personPhone)
        }
        // endregion

        return view
    }
}
