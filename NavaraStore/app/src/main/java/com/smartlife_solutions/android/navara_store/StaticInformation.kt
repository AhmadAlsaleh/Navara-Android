package com.smartlife_solutions.android.navara_store

import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.Models.ExceptionModel
import java.io.ByteArrayOutputStream
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.text.NumberFormat
import android.util.Base64
import android.view.WindowManager
import android.widget.Toast
import java.util.*

class StaticInformation {

    fun smartlifeWebSite() = "http://www.smartlife-solutions.com/"
    fun navaraWebSite() = "http://www.navarastore.com/"
    fun termsOfUseLink() = navaraWebSite() + "termsOfUse"
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

    fun openWhatsApp(activity: Activity, message: String = "") {
        val whatsAppNumber = "+963967926970"

        activity.startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("https://api.whatsapp.com/send?phone=$whatsAppNumber&text=$message\n")))
    }

    fun copyToClip(activity: Activity, label: String, text: String): Boolean {
        val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.primaryClip = ClipData.newPlainText(label, text)
        return true
    }

    fun dialogXY(dialog: Dialog) {
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.show()
        dialog.window!!.attributes = lp
    }

    fun getResizedBitmap(bm: Bitmap, max: Int): Bitmap {
        try {
            var width = bm.width
            var height = bm.height

            Log.v("Pictures", "Width and height are $width--$height")

            when {
                width > height -> {
                    // landscape
                    val ratio = (width / max).toFloat()
                    width = max;
                    height = (height / ratio).toInt()
                }
                height > width -> {
                    // portrait
                    val ratio = (height / max).toFloat()
                    height = max
                    width = (width / ratio).toInt()
                }
                else -> {
                    // square
                    height = max
                    width = max
                }
            }
            Log.v("Pictures", "after scaling Width and height are $width--$height")
            return try {
                Bitmap.createScaledBitmap(bm, width, height, true)
            } catch (err: OutOfMemoryError) {
                bm
            }
        } catch (er: Exception) {
            return bm
        }
    }

    fun imageToString(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    fun sendExpentionReport(context: Context, exception: ExceptionModel) {
        val queue = Volley.newRequestQueue(context)
        val request = object : StringRequest(Request.Method.POST, APIsURL().EXCEPTION_URL, {
            queue.cancelAll("exception")
        }, {
            queue.cancelAll("exception")
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray? {
                return try {
                    Gson().toJson(exception, ExceptionModel::class.java)
                            .toByteArray(Charset.forName("utf-8"))
                } catch (err: UnsupportedEncodingException) {
                    Log.e("exception error", err.toString())
                    null
                }
            }

            override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
                var responseString = ""
                if (response != null) {
                    responseString = response.statusCode.toString()
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response))
            }
        }
        request.tag = "exception"
        queue.add(request)
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

    fun fadeInAnim(context: Context?): Animation? {
        return AnimationUtils.loadAnimation(context, R.anim.fade_in)
    }

    fun slideHint(context: Context?): Animation? {
        return AnimationUtils.loadAnimation(context, R.anim.slide_down_hint)
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