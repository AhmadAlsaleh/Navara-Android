package com.smartlife_solutions.android.navara_store

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.text.Html
import android.view.View
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.smartlife_solutions.android.navara_store.Dialogs.ContactUsDialog
import kotlinx.android.synthetic.main.activity_location.*

class LocationActivity : AppCompatActivity() {

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var googleMap: GoogleMap

    private val SUPPORT_MOBILE = "0943877890"
    private val SUPPORT_EMAIL = "contact@navarastore.com"
    private val WEB_URL = "http://www.navarastore.com"
    private val companyPlace = LatLng(35.521665, 35.774847)

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        addressTV.text = Html.fromHtml(getString(R.string.address_line))

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
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(WEB_URL)))
        } // show web app

        callContactFAB.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(this))
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$SUPPORT_MOBILE"))
            startActivity(callIntent)
        } // call

        contactMailFAB.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(this))
            ContactUsDialog(this).show()
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
        addressText.typeface = myFont
        addressTV.typeface = myFont
        workTimeText.typeface = myFont
        workTimeTV.typeface = myFont
        sendTV.typeface = myFont
        callTV.typeface = myFont
        webTV.typeface = myFont
        // endregion
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun toggleHideMore() {
        if (hiddenRL.visibility == View.GONE) {
            hiddenRL.visibility = View.VISIBLE
            directionsFAB.foreground = getDrawable(R.drawable.ic_circle_white_hide)
            goToFAB.foreground = getDrawable(R.drawable.ic_circle_white_hide)
        } else {
            hiddenRL.visibility = View.GONE
            directionsFAB.foreground = null
            goToFAB.foreground = null
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBackPressed() {
        if (hiddenRL.visibility == View.VISIBLE) {
            hiddenRL.visibility = View.GONE
            directionsFAB.foreground = null
            goToFAB.foreground = null
            return
        }
        super.onBackPressed()
    }
}
