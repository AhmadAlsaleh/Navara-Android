package com.smartlife_solutions.android.navara_store

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.smartlife_solutions.android.navara_store.Adapters.LoginRegisterPagerAdapter
import com.smartlife_solutions.android.navara_store.Dialogs.AllDoneDialog
import com.smartlife_solutions.android.navara_store.LoginRegisterFragments.LoginFragment
import com.smartlife_solutions.android.navara_store.LoginRegisterFragments.RegisterFragment
import kotlinx.android.synthetic.main.activity_login_register.*
import kotlinx.android.synthetic.main.fragment_loading.*
import org.json.JSONObject

class LoginRegisterActivity : AppCompatActivity() {

    lateinit var doneReset: AllDoneDialog
    private var fromMain = true

    var callbackManager: CallbackManager? = null
    var mCallback = object : FacebookCallback<LoginResult> {
        override fun onSuccess(result: LoginResult?) {
            val profile = Profile.getCurrentProfile()
            try {
                Log.e("facebook name", profile.name)
                val userJSON = JSONObject()
                userJSON.put("UserID", profile.id)
                userJSON.put("Username", profile.id)
                userJSON.put("FirstName", profile.name)
                userJSON.put("Email", "")
                userJSON.put("PhoneNumber", "")
                userJSON.put("Password", profile.id + "navar@SmartLife")
                userJSON.put("isExternalLogin", "true")
                registerFragment.register(userJSON, true)
            } catch (err: Exception) {
                Log.e("error", err.message)
            }
            LoginManager.getInstance().logOut()
        }

        override fun onCancel() {
            Toast.makeText(this@LoginRegisterActivity, "CANCELED", Toast.LENGTH_SHORT).show()
        }

        override fun onError(error: FacebookException?) {
            Log.e("facebook error", error?.message)
            Toast.makeText(this@LoginRegisterActivity, "Try Again", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // region facebook
        try {
            FacebookSdk.sdkInitialize(applicationContext)
            callbackManager = CallbackManager.Factory.create()
            val att = object : AccessTokenTracker() {
                override fun onCurrentAccessTokenChanged(oldAccessToken: AccessToken?, currentAccessToken: AccessToken?) {
                }
            }
            val pt = object : ProfileTracker() {
                override fun onCurrentProfileChanged(oldProfile: Profile?, currentProfile: Profile?) {
                }
            }
            att.startTracking()
            pt.startTracking()
        } catch (err: Exception) {
            Log.e("face", err.message)
        }
        // endregion
        setContentView(R.layout.activity_login_register)

        setupViewPager()
        intent.getBooleanExtra("main", true)

        loginTabBTN.typeface = StaticInformation().myFont(this)
        loginTabBTN.setOnClickListener {
            loginRegisterVP.setCurrentItem(0, true)
        }

        registerTabBTN.setOnClickListener {
            loginRegisterVP.setCurrentItem(1, true)
        }
        registerTabBTN.typeface = StaticInformation().myFont(this)

        val rotateAnimation = RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f)

        rotateAnimation.interpolator = LinearInterpolator()
        rotateAnimation.duration = 3000
        rotateAnimation.repeatCount = Animation.INFINITE
        loadingIV.startAnimation(rotateAnimation)
        loadingTV.typeface = StaticInformation().myFont(this)

        hideLoading()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }

    fun hideLoading() {
        loadingRL.visibility = View.GONE
    }

    fun showLoading() {
        loadingRL.visibility = View.VISIBLE
    }

    fun setCurrentPage(position: Int) {
        loginRegisterVP.currentItem = position
    }

    private val loginFragment = LoginFragment(this)
    private val registerFragment = RegisterFragment(this)
    private fun setupViewPager() {
        val adapter = LoginRegisterPagerAdapter(supportFragmentManager)
        adapter.addFragment(loginFragment, "Login")
        adapter.addFragment(registerFragment, "Register")
        loginRegisterVP.adapter = adapter

        loginRegisterVP?.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onPageSelected(p0: Int) {
                when(p0) {
                    0 -> {
                        loginTabBTN.setTextColor(getColor(R.color.navaraPrimary))
                        loginTabBTN.setTypeface(loginTabBTN.typeface, Typeface.BOLD)
                        registerTabBTN.setTextColor(getColor(R.color.blackItem))
                        registerTabBTN.setTypeface(registerTabBTN.typeface, Typeface.NORMAL)
                    }

                    1 -> {
                        loginTabBTN.setTextColor(getColor(R.color.blackItem))
                        loginTabBTN.setTypeface(loginTabBTN.typeface, Typeface.NORMAL)
                        registerTabBTN.setTextColor(getColor(R.color.navaraPrimary))
                        registerTabBTN.setTypeface(registerTabBTN.typeface, Typeface.BOLD)
                    }
                }
            }

        })
    }

    override fun onBackPressed() {
        if (fromMain) {
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        } else {
            finish()
        }
    }

}
