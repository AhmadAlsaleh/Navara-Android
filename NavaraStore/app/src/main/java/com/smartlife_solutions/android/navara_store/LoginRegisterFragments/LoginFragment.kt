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
import com.smartlife_solutions.android.navara_store.*
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.UserModel
import com.smartlife_solutions.android.navara_store.Dialogs.AllDoneDialog
import com.smartlife_solutions.android.navara_store.Dialogs.ResetPasswordDialog
import kotlinx.android.synthetic.main.fragment_login.*
import org.json.JSONObject
import java.util.*

@SuppressLint("ValidFragment")
class LoginFragment (var activity: LoginRegisterActivity) : Fragment() {

    private lateinit var ccp: CountryCodePicker
    private lateinit var edtPhoneNumber: AppCompatEditText
    private var isShowPassword = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // region main
        val view = inflater.inflate(R.layout.fragment_login, container, false)

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

        val lang = Statics.getLanguageJSONObject(activity).getJSONObject("loginRegisterActivity")
        // region font
        val myFont = StaticInformation().myFont(context)
        val emailOrMobile = view.findViewById<EditText>(R.id.emailOrMobileNumberET)
        emailOrMobile.typeface = myFont
        emailOrMobile.hint = lang.getString("emailExample")
        val password = view.findViewById<EditText>(R.id.passwordET)
        password.typeface = myFont
        val loginBTN = view.findViewById<Button>(R.id.loginBTN)
        loginBTN.typeface = myFont
        loginBTN.text = lang.getString("login")
        val loginFacebook = view.findViewById<TextView>(R.id.loginFacebookTV)
        loginFacebook.typeface = myFont
        loginFacebook.text = lang.getString("loginWithFacebook")
        doNotHaveAccount.typeface = myFont
        doNotHaveAccount.text = lang.getString("doNotHaveAccount")
        forgetPassword.typeface = myFont
        forgetPassword.text = lang.getString("forgetPassword")
        val emailLogin = view.findViewById<TextView>(R.id.emailLoginTV)
        emailLogin.typeface = myFont
        emailLogin.text = lang.getString("emailOrMobile")
        val passwordLogin = view.findViewById<TextView>(R.id.passwordLoginTV)
        passwordLogin.typeface = myFont
        passwordLogin.text = lang.getString("yourPassword")
        // endregion

        ccp = view.findViewById(R.id.ccpLogin)
        edtPhoneNumber = view.findViewById(R.id.phone_number_edt_login)
        edtPhoneNumber.typeface = myFont
        edtPhoneNumber.hint = lang.getString("mobileExample")

        val emailRB = view.findViewById<RadioButton>(R.id.loginEmailRB)
        emailRB.typeface = myFont
        emailRB.text = lang.getString("email")
        val phoneRB = view.findViewById<RadioButton>(R.id.loginPhoneRB)
        phoneRB.typeface = myFont
        phoneRB.text = lang.getString("mobile")
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
                        Toast.makeText(context, lang.getString("passwordVal"), Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, lang.getString("checkEmail"), Toast.LENGTH_LONG).show()
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
                            Toast.makeText(context, lang.getString("passwordVal"), Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, "Remove first 0 from your Number please", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(context, lang.getString("checkEmail"), Toast.LENGTH_LONG).show()
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

        // region set texts

        val translate = Statics.getLanguageJSONObject(activity).getJSONObject("loginRegisterActivity")
        emailLogin.text = translate.getString("emailOrMobile")
        emailRB.text = translate.getString("email")
        phoneRB.text = translate.getString("mobile")
        passwordLogin.text = translate.getString("yourPassword")
        loginFacebook.text = translate.getString("loginWithFacebook")
        forgetPassword.text = translate.getString("forgetPassword")
        doNotHaveAccount.text = translate.getString("doNotHaveAccount")
        emailOrMobile.hint = translate.getString("emailExample")
        edtPhoneNumber.hint = translate.getString("mobileExample")
        loginBTN.text = translate.getString("login")

        // endregion

        return view
    }

    private fun login(info: JSONObject) {
        val lang = Statics.getLanguageJSONObject(activity).getJSONObject("loginRegisterActivity")
        activity.showLoading()

        Log.e("info", info.toString())
        val reqQ = Volley.newRequestQueue(context)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, APIsURL().LOGIN_URL, info, {
            try {
                val user = Gson().fromJson(it.toString(), UserModel::class.java)
                Log.e("token", user.token)
                Toast.makeText(context, lang.getString("welcome"), Toast.LENGTH_SHORT).show()
                val db = DatabaseHelper(context)
                db.clearTable(UserModel::class.java)
                db.userModelIntegerRuntimeException.create(user)
                Statics.myToken = user.token
                startActivity(Intent(context, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                activity.finish()
            } catch (err: Exception) {}
        }, {
            try {
                when (it.networkResponse.statusCode) {
                    400 -> Toast.makeText(context, lang.getString("loginError"), Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(context, lang.getString("passwordInc"), Toast.LENGTH_SHORT).show()
                }
            } catch (err: Exception) {
                Toast.makeText(context, lang.getString("noNet"), Toast.LENGTH_SHORT).show()
            }
            activity.hideLoading()
            reqQ.cancelAll("login")
        })
        jsonObjectRequest.tag = "login"
        reqQ.add(jsonObjectRequest)
    }

}
