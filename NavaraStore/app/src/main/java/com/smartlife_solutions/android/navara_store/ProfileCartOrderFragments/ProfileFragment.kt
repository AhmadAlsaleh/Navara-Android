package com.smartlife_solutions.android.navara_store.ProfileCartOrderFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.Dialogs.ChangePasswordDialog
import com.smartlife_solutions.android.navara_store.Dialogs.ConfirmAccountDialog
import com.smartlife_solutions.android.navara_store.OnSwipeTouchListener
import com.smartlife_solutions.android.navara_store.ProfileCartOrders

import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation
import kotlinx.android.synthetic.main.fragment_profile.*
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

@SuppressLint("ValidFragment")
class ProfileFragment (var activity: ProfileCartOrders) : Fragment() {

    var isChange: Boolean = false
    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var phone: EditText
    private lateinit var confirmTV: TextView
    var myAccount = ""
    var nameString = ""
    var phoneString = ""
    var emailString = ""
    var isVerify = false
    var isExternal = false
    private lateinit var changePassword: TextView

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // region font
        val myFont = StaticInformation().myFont(context)
        view.findViewById<TextView>(R.id.nameText).typeface = myFont
        name = view.findViewById(R.id.nameET)
        name.typeface = myFont
        view.findViewById<TextView>(R.id.emailText).typeface = myFont
        email = view.findViewById(R.id.emailET)
        email.typeface = myFont
        view.findViewById<TextView>(R.id.phoneNumberText).typeface = myFont
        phone = view.findViewById(R.id.phoneNumberET)
        phone.typeface = myFont
        val saveInfoBTN = view.findViewById<Button>(R.id.saveProfileBTN)
        saveInfoBTN.typeface = myFont
        changePassword = view.findViewById(R.id.changePasswordText)
        changePassword.typeface = myFont
        confirmTV = view.findViewById(R.id.confirmTV)
        confirmTV.typeface = myFont
        // endregion

        // region focus listeners
        name.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                isChange = true
            }
        }

        email.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                isChange = true
            }
        }

        phone.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                isChange = true
            }
        }
        // endregion

        saveInfoBTN.setOnClickListener {
            if (isChange) {
                activity.setPreLoader()
                isChange = false
                StaticInformation().hideKeyboard(activity)
                updateUserInfo()
            }
        }

        changePassword.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            ChangePasswordDialog(context!!, activity).show()
        }

        confirmTV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            ConfirmAccountDialog(context!!, myAccount).show()
        }

        view.findViewById<RelativeLayout>(R.id.profileRL).setOnTouchListener(object : OnSwipeTouchListener(context) {})

        name.setText(nameString)
        email.setText(if (emailString == "null") { "" } else { emailString })
        phone.setText(if (phoneString == "null") { "" } else { phoneString })

        if (isExternal) {
            changePassword.visibility = View.GONE
            confirmTV.visibility = View.GONE
        } else {
            if (isVerify) {
                confirmTV.visibility = View.GONE
            } else {
                confirmTV.visibility = View.VISIBLE
            }
            if (StaticInformation().isEmail(myAccount)) {
                fixEditText(email)
            } else {
                fixEditText(phone)
            }
        }

        return view
    }

    @SuppressLint("ResourceAsColor")
    private fun fixEditText(editText: EditText) {
        editText.isFocusable = false
        editText.isFocusableInTouchMode = false
        editText.setBackgroundResource(R.drawable.edit_background_gray)
        editText.setTextColor(R.color.gray)
    }

    private fun updateUserInfo() {
        val myToken= try {
            DatabaseHelper(context).userModelIntegerRuntimeException.queryForAll()[0].token
        } catch (err: Exception) {
            ""
        }
        val reqBodyJson = JSONObject()
        reqBodyJson.put("FirstName", name.text.toString())
        reqBodyJson.put("Email", email.text.toString())
        reqBodyJson.put("PhoneNumber", phone.text.toString())
        val queue = Volley.newRequestQueue(context)
        val request = object : StringRequest(Request.Method.POST, APIsURL().UPDATE_USER_INFORMATION, {
            activity.stopLoader(0)
            Toast.makeText(context, "Your Information has Updated Successfully", Toast.LENGTH_SHORT).show()
            Log.e("data", it.toString())
        }, {
            try {
                Log.e("error", it.networkResponse.statusCode.toString())
            } catch (err: Exception) {}
            Toast.makeText(context, "No Internet Connection, Please Try Again",Toast.LENGTH_SHORT).show()
            queue.cancelAll("info")
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
                    reqBodyJson.toString().toByteArray(Charset.forName("utf-8"))
                } catch (err: UnsupportedEncodingException) {
                    null
                }
            }
        }
        request.tag = "info"
        queue.add(request)
    }

}
