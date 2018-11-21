package com.smartlife_solutions.android.navara_store

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.ViewPager
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import com.smartlife_solutions.android.navara_store.Adapters.MainPagerAdapter
import com.smartlife_solutions.android.navara_store.Dialogs.*
import kotlinx.android.synthetic.main.activity_main.*
import com.smartlife_solutions.android.navara_store.MainBackFragments.MainBack2
import com.smartlife_solutions.android.navara_store.MainBackFragments.MainBack3
import com.smartlife_solutions.android.navara_store.MainBackFragments.MainBack4
import com.smartlife_solutions.android.navara_store.MainBackFragments.MainBack1
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var allDoneMain: AllDoneDialog
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
        // changeTimer()

        if (Statics.getCurrentLanguageName(this) == Statics.arabic) {
            val conf = resources.configuration
            conf.setLayoutDirection(Locale("fa"))
            resources.updateConfiguration(conf, resources.displayMetrics)
        } else {
            val conf = resources.configuration
            conf.setLayoutDirection(Locale("en"))
            resources.updateConfiguration(conf, resources.displayMetrics)
        }


        allDoneMain = AllDoneDialog(this, false, lang = Statics.getLanguageJSONObject(this))
        isActivityVisible = true
        setFont()

        setHints()

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
        menuAboutUsTV.setOnClickListener(this)

        profileIV.setOnClickListener(this)
        locationIV.setOnClickListener(this)
        itemsIV.setOnClickListener(this)
        offersIV.setOnClickListener(this)
        orderIV.setOnClickListener(this)

        menuFacebookIV.setOnClickListener(this)
        menuInstagramIV.setOnClickListener(this)
        menuTwitterIV.setOnClickListener(this)
        menuWhatsAppIV.setOnClickListener(this)
        // endregion

        try {
            if (intent.getBooleanExtra("confirm", false)) {
                ConfirmAccountDialog(this, intent.getStringExtra("account"), true, lang = Statics.getLanguageJSONObject(this).getJSONObject("dialogs").getJSONObject("confirm")).show()
            }
        } catch (err: Exception) {}

        val showDone = intent.getBooleanExtra("done", false)
        if (showDone) {
            allDoneMain.message = intent.getStringExtra("code")
            allDoneMain.show()
        }

