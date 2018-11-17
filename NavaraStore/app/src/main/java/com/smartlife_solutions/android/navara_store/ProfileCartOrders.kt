package com.smartlife_solutions.android.navara_store

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.smartlife_solutions.android.navara_store.Adapters.AccountPagerAdapter
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.Dialogs.SureToDoDialog
import com.smartlife_solutions.android.navara_store.ProfileCartOrderFragments.CartFragment
import com.smartlife_solutions.android.navara_store.ProfileCartOrderFragments.OrdersFragment
import com.smartlife_solutions.android.navara_store.ProfileCartOrderFragments.ProfileFragment
import kotlinx.android.synthetic.main.activity_profile_cart_orders.*
import java.util.*

class ProfileCartOrders : AppCompatActivity() {

    private var finishOnBack = false
    private var isInProfile = true
    private var profileFragment = ProfileFragment(this)
    private var cartFragment = CartFragment()
    private var ordersFragment = OrdersFragment()
    private var currentPage = 0

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_cart_orders)

        if (!StaticInformation().isConnected(this)) {
            setNoInternet()
            return
        } else {
            setPreLoader()
        }

        if (Statics.getCurrentLanguageName(this) == Statics.arabic) {
            val conf = resources.configuration
            conf.setLayoutDirection(Locale("fa"))
            resources.updateConfiguration(conf, resources.displayMetrics)
        } else {
            val conf = resources.configuration
            conf.setLayoutDirection(Locale("en"))
            resources.updateConfiguration(conf, resources.displayMetrics)
        }

        currentPage = intent.getIntExtra("currentPage", 0)
        finishOnBack = intent.getBooleanExtra(StaticInformation().FINITSH_ON_BACK, false)

        getUserInfo()

        // region font
        val lang = Statics.getLanguageJSONObject(this).getJSONObject("profileCartOrdersActivity")
        val myFont = StaticInformation().myFont(this)
        profileTabBTN.typeface = myFont
        profileTabBTN.text = lang.getString("profile")
        cartTabBTN.typeface = myFont
        cartTabBTN.text = lang.getString("myCart")
        orderTabBTN.typeface = myFont
        orderTabBTN.text = lang.getString("myOrders")
        // endregion

    }

    private fun getUserInfo() {

        val queue = Volley.newRequestQueue(this)
        val request = object : JsonObjectRequest(Request.Method.GET, APIsURL().GET_USER_INFORMATION, null, {
            Log.e("profile", "Done")
            Log.e("profile", it.toString())
            profileFragment.nameString = it.getString("name")
            profileFragment.emailString = if (it.getString("email") == "null") { "" } else { it.getString("email") }
            profileFragment.phoneString = if (it.getString("mobile") == "null") { "" } else { it.getString("mobile") }
            profileFragment.myAccount = it.getString("userName")
            profileFragment.isExternal = it.getBoolean("isExternalLogin")
            profileFragment.isVerify = it.getBoolean("isVerified")
            profileFragment.countryCode = it.getString("countryCode")
            profileFragment.phoneNumber = it.getString("phoneNumber")
            profileFragment.cashString = it.getString("wallet")
            profileFragment.uniqueCode = if (it.getString("uniqueCode") == null) { "" } else { it.getString("uniqueCode") }

            getCartItems()

            queue.cancelAll("info")

        }, {
            Log.e("error", "error")
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

        }
        request.tag = "info"
        queue.add(request)
    }

    private fun getCartItems() {

        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, APIsURL().GET_CART, null, {
            Log.e("cart", it.toString())
            cartFragment.itJSONArray = it.getJSONArray("items")
            getOrders()
        }, {
            try {
                Log.e("error", it.networkResponse.statusCode.toString())
            } catch (err: Exception) {}
            queue.cancelAll("cart")
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

        }
        jsonObjectRequest.tag = "cart"
        queue.add(jsonObjectRequest)
    }

    private fun getOrders() {

        val queue = Volley.newRequestQueue(this)
        val jsonArrayRequest = object : JsonArrayRequest(Request.Method.GET, APIsURL().GET_ORDERS, null, {
            stopLoader()
            ordersFragment.orderJSONArray = it
            setupViewPager()
            setPagerPosition(currentPage)
            Log.e("Orders Array", it.toString())
            queue.cancelAll("orders")
        }, {
            try {
                Log.e("error", it.networkResponse.statusCode.toString())
            } catch (err: Exception) {}
            queue.cancelAll("orders")
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

        }
        jsonArrayRequest.tag = "orders"
        queue.add(jsonArrayRequest)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setFragment(fragment: Fragment, tabPosition: Int) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.profileCartOrderFL, fragment)
        ft.commit()
        setBlackTabsText()
        when (tabPosition) {
            0 -> {
                profileTabBTN.setTextColor(resources.getColor(R.color.navaraPrimary))
                isInProfile = true
            }
            1 -> {
                cartTabBTN.setTextColor(resources.getColor(R.color.navaraPrimary))
                isInProfile = false
            }
            2 -> {
                orderTabBTN.setTextColor(resources.getColor(R.color.navaraPrimary))
                isInProfile = false
            }
        }
    }

    private fun setNoInternet() {
        val fragmentTranslate = supportFragmentManager.beginTransaction()
        fragmentTranslate.replace(R.id.loadProfileFL, NoInternetFragment(Statics.getLanguageJSONObject(this).getString("noConnection")))
        fragmentTranslate.commit()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBackPressed() {
        if (finishOnBack) {
            finish()
        } else {
            if (!isInProfile) {
                setPagerPosition(0)
                return
            }
        }

        if (profileFragment.isChange) {
            val sureCancel = SureToDoDialog(this, "Sure to discard changes?")
            sureCancel.show()
            sureCancel.setOnDismissListener {
                if (sureCancel.isTrue) {
                    if (intent.getBooleanExtra(Statics.fromNotification, false)) {
                        startActivity(Intent(this, MainActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    } else {
                        finish()
                    }
                }
            }
        } else {
            if (intent.getBooleanExtra(Statics.fromNotification, false)) {
                startActivity(Intent(this, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            } else {
                finish()
            }
        }

    }

    private var load = LoadingFragment()
    @SuppressLint("CommitTransaction")
    fun setPreLoader() {
        val fragmentTranslate = supportFragmentManager.beginTransaction()
        fragmentTranslate.replace(R.id.loadProfileFL, load)
        fragmentTranslate.commit()
    }

    fun stopLoader(mSec: Long = 1000) {
        Handler().postDelayed({
            try {
                val fragmentTranslate = supportFragmentManager.beginTransaction()
                fragmentTranslate.remove(load)
                fragmentTranslate.commit()
            } catch (err: Exception) {}
        }, mSec)
    }

    private fun setPagerPosition(currentPage: Int) {
        profileCartOrderVP.currentItem = currentPage
    }

    fun getPagerPosition(): Int {
        return profileCartOrderVP.currentItem
    }

    private fun setupViewPager() {
        val adapter = AccountPagerAdapter(supportFragmentManager)
        adapter.addFragment(profileFragment, "Profile")
        adapter.addFragment(cartFragment, "My Cart")
        adapter.addFragment(ordersFragment, "My Order")

        profileCartOrderVP.adapter = adapter

        profileCartOrderVP.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(p0: Int) {}

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

            @TargetApi(Build.VERSION_CODES.M)
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onPageSelected(p0: Int) {
                setBlackTabsText()
                when (p0) {
                    0 -> profileTabBTN.setTextColor(resources.getColor(R.color.navaraPrimary))
                    1 -> cartTabBTN.setTextColor(resources.getColor(R.color.navaraPrimary))
                    2 -> orderTabBTN.setTextColor(resources.getColor(R.color.navaraPrimary))
                }
            }

        })

        profileTabBTN.setOnClickListener {
            setPagerPosition(0)
        }

        cartTabBTN.setOnClickListener {
            setPagerPosition(1)
        }

        orderTabBTN.setOnClickListener {
            setPagerPosition(2)
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setBlackTabsText() {
        profileTabBTN.setTextColor(resources.getColor(R.color.blackItem))
        cartTabBTN.setTextColor(resources.getColor(R.color.blackItem))
        orderTabBTN.setTextColor(resources.getColor(R.color.blackItem))
    }

}
