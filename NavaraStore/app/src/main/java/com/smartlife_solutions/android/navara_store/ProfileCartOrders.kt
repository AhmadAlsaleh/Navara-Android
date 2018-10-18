package com.smartlife_solutions.android.navara_store

import android.annotation.SuppressLint
import android.annotation.TargetApi
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

        currentPage = intent.getIntExtra("currentPage", 0)
        finishOnBack = intent.getBooleanExtra(StaticInformation().FINITSH_ON_BACK, false)

        getUserInfo()

        // region font
        val myFont = StaticInformation().myFont(this)
        profileTabBTN.typeface = myFont
        cartTabBTN.typeface = myFont
        orderTabBTN.typeface = myFont
        // endregion

    }

    private fun getUserInfo() {
        val myToken= try {
            DatabaseHelper(this).userModelIntegerRuntimeException.queryForAll()[0].token
        } catch (err: Exception) {
            ""
        }
        val queue = Volley.newRequestQueue(this)
        val request = object : JsonObjectRequest(Request.Method.GET, APIsURL().GET_USER_INFORMATION, null, {
            Log.e("profile", "Done")

            profileFragment.nameString = it.getString("name")
            profileFragment.emailString = if (it.getString("email") == "null") { "" } else { it.getString("email") }
            profileFragment.phoneString = if (it.getString("mobile") == "null") { "" } else { it.getString("mobile") }
            profileFragment.myAccount = it.getString("userName")
            profileFragment.isExternal = it.getBoolean("isExternalLogin")
            profileFragment.isVerify = it.getBoolean("isVerified")

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
                params["Authorization"] = "Bearer $myToken"
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
        val myToken= try {
            DatabaseHelper(this).userModelIntegerRuntimeException.queryForAll()[0].token
        } catch (err: Exception) {
            ""
        }
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
                params["Authorization"] = "Bearer $myToken"
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
        val myToken= try {
            DatabaseHelper(this).userModelIntegerRuntimeException.queryForAll()[0].token
        } catch (err: Exception) {
            ""
        }
        val queue = Volley.newRequestQueue(this)
        val jsonArrayRequest = object : JsonArrayRequest(Request.Method.GET, APIsURL().GET_ORDERS, null, {
            stopLoader()
            ordersFragment.orderJSONArray = it
            setupViewPager()
            setPagerPosition(currentPage)
            Log.e("Orders", "Done")
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
                params["Authorization"] = "Bearer $myToken"
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
                profileTabBTN.setTextColor(getColor(R.color.navaraPrimary))
                isInProfile = true
            }
            1 -> {
                cartTabBTN.setTextColor(getColor(R.color.navaraPrimary))
                isInProfile = false
            }
            2 -> {
                orderTabBTN.setTextColor(getColor(R.color.navaraPrimary))
                isInProfile = false
            }
        }
    }

    private fun setNoInternet() {
        val fragmentTranslate = supportFragmentManager.beginTransaction()
        fragmentTranslate.replace(R.id.loadProfileFL, NoInternetFragment())
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
                    finish()
                }
            }
        } else {
            finish()
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
                    0 -> profileTabBTN.setTextColor(getColor(R.color.navaraPrimary))
                    1 -> cartTabBTN.setTextColor(getColor(R.color.navaraPrimary))
                    2 -> orderTabBTN.setTextColor(getColor(R.color.navaraPrimary))
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
        profileTabBTN.setTextColor(getColor(R.color.blackItem))
        cartTabBTN.setTextColor(getColor(R.color.blackItem))
        orderTabBTN.setTextColor(getColor(R.color.blackItem))
    }

}
