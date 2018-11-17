package com.smartlife_solutions.android.navara_store.Dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation
import kotlinx.android.synthetic.main.dialog_reset_password_new.*
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

class ResetPasswordNewDialog(context: Context, private var userID: String, var lang: JSONObject): Dialog(context), View.OnClickListener {

    private var isShow = false

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.resetCodeClose, R.id.resetCodeCancelBtn -> dismiss()
            R.id.resetCodeResetBTN -> if (isInputCorrect()) {
                resetRequest()
            }
            R.id.resetCodeEyeIV -> toggleShowPassword()
        }
    }

    private fun isInputCorrect(): Boolean {
        if (resetCodeCodeET.text.toString().isEmpty()) {
            Toast.makeText(context, lang.getString("enterCode"), Toast.LENGTH_SHORT).show()
            return false
        }

        if (resetCodeNewPasswordET.text.toString().isEmpty()) {
            Toast.makeText(context, lang.getString("enterPassword"), Toast.LENGTH_SHORT).show()
            return false
        } else if (resetCodeNewPasswordET.text.toString().length < 6) {
            Toast.makeText(context, lang.getString("passwordValidation"), Toast.LENGTH_SHORT).show()
            return false
        }

        return true

    }

    private fun toggleShowPassword() {
        if (isShow) {
            isShow = false
            resetCodeEyeIV.setImageResource(R.drawable.ic_eye_black)
            resetCodeNewPasswordET.transformationMethod = PasswordTransformationMethod()
        } else {
            isShow = true
            resetCodeEyeIV.setImageResource(R.drawable.ic_eye_primary)
            resetCodeNewPasswordET.transformationMethod = null
        }
    }

    private fun resetRequest() {
        val jsonObject = JSONObject()
        jsonObject.put("UserID", userID)
        jsonObject.put("NewPassword", resetCodeNewPasswordET.text.toString())
        jsonObject.put("Code", resetCodeCodeET.text.toString())
        val queue = Volley.newRequestQueue(context)
        val reset = object : StringRequest(Request.Method.POST, APIsURL().CHANGE_FORGETTED_PASSWORD, {
            Toast.makeText(context, lang.getString("changed"), Toast.LENGTH_SHORT).show()
            queue.cancelAll("reset")
            dismiss()
        }, {
            Log.e("error reset", it.toString())
            Toast.makeText(context, lang.getString("noNet"), Toast.LENGTH_SHORT).show()
            resetCodePB.visibility = View.GONE
            resetCodeResetBTN.visibility = View.VISIBLE
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
        reset.tag = "reset"
        queue.add(reset)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_reset_password_new)

        // region font
        val myFont = StaticInformation().myFont(context)
        resetCodeTitle.typeface = myFont
        resetCodeTV.typeface = myFont
        resetCodeCodeTV.typeface = myFont
        resetCodeCodeET.typeface = myFont
        resetCodeNewPasswordTV.typeface = myFont
        resetCodeNewPasswordET.typeface = myFont
        resetCodeResetBTN.typeface = myFont
        resetCodeCancelBtn.typeface = myFont
        // endregion

        resetCodeTitle.text = lang.getString("title")
        resetCodeTV.text = lang.getString("enterCodePassword")
        resetCodeCodeTV.text = lang.getString("code")
        resetCodeNewPasswordTV.text = lang.getString("newPassword")
        resetCodeResetBTN.text = lang.getString("button")
        resetCodeCancelBtn.text = lang.getString("returnBTN")

        resetCodeClose.setOnClickListener(this)
        resetCodeCancelBtn.setOnClickListener(this)
        resetCodeResetBTN.setOnClickListener(this)
        resetCodeEyeIV.setOnClickListener(this)
    }
}