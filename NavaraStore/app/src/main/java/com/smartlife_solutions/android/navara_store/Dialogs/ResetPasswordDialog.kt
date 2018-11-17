package com.smartlife_solutions.android.navara_store.Dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.AppCompatEditText
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.rilixtech.CountryCodePicker
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.LoginRegisterActivity
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation
import com.smartlife_solutions.android.navara_store.Statics
import kotlinx.android.synthetic.main.dialog_reset_password.*
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

class ResetPasswordDialog(context: Context, var activity: LoginRegisterActivity): Dialog(context) {

    private lateinit var ccp: CountryCodePicker
    private lateinit var edtPhoneNumber: AppCompatEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_reset_password)

        val myFont = StaticInformation().myFont(context)
        resetPasswordTitle.typeface = myFont
        resetEmailMobile.typeface = myFont
        resetEmailMobileET.typeface = myFont
        resetPasswordBTN.typeface = myFont

        val lang = Statics.getLanguageJSONObject(activity).getJSONObject("dialogs").getJSONObject("resetPassword")
        resetPasswordTitle.text = lang.getString("title")
        resetEmailMobile.text = lang.getString("emailOrMobile")
        resetPasswordBTN.text = lang.getString("button")
        resetEmailMobileET.hint = lang.getString("emailHint")

        ccp = findViewById(R.id.ccpReset)
        edtPhoneNumber = findViewById(R.id.phone_number_edt_reset)
        edtPhoneNumber.typeface = myFont
        edtPhoneNumber.hint = lang.getString("mobileHint")
        val emailRB = findViewById<RadioButton>(R.id.resetEmailRB)
        emailRB.typeface = myFont
        emailRB.text = lang.getString("email")
        val phoneRB = findViewById<RadioButton>(R.id.resetPhoneRB)
        phoneRB.typeface = myFont
        phoneRB.text = lang.getString("mobile")

        emailRB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                resetEmailMobileET.visibility = View.VISIBLE
                resetPhoneLL.visibility = View.GONE
            }
        }
        phoneRB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                resetEmailMobileET.visibility = View.GONE
                resetPhoneLL.visibility = View.VISIBLE
            }
        }


        resetPasswordBTN.setOnClickListener {

            if (emailRB.isChecked) {
                if (StaticInformation().isEmail(resetEmailMobileET.text.toString())) {
                    val jsonObject = JSONObject()
                    jsonObject.put("UserID", resetEmailMobileET.text.toString())
                    resetPasswordRequest(jsonObject)
                } else {
                    Toast.makeText(context, lang.getString("incorrectEmail"), Toast.LENGTH_LONG).show()
                }
            }

            if (phoneRB.isChecked) {
                val s = edtPhoneNumber.text.toString().trimStart('0')
                edtPhoneNumber.setText(s)
                if (edtPhoneNumber.text.toString()[0] != '0') {
                    if (StaticInformation().isPhone(edtPhoneNumber.text.toString())) {
                        val jsonObject = JSONObject()
                        jsonObject.put("UserID", "+" + ccp.selectedCountryCode + edtPhoneNumber.text.toString())
                        resetPasswordRequest(jsonObject)
                    } else {
                        Toast.makeText(context, lang.getString("incorrectPhone"), Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "Remove first 0 from your Number please", Toast.LENGTH_SHORT).show()
                }
            }
        }

        resetPasswordClose.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            dismiss()
        }
    }

    private fun resetPasswordRequest(jsonObject: JSONObject) {
        resetPasswordBTN.visibility = View.GONE
        resetPB.visibility = View.VISIBLE
        Log.e("reset object", jsonObject.toString())
        val queue = Volley.newRequestQueue(context)
        val resetRequest = object : StringRequest(Request.Method.POST, APIsURL().RESET_PASSWORD,
                Response.Listener {
                    dismiss()
                    if (StaticInformation().isEmail(jsonObject.getString("UserID"))) {
                        activity.doneReset = AllDoneDialog(context, false, true, message = jsonObject.getString("UserID"), lang = Statics.getLanguageJSONObject(activity))
                        activity.doneReset.show()
                    } else {
                        activity.resetCode = ResetPasswordNewDialog(context, jsonObject.getString("UserID"), Statics.getLanguageJSONObject(activity).getJSONObject("dialogs").getJSONObject("resetPassword"))
                        activity.resetCode.show()
                    }
                    queue.cancelAll("reset")
                }, Response.ErrorListener {
            Toast.makeText(context, Statics.getLanguageJSONObject(activity).getString("noInternet"), Toast.LENGTH_SHORT).show()
            resetPasswordBTN.visibility = View.VISIBLE
            resetPB.visibility = View.GONE
            Log.e("reset error", it.toString())
            queue.cancelAll("reset")
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray? {
                return try {
                    jsonObject.toString().toByteArray(Charset.forName("utf-8"))
                } catch (err: UnsupportedEncodingException) {
                    null
                }
            }
        }
        resetRequest.tag = "reset"
        queue.add(resetRequest)
    }

}