package com.smartlife_solutions.android.navara_store.Dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.LoginRegisterActivity
import com.smartlife_solutions.android.navara_store.ProfileCartOrders
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation
import kotlinx.android.synthetic.main.dialog_change_password.*
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

class ChangePasswordDialog(context: Context, var activity: ProfileCartOrders): Dialog(context), View.OnClickListener {

    lateinit var myFont: Typeface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_change_password)
        myFont = StaticInformation().myFont(context)!!

        // region font
        changePasswordTitle.typeface = myFont
        changePasswordOldText.typeface = myFont
        oldPasswordET.typeface = myFont
        changePasswordNewText.typeface = myFont
        newPasswordET.typeface = myFont
        changePasswordReNewText.typeface = myFont
        renewPasswordET.typeface = myFont
        changePasswordBTN.typeface = myFont
        // endregion

        changePasswordClose.setOnClickListener(this)
        changePasswordBTN.setOnClickListener(this)
        oldPasswordEyeIV.setOnClickListener(this)
        newPasswordEyeIV.setOnClickListener(this)
        renewPasswordEyeIV.setOnClickListener(this)

    }

    var showPasswords = booleanArrayOf(false, false, false)
    override fun onClick(v: View?) {
        v?.startAnimation(StaticInformation().clickAnim(context))
        when (v?.id) {
            R.id.changePasswordClose -> {
                if (changePasswordPB.visibility == View.GONE) {
                    dismiss()
                }
            }
            R.id.changePasswordBTN -> changePassword()
            R.id.oldPasswordEyeIV -> showPasswords[0] = checkPasswordShow(v as ImageView, oldPasswordET, showPasswords[0])
            R.id.newPasswordEyeIV -> showPasswords[1] = checkPasswordShow(v as ImageView, newPasswordET, showPasswords[1])
            R.id.renewPasswordEyeIV -> showPasswords[2] = checkPasswordShow(v as ImageView, renewPasswordET, showPasswords[2])
        }
    }

    private fun checkPasswordShow(v: ImageView, edit: EditText, isShow: Boolean): Boolean {
        return if (isShow) {
            v.setImageResource(R.drawable.ic_eye_black)
            edit.transformationMethod = PasswordTransformationMethod()
            false
        } else {
            v.setImageResource(R.drawable.ic_eye_primary)
            edit.transformationMethod = null
            true
        }
    }

    private fun changePassword() {
        if (checkPasswordsLength()) {
            if (newPasswordET.text.toString() != renewPasswordET.text.toString()) {
                Toast.makeText(context, "Passwords are not matched", Toast.LENGTH_LONG).show()
            } else {
                sendRequest()
            }
        } else {
            Toast.makeText(context, "password have to be 6 characters at least", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPasswordsLength(): Boolean {
        return oldPasswordET.text.length >= 6
                && newPasswordET.text.length >= 6
                && renewPasswordET.text.length >= 6
    }

    private fun sendRequest() {

        toggleLoader(true)

        val myToken= try {
            DatabaseHelper(context).userModelIntegerRuntimeException.queryForAll()[0].token
        } catch (err: Exception) {
            ""
        }
        val jsonBody = JSONObject()
        jsonBody.put("OldPassword", oldPasswordET.text.toString())
        jsonBody.put("NewPassword", newPasswordET.text.toString())
        val requestBody: String = jsonBody.toString()
        val queue = Volley.newRequestQueue(context)
        val request = object : StringRequest(Request.Method.POST, APIsURL().CHANGE_PASSWORD_URL,
                Response.Listener<String> {
                    Toast.makeText(context, "Your Password has Changed Successfully", Toast.LENGTH_SHORT).show()
                    dismiss()
                    queue.cancelAll("change")
                }, Response.ErrorListener {
            try {
                it.networkResponse.statusCode
                Toast.makeText(context, "Incorrect Old Password, Please Try Again", Toast.LENGTH_SHORT).show()
            } catch (err: Exception) {
                Toast.makeText(context, "No Internet Connection, Please Try Again", Toast.LENGTH_SHORT).show()
            }
            toggleLoader(false)
            queue.cancelAll("change")
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

            override fun getBody(): ByteArray? {
                return try {
                    requestBody.toByteArray(Charset.forName("utf-8"))
                } catch (err: UnsupportedEncodingException) {
                    null
                }
            }

            override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
                var responseString = ""
                if (response != null) {
                    responseString = response.statusCode.toString()
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response))
            }
        }
        request.tag = "change"
        queue.add(request)
    }

    private fun toggleLoader(showLoader: Boolean) {
        if (showLoader) {
            changePasswordPB.visibility = View.VISIBLE
            changePasswordBTN.visibility = View.GONE
        } else {
            changePasswordPB.visibility = View.GONE
            changePasswordBTN.visibility = View.VISIBLE
        }
    }

}