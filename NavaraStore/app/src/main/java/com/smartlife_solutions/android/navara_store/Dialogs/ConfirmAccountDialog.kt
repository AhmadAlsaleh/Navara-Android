package com.smartlife_solutions.android.navara_store.Dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
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
import com.smartlife_solutions.android.navara_store.Statics
import kotlinx.android.synthetic.main.dialog_confirm.*
import org.json.JSONObject

class ConfirmAccountDialog(context: Context, private var account: String,
                           private var isCount: Boolean = false, private var confTV: TextView? = null, var lang:JSONObject): Dialog(context) {

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

        confirmTitle.text = lang.getString("title")
        confirmSendBtn.text = lang.getString("confirmBTN")
        confirmCancelBtn.text = lang.getString("returnBTN")

        StaticInformation.clickResendTimer = if (isCount) { 59 } else { 0 }
        if (StaticInformation().isEmail(account)) {
            confirmTV.text = lang.getString("email")
            confirmSendBtn.visibility = View.GONE
            confirmET.visibility = View.GONE
            resendText = lang.getString("resendEmail")
        } else {
            resendText = lang.getString("resendCode")
            confirmTV.text = lang.getString("code")
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
            if (confirmET.text.toString().isNotEmpty()) {
                confirmSendBtn.visibility = View.GONE
                confirmPB.visibility = View.VISIBLE
                sendConfirm()
            } else {
                Toast.makeText(context, lang.getString("enterCode"), Toast.LENGTH_SHORT).show()
            }
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
                    confirmResendTV.text = "$resendText 00:${StaticInformation.clickResendTimer}"
                    countDown()
                } else {
                    confirmResendTV.text = resendText
                }
            } catch (err: Exception) {}
        }, 1000)
    }

    private fun resendCode() {

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
                params["Authorization"] = "Bearer ${Statics.myToken}"
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
        val request = object : StringRequest(Request.Method.GET, APIsURL().CONFIRM + "Code=${confirmET.text}",
                Response.Listener<String> {
                    dismiss()
                    Toast.makeText(context, lang.getString("successfully"), Toast.LENGTH_SHORT).show()
                    confTV?.visibility = View.GONE
                    queue.cancelAll("confirm")
                }, Response.ErrorListener {
            try {
                if (!StaticInformation().isConnected(context as Activity)) {
                    Toast.makeText(context, lang.getString("noInternet"), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, lang.getString("incorrectCode"), Toast.LENGTH_SHORT).show()
                }
            } catch (err: Exception) {
                Toast.makeText(context, lang.getString("incorrectCode"), Toast.LENGTH_SHORT).show()
            }
            confirmPB.visibility = View.GONE
            confirmSendBtn.visibility = View.VISIBLE
            queue.cancelAll("confirm")
        }) {
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=UTF-8"
                params["Authorization"] = "Bearer ${Statics.myToken}"
                return params
            }
        }
        request.tag = "confirm"
        queue.add(request)
    }

}