package com.smartlife_solutions.android.navara_store.Dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation
import kotlinx.android.synthetic.main.dialog_confirm.*

class ConfirmAccountDialog(context: Context, private var account: String, private var isCount: Boolean = false): Dialog(context) {

    private var resendText = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_confirm)

        setCanceledOnTouchOutside(false)

        val myFont = StaticInformation().myFont(context)
        confirmET.typeface = myFont
        confirmSendBtn.typeface = myFont
        confirmTV.typeface = myFont
        confirmTitle.typeface = myFont
        confirmResendTV.typeface = myFont
        confirmCancelBtn.typeface = myFont
        StaticInformation.clickResendTimer = if (isCount) { 59 } else { 0 }
        if (StaticInformation().isEmail(account)) {
            confirmTV.text = "Check your inbox please to confirm"
            confirmSendBtn.visibility = View.GONE
            confirmET.visibility = View.GONE
            resendText = "Resend Email"
        } else {
            resendText = "Resend Code"
            confirmTV.text = "Enter Confirmation Code below please"
        }
        confirmResendTV.text = resendText

        confirmCancelBtn.setOnClickListener {
            dismiss()
        }

        confirmClose.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            dismiss()
        }

        confirmSendBtn.setOnClickListener {
            confirmSendBtn.visibility = View.GONE
            confirmPB.visibility = View.VISIBLE
            sendConfirm()
        }

        confirmResendTV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            if (StaticInformation.clickResendTimer == 0) {
                resendCode()
            }
        }

        countDown()

    }

    @SuppressLint("SetTextI18n")
    private fun countDown() {
        Handler().postDelayed({
            try {
                if (StaticInformation.clickResendTimer > 0) {
                    StaticInformation.clickResendTimer--
                    confirmResendTV.text = "$resendText 00:$StaticInformation.clickResendTimer"
                    countDown()
                } else {
                    confirmResendTV.text = resendText
                }
            } catch (err: Exception) {}
        }, 1000)
    }

    private fun resendCode() {
        val myToken= try {
            DatabaseHelper(context).userModelIntegerRuntimeException.queryForAll()[0].token
        } catch (err: Exception) {
            ""
        }
        val queue = Volley.newRequestQueue(context)
        val request = object : StringRequest(Request.Method.GET, APIsURL().RESEND_CODE,
                Response.Listener<String> {
                    StaticInformation.clickResendTimer = 59
                    countDown()
                    queue.cancelAll("resend")
                }, Response.ErrorListener {
            queue.cancelAll("resend")
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=UTF-8"
                params["Authorization"] = "Bearer $myToken"
                return params
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
        }
        request.tag = "resend"
        queue.add(request)
    }

    private fun sendConfirm() {
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(Request.Method.GET,
                APIsURL().CONFIRM + "userid=$account&token=${confirmET.text}",
                Response.Listener<String> {
                    dismiss()
                    Toast.makeText(context, "Your Account has Verified Successfully", Toast.LENGTH_SHORT).show()
                    queue.cancelAll("confirm")
                }, Response.ErrorListener {
            try {
                if (!StaticInformation().isConnected(context as Activity)) {
                    Toast.makeText(context, "No Internet Connection, Please Try Again", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Incorrect Code try to Resend New One", Toast.LENGTH_SHORT).show()
                }
            } catch (err: Exception) {
                Toast.makeText(context, "Incorrect Code try to Resend New One", Toast.LENGTH_SHORT).show()
            }
            confirmPB.visibility = View.GONE
            confirmSendBtn.visibility = View.VISIBLE
            queue.cancelAll("confirm")
        })
        request.tag = "confirm"
        queue.add(request)
    }

}