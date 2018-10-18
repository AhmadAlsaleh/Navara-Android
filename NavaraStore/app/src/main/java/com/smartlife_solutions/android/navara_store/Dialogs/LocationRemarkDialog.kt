package com.smartlife_solutions.android.navara_store.Dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.smartlife_solutions.android.navara_store.OrderFragments.OrderChooseLocationFragment
import com.smartlife_solutions.android.navara_store.OrderFragments.OrderSummaryFragment
import com.smartlife_solutions.android.navara_store.OrdersActivity
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation
import kotlinx.android.synthetic.main.dialog_location_remark.*

class LocationRemarkDialog(context: Context, var title: String, var fragment: OrderChooseLocationFragment,
                           var orderSummaryFragment: OrderSummaryFragment, var activity: OrdersActivity)
    : Dialog(context), View.OnClickListener {

    override fun onClick(v: View?) {
        v?.startAnimation(StaticInformation().clickAnim(context))
        when (v?.id) {
            R.id.locationRemarkClose -> dismiss()
            R.id.locationRemarkBTN -> saveRemark()
        }
    }

    private fun saveRemark() {
        activity.locationRemarkText = locationRemarkET.text.toString()
        fragment.setupMap()
        locationRemarkPB.visibility = View.VISIBLE
        locationRemarkBTN.visibility = View.GONE
        Handler().postDelayed({
            locationRemarkPB.visibility = View.GONE
            locationRemarkBTN.visibility = View.VISIBLE
            dismiss()
        }, 1000)
        Log.e("location remark dialog", activity.locationRemarkText)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_location_remark)

        val myFont = StaticInformation().myFont(context)
        locationRemarkTitle.typeface = myFont
        locationRemarkTV.typeface = myFont
        locationRemarkET.typeface = myFont
        locationRemarkBTN.typeface = myFont

        locationRemarkET.setText(fragment.activity.locationRemarkText)
        locationRemarkTV.text = title

        locationRemarkBTN.setOnClickListener(this)
        locationRemarkClose.setOnClickListener(this)

    }
}