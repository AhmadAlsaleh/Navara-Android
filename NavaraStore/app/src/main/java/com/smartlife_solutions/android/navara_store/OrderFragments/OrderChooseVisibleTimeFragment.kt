package com.smartlife_solutions.android.navara_store.OrderFragments

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.smartlife_solutions.android.navara_store.OrdersActivity

import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation
import com.smartlife_solutions.android.navara_store.Statics
import java.util.*

@SuppressLint("ValidFragment")
class OrderChooseVisibleTimeFragment(var activity: OrdersActivity) : Fragment() {

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_choose_visible_time, container, false)

        val lang = Statics.getLanguageJSONObject(activity).getJSONObject("makeOrderActivity").getJSONObject("timeFragment")

        val myFont = StaticInformation().myFont(context)
        val titleTime = view.findViewById<TextView>(R.id.chooseTimeTitle)
        titleTime.typeface = myFont
        titleTime.text = lang.getString("title")
        val fromTV = view.findViewById<TextView>(R.id.fromTV)
        fromTV.typeface = myFont
        fromTV.text = lang.getString("from")
        val toTV = view.findViewById<TextView>(R.id.toTV)
        toTV.typeface = myFont
        toTV.text = lang.getString("to")
        val toBTN = view.findViewById<Button>(R.id.setToTimeBTN)
        toBTN.typeface = myFont
        toBTN.text = lang.getString("setToTime")
        val fromBTN = view.findViewById<Button>(R.id.setFromTimeBTN)
        fromBTN.typeface = myFont
        fromBTN.text = lang.getString("setFromTime")
        val timeFromTP = view.findViewById<TextView>(R.id.clockFromTC)
        timeFromTP.typeface = myFont
        val timeToTP = view.findViewById<TextView>(R.id.clockToTC)
        timeToTP.typeface = myFont

        var hourForm = activity.fromTime.split(':')[0].toInt()
        var minutesFrom = activity.fromTime.split(':')[1].split(' ')[0].toInt()

        var hourTo = activity.toTime.split(':')[0].toInt()
        var minutesTo = activity.toTime.split(':')[1].split(' ')[0].toInt()

        timeFromTP.text = "${checkHourDigits(hourForm.toString())}:${checkHourDigits(minutesFrom.toString())} AM"
        var hourT = checkHourDigits(hourTo.toString()).toInt()
        if (hourT > 12) {
            hourT -= 12
        }
        timeToTP.text = "${checkHourDigits(hourT.toString())}:${checkHourDigits(minutesTo.toString())} PM"

        fromBTN.setOnClickListener {
            val fromTPD = TimePickerDialog(context,
                    TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                        hourForm = hourOfDay
                        minutesFrom = minute
                        if (hourOfDay > 12) {
                            timeFromTP.text = "${checkHourDigits((hourOfDay - 12).toString())}:${checkHourDigits(minute.toString())} PM"
                        } else {
                            if (hourOfDay == 0) {
                                timeFromTP.text = "12:${checkHourDigits(minute.toString())} AM"
                            } else {
                                timeFromTP.text = "${checkHourDigits(hourOfDay.toString())}:${checkHourDigits(minute.toString())} AM"
                            }
                        }
                        activity.fromTime = timeFromTP.text.toString()
            }, hourForm, minutesFrom, false)
            fromTPD.setTitle("Set from time")
            fromTPD.show()
        }

        toBTN.setOnClickListener {
            val fromTPD = TimePickerDialog(context,
                    TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                        hourTo = hourOfDay
                        minutesTo = minute
                        if (hourOfDay > 12) {
                            timeToTP.text = "${checkHourDigits((hourOfDay - 12).toString())}:${checkHourDigits(minute.toString())} PM"
                        } else {
                            if (hourOfDay == 0) {
                                timeToTP.text = "12:${checkHourDigits(minute.toString())} AM"
                            } else {
                                timeToTP.text = "${checkHourDigits(hourOfDay.toString())}:${checkHourDigits(minute.toString())} AM"
                            }
                        }
                        activity.toTime = timeToTP.text.toString()
                    }, hourTo, minutesTo, false)
            fromTPD.setTitle("Set to time")
            fromTPD.show()
        }

        return view
    }

    private fun checkHourDigits(s: String): String {
        return if (s.length == 1) {
            "0$s"
        } else {
            s
        }
    }

}
