package com.smartlife_solutions.android.navara_store.ProfileCartOrderFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
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
import com.rilixtech.CountryCodePicker
import com.smartlife_solutions.android.navara_store.*
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.UserModel
import com.smartlife_solutions.android.navara_store.Dialogs.ChangePasswordDialog
import com.smartlife_solutions.android.navara_store.Dialogs.ConfirmAccountDialog

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
    var cashString = ""
    var uniqueCode = ""
    var isVerify = false
    var isExternal = false
    private lateinit var changePassword: TextView
    private lateinit var logoutTV: TextView
    private lateinit var ccpProfile: CountryCodePicker
    private lateinit var confirmRL: RelativeLayout
    private lateinit var passwordRL: RelativeLayout
    private lateinit var profileCashTV: TextView
    private lateinit var profilePromoCodeTitleTV: TextView
    private lateinit var profilePromoCodeTV: TextView
    private lateinit var profilePromoCodeCopyTV: TextView

    var phoneNumber = ""
    var countryCode = ""
    private lateinit var lang: JSONObject

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        ccpProfile = view.findViewById(R.id.ccpProfile)
        confirmRL = view.findViewById(R.id.profileConfirmRL)
        passwordRL = view.findViewById(R.id.profileChangePasswordRL)

        lang = Statics.getLanguageJSONObject(activity).getJSONObject("profileCartOrdersActivity")
        // region font
        val myFont = StaticInformation().myFont(context)
        view.findViewById<TextView>(R.id.nameText).typeface = myFont
        view.findViewById<TextView>(R.id.nameText).text = lang.getString("name")
        name = view.findViewById(R.id.nameET)
        name.typeface = myFont
        view.findViewById<TextView>(R.id.emailText).typeface = myFont
        view.findViewById<TextView>(R.id.emailText).text = lang.getString("email")
        email = view.findViewById(R.id.emailET)
        email.typeface = myFont
        view.findViewById<TextView>(R.id.phoneNumberText).typeface = myFont
        view.findViewById<TextView>(R.id.phoneNumberText).text = lang.getString("mobile")
        phone = view.findViewById(R.id.phoneNumberET)
        phone.typeface = myFont
        val saveInfoBTN = view.findViewById<Button>(R.id.saveProfileBTN)
        saveInfoBTN.typeface = myFont
        saveInfoBTN.text = lang.getString("save")
        changePassword = view.findViewById(R.id.changePasswordText)
        changePassword.typeface = myFont
        changePassword.text = lang.getString("changeMyPassword")
        confirmTV = view.findViewById(R.id.confirmTV)
        confirmTV.typeface = myFont
        confirmTV.text = lang.getString("verifyYourAccount")
        logoutTV = view.findViewById(R.id.profileLogout)
        logoutTV.typeface = myFont
        logoutTV.text = lang.getString("logout")
        profileCashTV = view.findViewById(R.id.profileCashTV)
        profileCashTV.typeface = myFont
        profilePromoCodeTitleTV = view.findViewById(R.id.profilePromoCodeTitleTV)
        profilePromoCodeTitleTV.typeface = myFont
        profilePromoCodeTitleTV.text = lang.getString("yourCode")
        profilePromoCodeTV = view.findViewById(R.id.profilePromoCodeTV)
