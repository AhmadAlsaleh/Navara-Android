package com.smartlife_solutions.android.navara_store.LoginRegisterFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatEditText
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.facebook.login.LoginManager
import com.facebook.login.widget.LoginButton
import com.google.gson.Gson
import com.rilixtech.CountryCodePicker
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.UserModel
import com.smartlife_solutions.android.navara_store.Dialogs.ResetPasswordDialog
import com.smartlife_solutions.android.navara_store.LoginRegisterActivity
import com.smartlife_solutions.android.navara_store.MainActivity
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation
import kotlinx.android.synthetic.main.fragment_login.*
import org.json.JSONObject
import java.util.*

@SuppressLint("ValidFragment")
class LoginFragment (var activity: LoginRegisterActivity) : Fragment() {

    private lateinit var ccp: CountryCodePicker
    private lateinit var edtPhoneNumber: AppCompatEditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // region
        val view = inflater.inflate(R.layout.fragment_login, container, false)

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

        val doNotHaveAccount: TextView = view.findViewById(R.id.doNotHaveAccountTV)
        doNotHaveAccount.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            activity.setCurrentPage(1)
        }

        val forgetPassword: TextView = view.findViewById(R.id.forgetPasswordTV)
        forgetPassword.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            ResetPasswordDialog(context!!, activity).show()
        }

        // region font
        val myFont = StaticInformation().myFont(context)
        val emailOrMobile = view.findViewById<EditText>(R.id.emailOrMobileNumberET)
        emailOrMobile.typeface = myFont
        val password = view.findViewById<EditText>(R.id.passwordET)
        password.typeface = myFont
        val loginBTN = view.findViewById<Button>(R.id.loginBTN)
        loginBTN.typeface = myFont
        view.findViewById<TextView>(R.id.loginFacebookTV).typeface = myFont
        doNotHaveAccount.typeface = myFont
        forgetPassword.typeface = myFont
        view.findViewById<TextView>(R.id.emailLoginTV).typeface = myFont
        view.findViewById<TextView>(R.id.passwordLoginTV).typeface = myFont
        // endregion

        ccp = view.findViewById(R.id.ccpLogin)
        edtPhoneNumber = view.findViewById(R.id.phone_number_edt_login)
        edtPhoneNumber.typeface = myFont

        val emailRB = view.findViewById<RadioButton>(R.id.loginEmailRB)
        emailRB.typeface = myFont
        val phoneRB = view.findViewById<RadioButton>(R.id.loginPhoneRB)
        phoneRB.typeface = myFont
        val phoneLL = view.findViewById<LinearLayout>(R.id.loginPhoneLL)

        emailRB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                emailOrMobile.visibility = View.VISIBLE
                phoneLL.visibility = View.GONE
            }
        }

        phoneRB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                emailOrMobile.visibility = View.GONE
                phoneLL.visibility = View.VISIBLE
            }
        }

        loginBTN.setOnClickListener {

            if (emailRB.isChecked) {
                if (StaticInformation().isEmail(emailOrMobile.text.toString())) {
                    if (password.text.length >= 6) {
                        val info = JSONObject()
                        info.put("UserID", emailOrMobile.text.toString())
                        info.put("Password", password.text.toString())
                        login(info)
                    } else {
                        Toast.makeText(context, "password have to be 6 characters at least", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "Check your email please", Toast.LENGTH_LONG).show()
                }
            }

            if (phoneRB.isChecked) {
                if (StaticInformation().isPhone(edtPhoneNumber.text.toString())) {
                    val s = edtPhoneNumber.text.toString().trimStart('0')
                    edtPhoneNumber.setText(s)
                    if (edtPhoneNumber.text.toString()[0] != '0') {
                        if (password.text.length >= 6) {
                            val info = JSONObject()
                            info.put("UserID", "+" + ccp.selectedCountryCode + edtPhoneNumber.text.toString())
                            info.put("Password", password.text.toString())
                            login(info)
                        } else {
                            Toast.makeText(context, "password have to be 6 characters at least", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, "Remove first 0 from your Number please", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(context, "Check your email please", Toast.LENGTH_LONG).show()
                }
            }

        }

        // endregion

        // region facebook
        val faceBTN = view.findViewById<LoginButton>(R.id.facebookLoginBTN)
        faceBTN.setReadPermissions(Arrays.asList("public_profile"))
        faceBTN.registerCallback(activity.callbackManager, activity.mCallback)

        val faceLL = view.findViewById<LinearLayout>(R.id.facebookLoginLL)
        faceLL.setOnClickListener {
            val loginFacebookManager = LoginManager.getInstance()
            loginFacebookManager.logInWithReadPermissions(activity, Arrays.asList("public_profile"))
            loginFacebookManager.registerCallback(activity.callbackManager, activity.mCallback)
        }
        // endregion

        return view
    }

    private fun login(info: JSONObject) {

        activity.showLoading()

        Log.e("info", info.toString())
        val reqQ = Volley.newRequestQueue(context)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, APIsURL().LOGIN_URL, info, {
            val user = Gson().fromJson(it.toString(), UserModel::class.java)
            Log.e("token", user.token)
            Toast.makeText(context, "Welcome", Toast.LENGTH_SHORT).show()
            val db = DatabaseHelper(context)
            db.clearTable(UserModel::class.java)
            db.userModelIntegerRuntimeException.create(user)
            startActivity(Intent(context, MainActivity::class.java))
            activity.finish()
        }, {
            try {
                when (it.networkResponse.statusCode) {
                    400 -> Toast.makeText(context, "This account is not registered", Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(context, "Incorrect Password", Toast.LENGTH_SHORT).show()
                }
            } catch (err: Exception) {
                Toast.makeText(context, "No Internet Connection, Please Try Again", Toast.LENGTH_SHORT).show()
            }
            activity.hideLoading()
            reqQ.cancelAll("login")
        })
        jsonObjectRequest.tag = "login"
        reqQ.add(jsonObjectRequest)
    }

}
