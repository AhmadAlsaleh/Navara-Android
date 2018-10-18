package com.smartlife_solutions.android.navara_store

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.ItemBasicModel
import kotlinx.android.synthetic.main.activity_order_information.*
import org.json.JSONObject

class OrderInformationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var gMap: GoogleMap
    private val loadingFragment = LoadingFragment()
    private var lat: Double = 0.0
    private var lng: Double = 0.0

    override fun onMapReady(p0: GoogleMap?) {
        gMap = p0!!
        gMap.clear()
        gMap.addMarker(MarkerOptions().position(LatLng(lat, lng)))
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), StaticInformation().ZOOM_VAL))
    }

    private fun hideLoad() {
        val ft = supportFragmentManager.beginTransaction()
        ft.remove(loadingFragment)
        ft.commit()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_information)

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.orderInfoFL, loadingFragment)
        ft.commit()

        orderInformationBackIV.setOnClickListener {
            onBackPressed()
        }

        // region font
        val myFont = StaticInformation().myFont(this)
        titleLayoutTV.typeface = myFont
        selectedItemsTV.typeface = myFont
        orderTotalInfoTV.typeface = myFont
        locationTV.typeface = myFont
        locationRemarkTV.typeface = myFont
        selectedDateTimeTV.typeface = myFont
        preferableTimeFromTV.typeface = myFont
        preferableTimeToTV.typeface = myFont
        contactInfoTV.typeface = myFont
        nameTV.typeface = myFont
        selectedNameTV.typeface = myFont
        phoneTV.typeface = myFont
        selectedPhoneTV.typeface = myFont
        remarkTV.typeface = myFont
        selectedRemarkTV.typeface = myFont
        orderCodeTV.typeface = myFont
        orderStatusTV.typeface = myFont
        // endregion

        if (submitMapView != null) {
            submitMapView.onCreate(null)
            submitMapView.onResume()
            submitMapView.getMapAsync(this)
        }

         getInfo(intent.getStringExtra("id_order"))

    }

    private fun getInfo(id: String) {
        val myToken= try {
            DatabaseHelper(this).userModelIntegerRuntimeException.queryForAll()[0].token
        } catch (err: Exception) {
            ""
        }
        Log.e("token", myToken)
        Log.e("id", id)
        val queue = Volley.newRequestQueue(this)
        val request = object : StringRequest(Request.Method.GET, APIsURL().GET_ORDER + id,
                Response.Listener<String> {
                    hideLoad()
                    Log.e("order", it)
                    setupInfo(JSONObject(it))
                    queue.cancelAll("orderid")
                }, Response.ErrorListener {
            Log.e("order error", it.message)
            queue.cancelAll("orderid")
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=UTF-8"
                params["Authorization"] = "Bearer $myToken"
                return params
            }
        }
        request.tag = "orderid"
        queue.add(request)

    }

    @SuppressLint("SetTextI18n")
    private fun setupInfo(it: JSONObject) {
        val items = it.getJSONArray("orderItems")
        for (i in 0 until items.length()) {
            val itemObject = items.getJSONObject(i)
            val item = ItemBasicModel(itemObject.getString("orderItemID"),
                    itemObject.getString("name"),
                    "", "", itemObject.getInt("quantity"),
                    itemObject.getInt("unitNetPrice").toFloat(),
                    itemObject.getString("thumbnailImagePath"))
            if (i == items.length() - 1) {
                chosenItemsLL.addView(SelectedItem(this, item, true).view)
            } else {
                chosenItemsLL.addView(SelectedItem(this, item).view)
            }
        }

        lat = it.getString("location")?.split(",")?.get(0)?.toDouble()!!
        lng = it.getString("location")?.split(",")?.get(1)!!.trim().toDouble()

        try {
            gMap.addMarker(MarkerOptions().position(LatLng(lat, lng)))
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), StaticInformation().ZOOM_VAL))
        } catch (err: Exception) {}

        Log.e("lat", lat.toString())
        Log.e("lng", lng.toString())

        if (it.getString("locationRemark").isEmpty()) {
            locationRemarkTV.visibility = View.GONE
        } else {
            locationRemarkTV.visibility = View.VISIBLE
            locationRemarkTV.text = it.getString("locationRemark")
        }

        preferableTimeFromTV.text = it.getString("fromTime")
        preferableTimeToTV.text = it.getString("toTime")

        selectedNameTV.text = it.getString("name")
        selectedPhoneTV.text = it.getString("mobile")
        selectedRemarkTV.text = it.getString("remark")

        orderTotalInfoTV.text = "Total: "+ StaticInformation().formatPrice(it.getInt("netTotalPrices")) + " S.P"

        val status = it.getString("status")
        if (status.contains("InProgress", true)) {
            orderStatusIV.setImageResource(R.drawable.ic_hourglass)
            orderStatusIV.setBackgroundResource(R.drawable.ic_circle_orange)
        } else {
            orderStatusIV.setImageResource(R.drawable.ic_check_white)
            orderStatusIV.setBackgroundResource(R.drawable.ic_circle_green)
        }
        orderStatusTV.text = status
        orderCodeTV.text = "Code: ${it.getString("code")}"

    }

    private fun checkNum(i: Int): String {
        if (i.toString().length == 1) {
            return "0$i"
        }
        return i.toString()
    }

}
