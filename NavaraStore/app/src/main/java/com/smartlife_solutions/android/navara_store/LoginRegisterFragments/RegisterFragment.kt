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
import kotlinx.android.synthetic.main.fragment_register.*
import org.json.JSONObject
import android.support.v7.widget.AppCompatEditText
import com.rilixtech.CountryCodePicker
import com.smartlife_solutions.android.navara_store.*


@SuppressLint("ValidFragment")
class RegisterFragment (var activity: LoginRegisterActivity) : Fragment() {

    private lateinit var ccp: CountryCodePicker
    private lateinit var edtPhoneNumber: AppCompatEditText
    private var isShowPassword = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        val imgEye: ImageView = view.findViewById(R.id.passwordEyeIV)
        imgEye.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            if (isShowPassword) {
                isShowPassword = false
                imgEye.setImageResource(R.drawable.ic_eye_black)
                passwordET.transformationMethod = PasswordTransformationMethod()
            } else {
                isShowPassword = true
                imgEye.setImageResource(R.drawable.ic_eye_primary)
                passwordET.transformationMethod = null
            }
        }

        val lang = Statics.getLanguageJSONObject(activity).getJSONObject("loginRegisterActivity")

        val myFont = StaticInformation().myFont(context)
        val yourNameET: EditText = view.findViewById(R.id.yourNameET)
        val emailOrMobileNumberET: EditText = view.findViewById(R.id.emailOrMobileNumberET)
        val passwordET: EditText = view.findViewById(R.id.passwordET)
        val registerBTN: Button = view.findViewById(R.id.registerBTN)
        val emailRB = view.findViewById<RadioButton>(R.id.registerEmailRB)
        emailRB.typeface = myFont
        emailRB.text = lang.getString("email")
        val phoneRB = view.findViewById<RadioButton>(R.id.registerPhoneRB)
        phoneRB.typeface = myFont
        phoneRB.text = lang.getString("mobile")
        val phoneLL = view.findViewById<LinearLayout>(R.id.registerPhoneLL)
        val promoCodeET = view.findViewById<EditText>(R.id.registerPromoCodeET)
        promoCodeET.typeface = myFont
        promoCodeET.hint = lang.getString("promoCode")
        val promoCodeCB = view.findViewById<CheckBox>(R.id.registerPromoCodeCB)
        promoCodeCB.typeface = myFont
        promoCodeCB.text = lang.getString("havePromoCode")
        promoCodeCB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                promoCodeET.visibility = View.VISIBLE
            } else {
                promoCodeET.visibility = View.GONE
            }
        }


        yourNameET.typeface = myFont
        emailOrMobileNumberET.typeface = myFont
        emailOrMobileNumberET.hint = lang.getString("emailExample")
        passwordET.typeface = myFont
        registerBTN.typeface = myFont
        registerBTN.text = lang.getString("register")

        view.findViewById<TextView>(R.id.nameRegisterTV).typeface = myFont
        view.findViewById<TextView>(R.id.nameRegisterTV).text = lang.getString("yourName")
        view.findViewById<TextView>(R.id.emailRegisterTV).typeface = myFont
        view.findViewById<TextView>(R.id.emailRegisterTV).text = lang.getString("emailOrMobile")
        view.findViewById<TextView>(R.id.passwordRegisterTV).typeface = myFont
        view.findViewById<TextView>(R.id.passwordRegisterTV).text = lang.getString("yourPassword")

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
                            userJSON.put("FirstName", yourNameET.text.toString())
                            userJSON.put("Password", passwordET.text.toString())
                            userJSON.put("Email", id)
                            userJSON.put("CountryCode", "")
                            userJSON.put("PhoneNumber", "")
                            userJSON.put("isExternalLogin", "false")
                            userJSON.put("StationType", "Mobile")
                            if (promoCodeET.visibility == View.VISIBLE) {
                                userJSON.put("InviterCode", promoCodeET.text.toString())
                            } else {
                                userJSON.put("InviterCode", "")
                            }
                            register(userJSON)
                        } else {
                            Toast.makeText(context, lang.getString("passwordVal"), Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, lang.getString("checkEmail"), Toast.LENGTH_LONG).show()
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
                            userJSON.put("FirstName", yourNameET.text.toString())
                            userJSON.put("Password", passwordET.text.toString())
                            userJSON.put("Email", "")
                            userJSON.put("PhoneNumber", s)
                            userJSON.put("CountryCode", ccp.selectedCountryCode)
                            userJSON.put("isExternalLogin", "false")
                            userJSON.put("StationType", "Mobile")
                            if (promoCodeET.visibility == View.VISIBLE) {
                                userJSON.put("InviterCode", promoCodeET.text.toString())
                            } else {
                                userJSON.put("InviterCode", "")
                            }
                            register(userJSON)
                        } else {
                            Toast.makeText(context, lang.getString("passwordVal"), Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, "Remove first 0 from your Number please", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(context, lang.getString("checkMobile"), Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(context,lang.getString("checkName"), Toast.LENGTH_LONG).show()
            }
        }


        ccp = view.findViewById(R.id.ccp)
        edtPhoneNumber = view.findViewById(R.id.phone_number_edt)
        edtPhoneNumber.typeface = myFont
        edtPhoneNumber.hint = lang.getString("mobileExample")

        return view
    }

    internal fun register(info: JSONObject, fromFacebook: Boolean = false) {
        activity.showLoading()

        val lang = Statics.getLanguageJSONObject(activity).getJSONObject("loginRegisterActivity")

        val reqQ: RequestQueue = Volley.newRequestQueue(context)
        Log.e("info", info.toString())
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, APIsURL().REGISTER_URL, info, {
            reqQ.cancelAll("register")
            Log.e("register", it.toString())
            try {
                val user = UserModel(info.getString("UserID"), "",
                        info.getString("Email"), info.getString("CountryCode") + info.getString("PhoneNumber"),
                        info.getString("FirstName"),
                        it.getString("token"), "")
                val db = DatabaseHelper(context)
                db.clearTable(UserModel::class.java)
                db.userModelIntegerRuntimeException.create(user)
                Statics.myToken = user.token
                startActivity(Intent(context, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .putExtra("confirm", !fromFacebook)
                        .putExtra("account", info.getString("UserID")))
                activity.finish()
            } catch (err: Exception) {}
        }, {
            try {
                Log.e("register", it.message)
                if (it.networkResponse.statusCode == 500) {
                    Toast.makeText(context, lang.getString("registerError"), Toast.LENGTH_LONG).show()
                }
            } catch (err: Exception) {
                Toast.makeText(context, lang.getString("try"), Toast.LENGTH_LONG).show()
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
