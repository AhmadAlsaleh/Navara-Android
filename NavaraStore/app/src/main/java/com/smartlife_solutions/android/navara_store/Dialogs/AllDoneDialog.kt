package com.smartlife_solutions.android.navara_store.Dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.smartlife_solutions.android.navara_store.ProfileCartOrders
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation
import kotlinx.android.synthetic.main.dialog_all_done.*
import org.json.JSONObject

class AllDoneDialog(context: Context, private var toCart: Boolean, private var isResetPassword: Boolean = false, var message: String = "", var lang: JSONObject): Dialog(context) {

    private lateinit var langDone: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_all_done)

        val myFont = StaticInformation().myFont(context)
        allDoneTitle.typeface = myFont
        okDoneBTN.typeface = myFont
        orderCodeTVDialog.typeface = myFont
        returnBTN.typeface = myFont

        langDone = lang.getJSONObject("dialogs").getJSONObject("allDone")

        returnBTN.text = langDone.getString("returnBTN")

        allDoneClose.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            dismiss()
        }

        if (!isResetPassword) {
            if (toCart) {
                setCart()
            } else {
                setDone()
                return
            }
        } else {
            setResetPassword()
        }

        if (!isResetPassword && !toCart) {
            setConfirm()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setCart() {
        returnBTN.visibility = View.VISIBLE
        allDoneTitle.text = langDone.getString("addToCart")
        okDoneBTN.text = langDone.getString("goToCart")
        allDoneMainIV.setImageResource(R.drawable.ic_cart_green)
        okDoneBTN.setOnClickListener {
            dismiss()
            context.startActivity(Intent(context, ProfileCartOrders::class.java)
                    .putExtra("currentPage", 1)
                    .putExtra("finishOnBack", true))
        }
        returnBTN.setOnClickListener {
            dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDone() {
        allDoneTitle.text = langDone.getString("orderSent")
        orderCodeTVDialog.text = "${langDone.getString("code")} $message"
        orderCodeTVDialog.visibility = View.VISIBLE
        okDoneBTN.text = langDone.getString("done")
        allDoneMainIV.setImageResource(R.drawable.ic_check_green)
        okDoneBTN.setOnClickListener {
            dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setResetPassword() {
        if (StaticInformation().isEmail(message)) {
            allDoneTitle.text = "${langDone.getString("email")}\n$message"
        } else {
            allDoneTitle.text = "${langDone.getString("sms")}\n$message"
        }
        okDoneBTN.text = langDone.getString("done")
        allDoneMainIV.setImageResource(R.drawable.ic_email)
        okDoneBTN.setOnClickListener {
            dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setConfirm() {
        if (StaticInformation().isEmail(message)) {
            allDoneTitle.text = "${langDone.getString("emailConf")}\n$message"
        } else {
            allDoneTitle.text = "${langDone.getString("smsConf")}\n$message"
        }
        okDoneBTN.text = langDone.getString("done")
        allDoneMainIV.setImageResource(R.drawable.ic_email)
        okDoneBTN.setOnClickListener {
            dismiss()
        }
    }

}