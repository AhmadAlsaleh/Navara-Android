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

class AllDoneDialog(context: Context, private var toCart: Boolean, private var isResetPassword: Boolean = false, var message: String = ""): Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_all_done)

        val myFont = StaticInformation().myFont(context)
        allDoneTitle.typeface = myFont
        okDoneBTN.typeface = myFont
        orderCodeTVDialog.typeface = myFont
        returnBTN.typeface = myFont

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
        allDoneTitle.text = "Item has been added to your Cart"
        okDoneBTN.text = "Go to Cart"
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
        allDoneTitle.text = "Your Order has\nSent Successfully"
        orderCodeTVDialog.text = "Order Code: $message"
        orderCodeTVDialog.visibility = View.VISIBLE
        okDoneBTN.text = "Done"
        allDoneMainIV.setImageResource(R.drawable.ic_check_green)
        okDoneBTN.setOnClickListener {
            dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setResetPassword() {
        if (StaticInformation().isEmail(message)) {
            allDoneTitle.text = "We Sent a reset password email to\n$message"
        } else {
            allDoneTitle.text = "We Sent a reset password SMS to\n$message"
        }
        okDoneBTN.text = "Done"
        allDoneMainIV.setImageResource(R.drawable.ic_email)
        okDoneBTN.setOnClickListener {
            dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setConfirm() {
        if (StaticInformation().isEmail(message)) {
            allDoneTitle.text = "We Sent a confirm email to\n$message"
        } else {
            allDoneTitle.text = "We Sent a confirm SMS to\n$message"
        }
        okDoneBTN.text = "Done"
        allDoneMainIV.setImageResource(R.drawable.ic_email)
        okDoneBTN.setOnClickListener {
            dismiss()
        }
    }

}