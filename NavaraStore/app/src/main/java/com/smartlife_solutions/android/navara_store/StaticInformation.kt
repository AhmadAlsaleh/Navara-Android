package com.smartlife_solutions.android.navara_store

import android.app.Activity
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.support.v4.content.ContextCompat.getSystemService
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import java.text.NumberFormat
import java.util.*

class StaticInformation {

    fun termsOfUseLink() = "http://www.navarastore.com/termsOfUse"
    fun facebookLink() = "https://www.facebook.com/groups/NavaraStore"
    fun instagramLink() = "https://www.instagram.com/navara.store"
    fun twitterLink() = "https://www.twitter.com/Navara_Store"

    val ZOOM_VAL = 16F
    val FINITSH_ON_BACK = "finishOnBack"
    val CHECK_INTERNET: Long = 2000

    companion object {
        @JvmStatic var clickResendTimer: Int = 0
    }

    init {
        clickResendTimer = 0
    }

    fun formatPrice(price: Int): String? {
        return NumberFormat.getNumberInstance(Locale.US).format(price)
    }

    fun isConnected(activity: Activity): Boolean {
        return (activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo != null
    }

    fun myFont(context: Context?): Typeface? {
        val am: AssetManager = context?.applicationContext!!.assets
        return Typeface.createFromAsset(am, String.format(Locale.US, "fonts/%s", "primary_font.otf"))
    }

    fun isEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPhone(phone: String): Boolean {
        return android.util.Patterns.PHONE.matcher(phone).matches()
    }

    fun clickAnim(context: Context?): Animation? {
        return AnimationUtils.loadAnimation(context, R.anim.click_anim)
    }

    fun fadeOutAnim(context: Context?): Animation? {
        return AnimationUtils.loadAnimation(context, R.anim.fade_out)
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}