//        profilePromoCodeTV.typeface = myFont
        profilePromoCodeCopyTV = view.findViewById(R.id.profilePromoCodeCopyTV)
        profilePromoCodeCopyTV.typeface = myFont
        profilePromoCodeCopyTV.text = lang.getString("copy")
        val profileUsedItem = view.findViewById<Button>(R.id.profileUsedItemBTN)
        profileUsedItem.typeface = myFont
        profileUsedItem.text = lang.getString("myUsedItems")
        // endregion

        profilePromoCodeCopyTV.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        profilePromoCodeCopyTV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            if (StaticInformation().copyToClip(activity, "Promo Code", uniqueCode)) {
                Toast.makeText(context, lang.getString("copied"), Toast.LENGTH_SHORT).show()
            }
        }

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

        profileUsedItem.setOnClickListener {
            startActivity(Intent(context, MyUsedItemsActivity::class.java)
                    .putExtra("name", nameString)
                    .putExtra("countryCode", countryCode)
                    .putExtra("mobile", phoneString))
        }

        changePassword.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            ChangePasswordDialog(context!!, activity).show()
        }

        confirmTV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            ConfirmAccountDialog(context!!, myAccount, confTV = confirmTV, lang = Statics.getLanguageJSONObject(activity).getJSONObject("dialogs").getJSONObject("confirm")).show()
        }

        name.setText(nameString)
        email.setText(if (emailString == "null") { "" } else { emailString })
        phone.setText(if (phoneString == "null" || phoneString.length < 3) { "" } else { phoneString })

        val cash = if (cashString == "null") {
            0F
        } else {
            cashString.toFloat()
        }
        profileCashTV.text = "${lang.getString("yourWallet")} ${StaticInformation().formatPrice(cash.toInt())} ${Statics.getLanguageJSONObject(activity).getString("currencyCode")}"
        profilePromoCodeTV.text = uniqueCode

        if (isExternal) {
            changePassword.visibility = View.GONE
            passwordRL.visibility = View.GONE
            confirmTV.visibility = View.GONE
            confirmRL.visibility = View.GONE

            try {
                ccpProfile.setCountryForPhoneCode(countryCode.toInt())
                phone.setText(phoneNumber)
            } catch (err: Exception) {}
        } else {
            if (isVerify) {
                confirmTV.visibility = View.GONE
                confirmRL.visibility = View.GONE
            } else {
                confirmTV.visibility = View.VISIBLE
                confirmRL.visibility = View.VISIBLE
            }
            when {
                StaticInformation().isEmail(myAccount) -> {
                    fixEditText(email)
                    try {
                        ccpProfile.setCountryForPhoneCode(countryCode.toInt())
                    } catch (err: Exception) {}
                    phone.setText(phoneNumber)
                }
                StaticInformation().isPhone(myAccount) && !isExternal -> {
                    fixEditText(phone, fromPhone = true)
                    try {
                        ccpProfile.setCountryForPhoneCode(countryCode.toInt())
                    } catch (err: Exception) {}
                    phone.setText(phoneNumber)
                    view.findViewById<LinearLayout>(R.id.profilePhoneLL)
                            .setBackgroundResource(R.drawable.edit_background_gray)
                    view.findViewById<RelativeLayout>(R.id.profilePhoneRL).visibility = View.VISIBLE
                }
            }

        }

        logoutTV.setOnClickListener {
            logout()
        }

        return view
    }

    private fun logout() {
        if (!StaticInformation().isConnected(activity)) {
            Toast.makeText(context, Statics.getLanguageJSONObject(activity).getString("noInternet"), Toast.LENGTH_SHORT).show()
            return
        }

        activity.setPreLoader()

        val queue = Volley.newRequestQueue(context)
        val request = object : StringRequest(Request.Method.GET, APIsURL().LOGOUT_URL,
                Response.Listener<String> {
                    queue.cancelAll("logout")
                    Log.e("logout", "True")
                    DatabaseHelper(context).clearTable(UserModel::class.java)
                    Statics.myToken = ""
                    activity.finish()
                    queue.cancelAll("logout")
                }, Response.ErrorListener {
            Toast.makeText(context, Statics.getLanguageJSONObject(activity).getString("noInternet"), Toast.LENGTH_SHORT).show()
            activity.stopLoader(0)
            queue.cancelAll("logout")
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=UTF-8"
                params["token"] = Statics.myToken
                return params
            }
        }
        request.tag = "logout"
        queue.add(request)

    }

    @SuppressLint("ResourceAsColor")
    private fun fixEditText(editText: EditText, fromPhone: Boolean = false) {
        editText.isFocusable = false
        editText.isFocusableInTouchMode = false
        if (fromPhone) {
            editText.setBackgroundResource(android.R.color.transparent)
        } else {
            editText.setBackgroundResource(R.drawable.edit_background_gray)
        }
        editText.setTextColor(R.color.gray)
    }

    private fun updateUserInfo() {

        val reqBodyJson = JSONObject()
        reqBodyJson.put("FirstName", name.text.toString())
        reqBodyJson.put("Email", email.text.toString())
        if (phone.text.toString().trimStart('0').trim().isNotEmpty()) {
            reqBodyJson.put("PhoneNumber", phone.text.toString().trimStart('0').trim())
            reqBodyJson.put("CountryCode", ccpProfile.selectedCountryCode)
        } else {
            reqBodyJson.put("PhoneNumber", "")
            reqBodyJson.put("CountryCode", "")
        }
        val queue = Volley.newRequestQueue(context)
        val request = object : StringRequest(Request.Method.POST, APIsURL().UPDATE_USER_INFORMATION, {
            activity.stopLoader(0)
            Toast.makeText(context, lang.getString("updated"), Toast.LENGTH_SHORT).show()
            Log.e("data", it.toString())
        }, {
            try {
                Log.e("error", it.networkResponse.statusCode.toString())
            } catch (err: Exception) {}
            activity.stopLoader(0)
            Toast.makeText(context, Statics.getLanguageJSONObject(activity).getString("noInternet"),Toast.LENGTH_SHORT).show()
            queue.cancelAll("info")
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
