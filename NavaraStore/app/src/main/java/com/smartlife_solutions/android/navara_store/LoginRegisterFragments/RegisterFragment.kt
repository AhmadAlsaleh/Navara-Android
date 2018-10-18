package com.smartlife_solutions.android.navara_store.LoginRegisterFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.UserModel
import com.smartlife_solutions.android.navara_store.LoginRegisterActivity
import com.smartlife_solutions.android.navara_store.MainActivity
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation
import kotlinx.android.synthetic.main.fragment_register.*
import org.json.JSONObject
import android.support.v7.widget.AppCompatEditText
import com.rilixtech.CountryCodePicker


@SuppressLint("ValidFragment")
class RegisterFragment (var activity: LoginRegisterActivity) : Fragment() {

    private lateinit var ccp: CountryCodePicker
    private lateinit var edtPhoneNumber: AppCompatEditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        val imgEye: ImageView = view.findViewById(R.id.passwordEyeIV)
        imgEye.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            if (imgEye.drawable.constantState == resources.getDrawable(R.drawable.ic_eye_primary).constantState) {
                imgEye.setImageResource(R.drawable.ic_eye_black)
                passwordET.transformationMethod = PasswordTransformationMethod()
            } else {
                imgEye.setImageResource(R.drawable.ic_eye_primary)
                passwordET.transformationMethod = null
            }
        }

        val myFont = StaticInformation().myFont(context)
        val yourNameET: EditText = view.findViewById(R.id.yourNameET)
        val emailOrMobileNumberET: EditText = view.findViewById(R.id.emailOrMobileNumberET)
        val passwordET: EditText = view.findViewById(R.id.passwordET)
        val registerBTN: Button = view.findViewById(R.id.registerBTN)
        val emailRB = view.findViewById<RadioButton>(R.id.registerEmailRB)
        emailRB.typeface = myFont
        val phoneRB = view.findViewById<RadioButton>(R.id.registerPhoneRB)
        phoneRB.typeface = myFont
        val phoneLL = view.findViewById<LinearLayout>(R.id.registerPhoneLL)

        yourNameET.typeface = myFont
        emailOrMobileNumberET.typeface = myFont
        passwordET.typeface = myFont
        registerBTN.typeface = myFont

        view.findViewById<TextView>(R.id.nameRegisterTV).typeface = myFont
        view.findViewById<TextView>(R.id.emailRegisterTV).typeface = myFont
        view.findViewById<TextView>(R.id.passwordRegisterTV).typeface = myFont

        emailRB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                emailOrMobileNumberET.visibility = View.VISIBLE
                phoneLL.visibility = View.GONE
            }
        }

        phoneRB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                emailOrMobileNumberET.visibility = View.GONE
                phoneLL.visibility = View.VISIBLE
            }
        }

        registerBTN.setOnClickListener {
            if (checkName(yourNameET.text.toString())) {

                if (emailRB.isChecked) {
                    if (StaticInformation().isEmail(emailOrMobileNumberET.text.toString())) {

                        if (passwordET.text.length >= 6) {
                            val id = emailOrMobileNumberET.text.toString()
                            val userJSON = JSONObject()
                            userJSON.put("UserID", id)
                            userJSON.put("Username", id)
                            userJSON.put("FirstName", yourNameET.text.toString())
                            userJSON.put("Password", passwordET.text.toString())
                            userJSON.put("Email", id)
                            userJSON.put("PhoneNumber", "")
                            userJSON.put("isExternalLogin", "false")
                            register(userJSON)
                        } else {
                            Toast.makeText(context, "password have to be 6 characters at least", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, "Check your email please", Toast.LENGTH_LONG).show()
                    }
                    return@setOnClickListener
                }
                if (phoneRB.isChecked && StaticInformation().isPhone(edtPhoneNumber.text.toString())) {
                    val s = edtPhoneNumber.text.toString().trimStart('0')
                    edtPhoneNumber.setText(s)
                    if (edtPhoneNumber.text.toString()[0] != '0') {
                        if (passwordET.text.length >= 6) {
                            val id = "+" + ccp.selectedCountryCode + edtPhoneNumber.text.toString()
                            val userJSON = JSONObject()
                            userJSON.put("UserID", id)
                            userJSON.put("Username", id)
                            userJSON.put("FirstName", yourNameET.text.toString())
                            userJSON.put("Password", passwordET.text.toString())
                            userJSON.put("Email", "")
                            userJSON.put("PhoneNumber", id)
                            userJSON.put("isExternalLogin", "false")
                            register(userJSON)
                        } else {
                            Toast.makeText(context, "password have to be 6 characters at least", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, "Remove first 0 from your Number please", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(context, "Check your mobile number please", Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(context,"Enter your name", Toast.LENGTH_LONG).show()
            }
        }


        ccp = view.findViewById(R.id.ccp)
        edtPhoneNumber = view.findViewById(R.id.phone_number_edt)
        edtPhoneNumber.typeface = myFont

        return view
    }

    internal fun register(info: JSONObject, fromFacebook: Boolean = false) {
        activity.showLoading()

        val reqQ: RequestQueue = Volley.newRequestQueue(context)
        Log.e("info", info.toString())
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, APIsURL().REGISTER_URL, info, {
            reqQ.cancelAll("register")
            Log.e("register", it.toString())

            val user = UserModel(info.getString("UserID"), info.getString("Username"),
                    info.getString("Email"), info.getString("PhoneNumber"), info.getString("FirstName"),
                    it.getString("token"), "")
            val db = DatabaseHelper(context)
            db.clearTable(UserModel::class.java)
            db.userModelIntegerRuntimeException.create(user)

            startActivity(Intent(context, MainActivity::class.java).putExtra("confirm", !fromFacebook)
                    .putExtra("account", info.getString("UserID")))
            activity.finish()
        }, {
            try {
                if (it.networkResponse.statusCode == 500) {
                    Toast.makeText(context, "This User already taken\nChoose another one please", Toast.LENGTH_LONG).show()
                }
            } catch (err: Exception) {
                Toast.makeText(context, "Please Try Again", Toast.LENGTH_LONG).show()
            }
            activity.hideLoading()
            reqQ.cancelAll("register")
        })
        jsonObjectRequest.tag = "register"
        reqQ.add(jsonObjectRequest)
    }

    private fun checkName(name: String): Boolean {
        return name.isNotEmpty()
    }

}
