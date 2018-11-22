package com.smartlife_solutions.android.navara_store

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.text.Html
import android.util.Log
import android.view.View
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.Dialogs.ContactUsDialog
import kotlinx.android.synthetic.main.activity_location.*
import org.json.JSONObject
import java.util.*

class LocationActivity : AppCompatActivity() {

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var googleMap: GoogleMap

    private val SUPPORT_MOBILE = "+963943877890"
    private val SUPPORT_EMAIL = "contact@navarastore.com"
    private val companyPlace = LatLng(35.521665, 35.774847)
    private var nameString = ""
    private var emailString = ""

    private lateinit var lang: JSONObject
    private lateinit var langC: JSONObject

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        lang = Statics.getLanguageJSONObject(this)
        langC = lang.getJSONObject("locationActivity")

        if (Statics.getCurrentLanguageName(this) == Statics.arabic) {
            val conf = resources.configuration
            conf.setLayoutDirection(Locale("fa"))
            resources.updateConfiguration(conf, resources.displayMetrics)
        } else {
            val conf = resources.configuration
            conf.setLayoutDirection(Locale("en"))
            resources.updateConfiguration(conf, resources.displayMetrics)
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        addressTV.text = langC.getString("addressLine")

        mapFragment = supportFragmentManager.findFragmentById(R.id.locationMapFragment) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap = it
            googleMap.addMarker(MarkerOptions().position(companyPlace).title("Navara Store")
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(companyPlace, 16F))
        }

        locationBackIV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(this))
            onBackPressed()
        } // back button

        contactUsFAB.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(this))
            toggleHideMore()
        } // show more

        hiddenRL.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(this))
            toggleHideMore()
        } // hide more

        directionsFAB.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(this))
            if (hiddenRL.visibility == View.GONE) {
                val uri = "http://maps.google.com/maps?q=loc:" + companyPlace.latitude.toString() + "," + companyPlace.longitude.toString() + " (Navara Store)"
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
            } else {
                toggleHideMore()
            }
        } // directions

        webContactFAB.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(this))
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(StaticInformation().navaraWebSite())))
        } // show web app

        callContactFAB.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(this))
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$SUPPORT_MOBILE"))
            startActivity(callIntent)
        } // call

        contactMailFAB.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(this))
            ContactUsDialog(this, nameString, emailString, Statics.getLanguageJSONObject(this).getJSONObject("dialogs").getJSONObject("contactUS")).show()
        }

        goToFAB.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(this))
            if (hiddenRL.visibility == View.GONE) {
                try {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(companyPlace, 16F))
                } catch (err: Exception) {}
            } else {
                toggleHideMore()
            }
        }

        // region set font
        val myFont = StaticInformation().myFont(this)
        titleLayoutTV.typeface = myFont
        titleLayoutTV.text = langC.getString("title")
        addressText.typeface = myFont
        addressText.text = langC.getString("address")
        addressTV.typeface = myFont
        workTimeText.typeface = myFont
        workTimeText.text = langC.getString("workingHours")
        workTimeTV.typeface = myFont
        workTimeTV.text = langC.getString("workingHoursLine")
        sendTV.typeface = myFont
        sendTV.text = langC.getString("contactUs").replace(" ", "\n", true)
        callTV.typeface = myFont
        callTV.text = langC.getString("makeCall").replace(" ", "\n", true)
        webTV.typeface = myFont
        webTV.text = langC.getString("visitWebsite").replace(" ", "\n", true)
        // endregion

        getUserInfo()
    }

    private fun getUserInfo() {

        val queue = Volley.newRequestQueue(this)
        val request = object : JsonObjectRequest(Request.Method.GET, APIsURL().GET_USER_INFORMATION, null, {
            try {
                Log.e("profile", "Done")
                Log.e("profile", it.toString())
                nameString = it.getString("name")
                emailString = if (it.getString("email") == "null") {
                    ""
                } else {
                    it.getString("email")
                }
            } catch (err: Exception) {}
            queue.cancelAll("info")
        }, {
            try {
                Log.e("error", "error")
            } catch (err: Exception) {}
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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun toggleHideMore() {
        if (hiddenRL.visibility == View.GONE) {
            hiddenRL.visibility = View.VISIBLE
        } else {
            hiddenRL.visibility = View.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBackPressed() {
        try {
            if (hiddenRL.visibility == View.VISIBLE) {
                hiddenRL.visibility = View.GONE
                return
            }
            startActivity(Intent(this, MainActivity::class.java))
        } catch (err: Exception) {}
        super.onBackPressed()
    }
}
