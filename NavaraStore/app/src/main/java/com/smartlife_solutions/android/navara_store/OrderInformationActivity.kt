package com.smartlife_solutions.android.navara_store

import android.annotation.SuppressLint
import android.content.Intent
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
import java.util.*

class OrderInformationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var gMap: GoogleMap
    private val loadingFragment = LoadingFragment()
    private var lat: Double = 0.0
    private var lng: Double = 0.0

    private lateinit var lang: JSONObject
    private lateinit var langC: JSONObject

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

        lang = Statics.getLanguageJSONObject(this)
        langC = lang.getJSONObject("OrderPreviewActivity")

        if (Statics.getCurrentLanguageName(this) == Statics.arabic) {
            val conf = resources.configuration
            conf.setLayoutDirection(Locale("fa"))
            resources.updateConfiguration(conf, resources.displayMetrics)
        } else {
            val conf = resources.configuration
            conf.setLayoutDirection(Locale("en"))
            resources.updateConfiguration(conf, resources.displayMetrics)
        }

        if (!StaticInformation().isConnected(this)) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.orderInfoFL, NoInternetFragment(Statics.getLanguageJSONObject(this).getString("noConnection")))
            ft.commit()
            return
        }

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.orderInfoFL, loadingFragment)
        ft.commit()

        orderInformationBackIV.setOnClickListener {
            onBackPressed()
        }

        orderContactUsIV.setOnClickListener {
            val s = StaticInformation()
            s.clickAnim(this)
            s.openWhatsApp(this, "Order Code: ${orderCodeTV.text}\n${orderStatusTV.text}")
        }

        // region font
        val myFont = StaticInformation().myFont(this)
        titleLayoutTV.typeface = myFont
        titleLayoutTV.text = langC.getString("orderInfo")
        selectedItemsTV.typeface = myFont
        selectedItemsTV.text = langC.getString("chosenItems")
        orderTotalInfoTV.typeface = myFont
        locationTV.typeface = myFont
        locationTV.text = langC.getString("location")
        locationRemarkTV.typeface = myFont
        selectedDateTimeTV.typeface = myFont
        selectedDateTimeTV.text = langC.getString("preferableTime")
        preferableTimeFromTV.typeface = myFont
        preferableTimeToTV.typeface = myFont

        contactInfoTV.typeface = myFont
        contactInfoTV.text = langC.getString("contactInfo")
        nameTV.typeface = myFont
        nameTV.text = langC.getString("name")
        selectedNameTV.typeface = myFont
        phoneTV.typeface = myFont
        phoneTV.text = langC.getString("phoneNumber")
        selectedPhoneTV.typeface = myFont
        remarkTV.typeface = myFont
        remarkTV.text = langC.getString("remark")
        selectedRemarkTV.typeface = myFont
        orderCodeTV.typeface = myFont
        orderStatusTV.typeface = myFont
        orderUseWalletTV.typeface = myFont
        orderDaysToDeliverTV.typeface = myFont
        // endregion

        if (submitMapView != null) {
            submitMapView.onCreate(null)
            submitMapView.onResume()
            submitMapView.getMapAsync(this)
        }

        getInfo(intent.getStringExtra("id_order"))

    }

    private fun getInfo(id: String) {

        Log.e("token", Statics.myToken)
        Log.e("id", id)
        val queue = Volley.newRequestQueue(this)
        val request = object : StringRequest(Request.Method.GET, APIsURL().GET_ORDER + id,
                Response.Listener<String> {
                    hideLoad()
                    Log.e("order", it)
                    setupInfo(JSONObject(it))
                    queue.cancelAll("orderid")
                }, Response.ErrorListener {
            try {
                Log.e("order error", it.message)
            } catch (err: Exception) {}
            queue.cancelAll("orderid")
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=UTF-8"
                params["Authorization"] = "Bearer ${Statics.myToken}"
                return params
            }
        }
        request.tag = "orderid"
        queue.add(request)

    }

    @SuppressLint("SetTextI18n")
    private fun setupInfo(it: JSONObject) {
        val items = it.getJSONArray("orderItems")
        var itemsArrayList = ArrayList<ItemBasicModel>()
        for (i in 0 until items.length()) {
            val itemObject = items.getJSONObject(i)
            itemsArrayList.add(ItemBasicModel(itemObject.getString("orderItemID"),
                    itemObject.getString("name"),
                    "", "", itemObject.getInt("quantity"),
                    itemObject.getInt("unitNetPrice").toFloat(),
                    itemObject.getString("thumbnailImagePath"), "", "", 0))
        }

        if (itemsArrayList.size > 1) {
            itemsArrayList = itemsArrayList.sortedWith(compareBy({ it.offerID }, { it.price }))
                    .reversed() as ArrayList<ItemBasicModel>
        }
        for (i in 0 until itemsArrayList.size) {
            if (i == itemsArrayList.size - 1) {
                chosenItemsLL.addView(SelectedItem(this, itemsArrayList[i], true, lang).view)
            } else {
                chosenItemsLL.addView(SelectedItem(this, itemsArrayList[i], lang = lang).view)
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

        orderTotalInfoTV.text = "Total: "+ StaticInformation().formatPrice(it.getInt("netTotalPrices")) + " ${lang.getString("currencyCode")}"
        if (it.getBoolean("useWallet")) {
            orderUseWalletTV.visibility = View.VISIBLE
            try {
                orderUseWalletTV.text = "(${langC.getString("fromWallet")} ${StaticInformation().formatPrice(
                        it.get("walletAmount").toString().toFloat().toInt()
                )} ${lang.getString("currencyCode")})"
            } catch (err: Exception) {}
        } else {
            orderUseWalletTV.visibility = View.GONE
        }

        val status = it.getString("status")
        when {
            status.contains("Canceled", true) -> {
                orderStatusIV.setImageResource(R.drawable.ic_close_white)
                orderStatusIV.setBackgroundResource(R.drawable.ic_circle_red)
                orderDaysToDeliverLL.visibility = View.GONE
            }
            status.contains("Delivered", true) -> {
                orderStatusIV.setImageResource(R.drawable.ic_check_white)
                orderStatusIV.setBackgroundResource(R.drawable.ic_circle_green)
                orderDaysToDeliverLL.visibility = View.GONE
            }
            else -> {
                orderStatusIV.setImageResource(R.drawable.ic_hourglass)
                orderStatusIV.setBackgroundResource(R.drawable.ic_circle_orange)
                orderDaysToDeliverLL.visibility = View.VISIBLE
            }
        }
        if (Statics.getCurrentLanguageName(this) == Statics.arabic) {
            when {
                status.contains("Canceled", true) -> {
                    orderStatusTV.text = "طلب ملغى"
                }
                status.contains("Delivered", true) -> {
                    orderStatusTV.text = "تم التوصيل"
                }
                else -> {
                    orderStatusTV.text = "يتم العمل"
                }
            }
        } else {
            orderStatusTV.text = status
        }
        orderCodeTV.text = "${langC.getString("code")} ${it.getString("code")}"

        var days = it.get("daysToDeliver").toString()
        days = try {
            when {
                days.toFloat().toInt() == 0 -> langC.getString("tomorrow")
                days.toFloat().toInt() == 1 -> "${langC.getString("during")} ${days.toFloat().toInt()} ${langC.getString("day")}"
                else -> "${langC.getString("during")} ${days.toFloat().toInt()} ${langC.getString("days")}"
            }
        } catch (e: Exception) {
            langC.getString("tomorrow")
        }

        orderDaysToDeliverTV.text = days

    }

    private fun checkNum(i: Int): String {
        if (i.toString().length == 1) {
            return "0$i"
        }
        return i.toString()
    }

    override fun onBackPressed() {
        if (intent.getBooleanExtra(Statics.fromNotification, false)) {
            startActivity(Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
        super.onBackPressed()
    }
}
