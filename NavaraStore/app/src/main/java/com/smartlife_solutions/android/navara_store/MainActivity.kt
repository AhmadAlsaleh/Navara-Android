package com.smartlife_solutions.android.navara_store

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.smartlife_solutions.android.navara_store.Adapters.MainPagerAdapter
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.Dialogs.AllDoneDialog
import com.smartlife_solutions.android.navara_store.Dialogs.ChangeLanguageDialog
import com.smartlife_solutions.android.navara_store.Dialogs.SureToDoDialog
import kotlinx.android.synthetic.main.activity_main.*
import com.smartlife_solutions.android.navara_store.MainBackFragments.MainBack2
import com.smartlife_solutions.android.navara_store.MainBackFragments.MainBack3
import com.smartlife_solutions.android.navara_store.MainBackFragments.MainBack4
import com.android.volley.toolbox.Volley
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.UserModel
import com.smartlife_solutions.android.navara_store.Dialogs.ConfirmAccountDialog
import com.smartlife_solutions.android.navara_store.MainBackFragments.MainBack1


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var allDoneMain: AllDoneDialog
    private var myToken: String = ""
    private var isActivityVisible = false

    override fun onResume() {
        super.onResume()
        isActivityVisible = true
    }

    override fun onPause() {
        super.onPause()
        isActivityVisible = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViewPagerBack()
        changeTimer()
        mainViewPager.currentItem = 0
        allDoneMain = AllDoneDialog(this, false)
        isActivityVisible = true
        setFont()

        checkLogin()

        // region on click listeners
        settingsIV.setOnClickListener(this)
        settingsMenuLL.setOnClickListener(this)

        menuAccountTV.setOnClickListener(this)
        menuMyCartTV.setOnClickListener(this)
        menuMyOrdersTV.setOnClickListener(this)

        menuOurItemsTV.setOnClickListener(this)
        menuLastOffersTV.setOnClickListener(this)
        menuLocationsTV.setOnClickListener(this)

        menuChangeLanguageTV.setOnClickListener(this)
        menuTermsTV.setOnClickListener(this)
        menuLogoutTV.setOnClickListener(this)

        profileIV.setOnClickListener(this)
        locationIV.setOnClickListener(this)
        itemsIV.setOnClickListener(this)
        offersIV.setOnClickListener(this)
        orderIV.setOnClickListener(this)

        menuFacebookIV.setOnClickListener(this)
        menuInstagramIV.setOnClickListener(this)
        menuTwitterIV.setOnClickListener(this)
        // endregion

        try {
            if (intent.getBooleanExtra("confirm", false)) {
                ConfirmAccountDialog(this, intent.getStringExtra("account"), true).show()
            }
        } catch (err: Exception) {}


        val showDone = intent.getBooleanExtra("done", false)
        if (showDone) {
            allDoneMain.message = intent.getStringExtra("code")
            allDoneMain.show()
        }
    }

    override fun onClick(v: View?) {
        val viewId = v?.id
        v?.startAnimation(StaticInformation().clickAnim(this))
        when(viewId) {
            R.id.settingsIV -> slideDownSettings()
            R.id.settingsMenuLL -> onBackPressed()

        // region profile
            R.id.menuAccountTV -> {
                if (myToken.isNotEmpty()) {
                    startActivity(
                            Intent(this, ProfileCartOrders::class.java)
                                    .putExtra("currentPage", 0)
                    )
                } else {
                    finish()
                    startActivity(Intent(this, LoginRegisterActivity::class.java))
                }
            }
            R.id.menuMyCartTV -> {
                if (myToken.isNotEmpty()) {
                    startActivity(
                            Intent(this, ProfileCartOrders::class.java)
                                    .putExtra("currentPage", 1)
                                    .putExtra(StaticInformation().FINITSH_ON_BACK, true)
                    )
                } else {
                    finish()
                    startActivity(Intent(this, LoginRegisterActivity::class.java))
                }
            }
            R.id.menuMyOrdersTV -> {
                if (myToken.isNotEmpty()) {
                    startActivity(
                            Intent(this, ProfileCartOrders::class.java)
                                    .putExtra("currentPage", 2)
                                    .putExtra(StaticInformation().FINITSH_ON_BACK, true)
                    )
                } else {
                    finish()
                    startActivity(Intent(this, LoginRegisterActivity::class.java))
                }
            }
        // endregion

        // region app sections
            R.id.menuOurItemsTV -> startActivity(Intent(this, ItemsActivity::class.java))
            R.id.menuLastOffersTV -> startActivity(Intent(this, OffersActivity::class.java))
            R.id.menuLocationsTV -> startActivity(Intent(this, LocationActivity::class.java))
        // endregion

        // region others
            R.id.menuChangeLanguageTV -> ChangeLanguageDialog(this).show()
            R.id.menuTermsTV -> startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse(StaticInformation().termsOfUseLink())))
            R.id.menuLogoutTV -> logout()
        // endregion

        // region social media
            R.id.menuFacebookIV -> startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse(StaticInformation().facebookLink())))
            R.id.menuInstagramIV -> startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse(StaticInformation().instagramLink())))
            R.id.menuTwitterIV -> startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse(StaticInformation().twitterLink())))
        // endregion

        // region action bar
            R.id.profileIV -> {
                if (myToken.isNotEmpty()) {
                    startActivity(
                            Intent(this, ProfileCartOrders::class.java)
                                    .putExtra("currentPage", 0)
                    )
                } else {
                    finish()
                    startActivity(Intent(this, LoginRegisterActivity::class.java))
                }
            }
            R.id.locationIV -> startActivity(Intent(this, LocationActivity::class.java))
            R.id.itemsIV -> startActivity(Intent(this, ItemsActivity::class.java))
            R.id.offersIV -> startActivity(Intent(this, OffersActivity::class.java))
            R.id.orderIV -> {
                finish()
                try {
                    if (DatabaseHelper(this).userModelIntegerRuntimeException.queryForAll()[0].token.isNotEmpty()) {
                        startActivity(Intent(this, OrdersActivity::class.java).putExtra("done", true))
                    } else {
                        startActivity(Intent(this, LoginRegisterActivity::class.java))
                    }
                } catch (err: Exception) {
                    startActivity(Intent(this, LoginRegisterActivity::class.java))
                }
            }
        // endregion
        }
    }

    override fun onBackPressed() {
        if (settingsMenuLL.visibility == View.VISIBLE) {
            slideUpSettings()
            return
        }
        val sureExit = SureToDoDialog(this, "Sure to exit?")
        sureExit.show()
        sureExit.setOnDismissListener {
            if (sureExit.isTrue) {
                finish()
            }
        }
    }

    private fun setupViewPagerBack() {
        val adapter = MainPagerAdapter(supportFragmentManager)
        adapter.addFragment(MainBack1(), "One")
        adapter.addFragment(MainBack2(), "Tow")
        adapter.addFragment(MainBack3(), "Three")
        adapter.addFragment(MainBack4(), "Four")
        mainViewPager.adapter = adapter
        mainViewPager?.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(p0: Int) {}

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

            override fun onPageSelected(p0: Int) {
                setPointsWhite()
                when (p0) {
                    0 -> point1.setBackgroundResource(R.drawable.primary_button_background)
                    1 -> point2.setBackgroundResource(R.drawable.primary_button_background)
                    2 -> point3.setBackgroundResource(R.drawable.primary_button_background)
                    3 -> point4.setBackgroundResource(R.drawable.primary_button_background)
                }
            }
        })
    }

    private fun changeTimer() {
        Handler().postDelayed({
            if (isActivityVisible) {
                changePager(mainViewPager.currentItem)
            }
        }, 5000)
    }

    private fun changePager(currentPage: Int) {
        try {
            if (currentPage == 3) {
                mainViewPager.currentItem = 0
            } else {
                mainViewPager.currentItem = currentPage + 1
            }
            changeTimer()
        } catch (err: Exception) {}
    }

    private fun setPointsWhite() {
        point1.setBackgroundResource(R.drawable.white_button_background)
        point2.setBackgroundResource(R.drawable.white_button_background)
        point3.setBackgroundResource(R.drawable.white_button_background)
        point4.setBackgroundResource(R.drawable.white_button_background)
    }

    @SuppressLint("PrivateResource")
    private fun slideDownSettings() {
        mainFL.isClickable = true
        mainFL.isFocusable = true
        menuSettingsFL.setBackgroundResource(R.color.hide_black)
        settingsMenuLL.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.slide_down))
        settingsMenuLL.visibility = View.VISIBLE
        Handler().postDelayed({
            mainFL.isClickable = false
            mainFL.isFocusable = false
        }, 1000)
    }

    @SuppressLint("PrivateResource")
    private fun slideUpSettings() {
        mainFL.isClickable = true
        mainFL.isFocusable = true
        settingsMenuLL.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up))
        settingsMenuLL.visibility = View.GONE
        Handler().postDelayed({
            mainFL.isClickable = false
            mainFL.isFocusable = false
            menuSettingsFL.setBackgroundResource(android.R.color.transparent)
        }, 1000)
    }

    private fun setFont() {
        val myFont = StaticInformation().myFont(this)
        menuProfileTV.typeface = myFont
        menuAccountTV.typeface = myFont
        menuMyCartTV.typeface = myFont
        menuMyOrdersTV.typeface = myFont

        menuAppSectionsTV.typeface = myFont
        menuOurItemsTV.typeface = myFont
        menuLastOffersTV.typeface = myFont
        menuLocationsTV.typeface = myFont

        menuOthersTV.typeface = myFont
        menuChangeLanguageTV.typeface = myFont
        menuTermsTV.typeface = myFont

        menuLogoutTV.typeface = myFont
    }

    private fun checkLogin() {
        myToken = try {
            DatabaseHelper(this).userModelIntegerRuntimeException.queryForAll()[0].token
        } catch (err: Exception) {
            ""
        }
        if (myToken.isNotEmpty()) {
            menuLogoutTV.visibility = View.VISIBLE
        } else {
            menuLogoutTV.visibility = View.GONE
        }
    }

    private fun logout() {
        if (!StaticInformation().isConnected(this)) {
            Toast.makeText(this, "No Internet Connection, Please Try Again", Toast.LENGTH_SHORT).show()
            return
        }
        val loading = LoadingFragment()
        var fragmentTranslate = supportFragmentManager.beginTransaction()
        fragmentTranslate.replace(R.id.mainLoading, loading)
        fragmentTranslate.commit()

        myToken = try {
            DatabaseHelper(this).userModelIntegerRuntimeException.queryForAll()[0].token
        } catch (err: Exception) {
            ""
        }
        val queue = Volley.newRequestQueue(this)
        val request = object : StringRequest(Request.Method.GET, APIsURL().LOGOUT_URL,
                Response.Listener<String> {
                    queue.cancelAll("logout")
                    Log.e("logout", "True")
                    DatabaseHelper(this).clearTable(UserModel::class.java)
                    checkLogin()
                    settingsMenuLL.visibility = View.GONE
                    mainFL.isClickable = false
                    mainFL.isFocusable = false
                    menuSettingsFL.setBackgroundResource(android.R.color.transparent)
                    fragmentTranslate = supportFragmentManager.beginTransaction()
                    fragmentTranslate.remove(loading)
                    fragmentTranslate.commit()
        }, Response.ErrorListener {
            try {
                fragmentTranslate = supportFragmentManager.beginTransaction()
                fragmentTranslate.remove(loading)
                fragmentTranslate.commit()
                queue.cancelAll("logout")
            } catch (err: Exception) {}
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=UTF-8"
                params["token"] = myToken
                return params
            }
        }
        request.tag = "logout"
        queue.add(request)

    }

}