//        Log.e("lang", Statics.getLanguageJSONObject(this).toString())
        setTranslateTexts()

    }

    @SuppressLint("RtlHardcoded")
    private fun setTranslateTexts() {
        val mainTranslate = Statics.getLanguageJSONObject(this).getJSONObject("mainActivity")
        menuProfileTV.text = mainTranslate.getString("profile")
        menuAccountTV.text = mainTranslate.getString("account")
        menuMyCartTV.text = mainTranslate.getString("myCart")
        menuMyOrdersTV.text = mainTranslate.getString("myOrder")
        menuAppSectionsTV.text = mainTranslate.getString("appSection")
        menuOurItemsTV.text = mainTranslate.getString("ourItems")
        menuLastOffersTV.text = mainTranslate.getString("latestOffers")
        menuLocationsTV.text = mainTranslate.getString("locationAndContact")
        menuOthersTV.text = mainTranslate.getString("others")
        menuChangeLanguageTV.text = mainTranslate.getString("changeLanguage")
        menuTermsTV.text = mainTranslate.getString("termsOfUse")
        menuAboutUsTV.text = mainTranslate.getString("aboutUs")
        if (Statics.getCurrentLanguageName(this) == Statics.arabic) {
            menu1LL.gravity = Gravity.RIGHT
            menu2LL.gravity = Gravity.RIGHT
            menu3LL.gravity = Gravity.RIGHT
        } else {
            menu1LL.gravity = Gravity.LEFT
            menu2LL.gravity = Gravity.LEFT
            menu3LL.gravity = Gravity.LEFT
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setHints() {
        val perf = getSharedPreferences("Navara", Context.MODE_PRIVATE)
        val isFirstMain = perf.getBoolean("main", true)
        if (isFirstMain) {
            val editShared = perf.edit()
            editShared.putBoolean("main", false)
            editShared.apply()
            mainHintRL.visibility = View.VISIBLE
        } else {
            mainHintRL.visibility = View.GONE
        }

        val font = StaticInformation().myFont(this)
        mainHintTV.typeface = font
        mainNextBTN.typeface = font

        val hintsLang = Statics.getLanguageJSONObject(this).getJSONObject("hints").getJSONObject("mainActivity")

        var currentHint = 0
        mainNextBTN.text = hintsLang.getString("start")
        mainHintTV.text = hintsLang.getString("welcome")
        mainNextBTN.setOnClickListener {
            hideAllHint()
            mainNextBTN.text = hintsLang.getString("next")
            when (currentHint) {
                0 -> setHint(hintsLang.getString("locationHint"), mainHintLocationIV)
                1 -> setHint(hintsLang.getString("itemsHint"), mainHintItemsIV)
                2 -> setHint(hintsLang.getString("offersHint"), mainHintOffersIV)
                3 -> setHint(hintsLang.getString("profileHint"), mainHintProfileIV)
                4 -> {
                    mainHintTV.text = hintsLang.getString("orderHint")
                    mainHintOrderRL.visibility = View.GONE
                }
                5 -> {
                    mainNextBTN.text = hintsLang.getString("done")
                    mainHintTV.text = hintsLang.getString("settings")
                    mainHintMenuIV.visibility = View.VISIBLE
                    mainHintOrderRL.visibility = View.VISIBLE
                }
                6 -> mainHintRL.visibility = View.GONE
            }
            currentHint++
            mainHintTV.startAnimation(StaticInformation().fadeInAnim(this))
        }

    }

    private fun setHint(text: String, view: View) {
        mainHintTV.text = text
        view.startAnimation(StaticInformation().fadeInAnim(this))
        view.visibility = View.VISIBLE
    }

    private fun hideAllHint() {
        mainHintLocationIV.visibility = View.GONE
        mainHintItemsIV.visibility = View.GONE
        mainHintOffersIV.visibility = View.GONE
        mainHintProfileIV.visibility = View.GONE
        mainHintMenuIV.visibility = View.GONE
    }

    override fun onClick(v: View?) {
        val viewId = v?.id
        v?.startAnimation(StaticInformation().clickAnim(this))
        when(viewId) {
            R.id.settingsIV -> slideDownSettings()
            R.id.settingsMenuLL -> onBackPressed()

        // region profile
            R.id.menuAccountTV -> {
                if (Statics.myToken.isNotEmpty()) {
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
                if (Statics.myToken.isNotEmpty()) {
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
                if (Statics.myToken.isNotEmpty()) {
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
            R.id.menuOurItemsTV -> {
                finish()
                startActivity(Intent(this, ItemsActivity::class.java))
            }
            R.id.menuLastOffersTV -> {
                finish()
                startActivity(Intent(this, OffersActivity::class.java))
            }
            R.id.menuLocationsTV -> {
                finish()
                startActivity(Intent(this, LocationActivity::class.java))
            }
        // endregion

        // region others
            R.id.menuChangeLanguageTV -> ChangeLanguageDialog(this,
                    Statics.getCurrentLanguageName(this),
                    Statics.getLanguageJSONObject(this).getJSONObject("dialogs").getJSONObject("changeLanguage"),
                    this, fromMain = true).show()

            R.id.menuTermsTV -> startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse(StaticInformation().termsOfUseLink())))
            R.id.menuAboutUsTV -> {
                onBackPressed()
                StaticInformation().dialogXY(AboutUsDialog(this, Statics.getLanguageJSONObject(this).getJSONObject("dialogs").getJSONObject("aboutUs")))
            }
        // endregion

        // region social media
            R.id.menuFacebookIV -> startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse(StaticInformation().facebookLink())))
            R.id.menuInstagramIV -> startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse(StaticInformation().instagramLink())))
            R.id.menuTwitterIV -> startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse(StaticInformation().twitterLink())))
            R.id.menuWhatsAppIV -> StaticInformation().openWhatsApp(this)
        // endregion

        // region action bar
            R.id.profileIV -> {
                if (Statics.myToken.isNotEmpty()) {
                    startActivity(
                            Intent(this, ProfileCartOrders::class.java)
                                    .putExtra("currentPage", 0)
                    )
                } else {
                    finish()
                    startActivity(Intent(this, LoginRegisterActivity::class.java))
                }
            }
            R.id.locationIV -> {
                finish()
                startActivity(Intent(this, LocationActivity::class.java))
            }
            R.id.itemsIV -> {
                finish()
                startActivity(Intent(this, ItemsActivity::class.java))
            }
            R.id.offersIV -> {
                finish()
                startActivity(Intent(this, OffersActivity::class.java))
            }
            R.id.orderIV -> {
                finish()
                try {
                    if (Statics.myToken.isNotEmpty()) {
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
        val sureExit = SureToDoDialog(this, Statics.getLanguageJSONObject(this).getJSONObject("dialogs").getJSONObject("sure").getString("exit"))
        sureExit.show()
        sureExit.setOnDismissListener {
            if (sureExit.isTrue) {
                finish()
            }
        }
    }

    private fun setupViewPagerBack() {
        val adapter = MainPagerAdapter(supportFragmentManager)
        if (Statics.getCurrentLanguageName(this) == Statics.english) {
            adapter.addFragment(MainBack1(), "One")
            adapter.addFragment(MainBack2(), "Tow")
            adapter.addFragment(MainBack3(), "Three")
            adapter.addFragment(MainBack4(), "Four")
        } else {
            mainViewPager.rotationY = 180F
            adapter.addFragment(MainBack1(true), "One")
            adapter.addFragment(MainBack2(true), "Tow")
            adapter.addFragment(MainBack3(true), "Three")
            adapter.addFragment(MainBack4(true), "Four")
        }

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
                // changePager(mainViewPager.currentItem)
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
            // changeTimer()
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
        }, 750)
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
        }, 750)
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

        menuAboutUsTV.typeface = myFont
    }

}
