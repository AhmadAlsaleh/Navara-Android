package com.smartlife_solutions.android.navara_store.OrderFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.MarkerOptions
import com.smartlife_solutions.android.navara_store.*
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.MainActivity
import com.smartlife_solutions.android.navara_store.OrdersActivity
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.SelectedItem
import com.smartlife_solutions.android.navara_store.StaticInformation
import org.json.JSONArray
import org.json.JSONObject

@SuppressLint("ValidFragment")
class OrderSummaryFragment(var activity: OrdersActivity) : Fragment(), OnMapReadyCallback {

    lateinit var name: TextView
    lateinit var phone: TextView
    lateinit var remark: TextView
    lateinit var orderTotalInfoTV: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapView = view.findViewById<MapView>(R.id.submitMapView)
        if (mapView != null) {
            mapView.onCreate(null)
            mapView.onResume()
            mapView.getMapAsync(this)
        }

        val locationRM = view.findViewById<TextView>(R.id.locationRemarkTV)
        locationRM.text = activity.locationRemarkText
        Log.e("location remark", locationRM.text.toString())
        if (locationRM.text.toString().isEmpty()) {
            locationRM.visibility = View.GONE
        }

    }

    override fun onMapReady(gMap: GoogleMap?) {
        MapsInitializer.initialize(context)
        if (activity.latLng != null) {
            gMap?.addMarker(MarkerOptions().position(activity.latLng!!))
            gMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(activity.latLng!!, StaticInformation().ZOOM_VAL))
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_summary, container, false)

        // region font
        val myFont= StaticInformation().myFont(context)
        view.findViewById<TextView>(R.id.selectedItemsTV).typeface = myFont
        view.findViewById<TextView>(R.id.locationTV).typeface = myFont
        view.findViewById<TextView>(R.id.contactInfoTV).typeface = myFont
        view.findViewById<TextView>(R.id.selectedDateTimeTV).typeface = myFont
        view.findViewById<TextView>(R.id.selectedDateTimeTV).typeface = myFont
        view.findViewById<TextView>(R.id.nameTV).typeface = myFont
        view.findViewById<TextView>(R.id.phoneTV).typeface = myFont
        view.findViewById<TextView>(R.id.remarkTV).typeface = myFont
        orderTotalInfoTV = view.findViewById(R.id.orderTotalInfoTV)
        orderTotalInfoTV.typeface = myFont
        val fromTime = view.findViewById<TextView>(R.id.preferableTimeFromTV)
        fromTime.typeface = myFont
        val toTime = view.findViewById<TextView>(R.id.preferableTimeToTV)
        toTime.typeface = myFont
        name = view.findViewById(R.id.selectedNameTV)
        name.typeface = myFont
        phone = view.findViewById(R.id.selectedPhoneTV)
        phone.typeface = myFont
        remark = view.findViewById(R.id.selectedRemarkTV)
        remark.typeface = myFont
        val submitBTN = view.findViewById<Button>(R.id.submitOrderBTN)
        submitBTN.typeface = myFont
        // endregion
        val chosenItemsLL = view.findViewById<LinearLayout>(R.id.chosenItemsLL)
        for (item in activity.finalSelectedItems) {
            if (activity.finalSelectedItems.indexOf(item) == activity.finalSelectedItems.size - 1) {
                chosenItemsLL.addView(SelectedItem(context!!, item, true).view)
            } else {
                chosenItemsLL.addView(SelectedItem(context!!, item, false).view)
            }
        }
        var total = 0
        for (itemOrder in activity.finalSelectedItems) {
            total += itemOrder.quantity * itemOrder.price
        }
        orderTotalInfoTV.text = "Total: ${StaticInformation().formatPrice(total)} S.P"

        name.text = activity.personName
        phone.text = activity.personPhone
        remark.text = activity.personRemark

        fromTime.text = activity.fromTime
        toTime.text = activity.toTime

        submitBTN.setOnClickListener {
            setupOrder()
        }

        return view
    }

    private fun setupOrder() {

        val itemsJSON = JSONArray()
        for (item in activity.finalSelectedItems) {
            val itemObject = JSONObject()
            itemObject.put("OrderItemID", item.id)
            itemObject.put("Quantity", item.quantity)
            itemObject.put("OfferID", item.offerID)
            itemsJSON.put(itemObject)
        }

        val orderObject = JSONObject()
        orderObject.put("OrderItems", itemsJSON)
        orderObject.put("FromTime", activity.fromTime)
        orderObject.put("ToTime", activity.toTime)
        orderObject.put("Location", "${activity.latLng?.latitude}, ${activity.latLng?.longitude}")
        orderObject.put("LocationRemark", activity.locationRemarkText)
        orderObject.put("Mobile", activity.personPhone)
        orderObject.put("Remark", activity.personRemark)
        orderObject.put("Name", activity.personName)

        activity.showLoader()
        val myToken= try {
            DatabaseHelper(context).userModelIntegerRuntimeException.queryForAll()[0].token
        } catch (err: Exception) {
            ""
        }
        val queue = Volley.newRequestQueue(context)
        val request = object : JsonObjectRequest(Request.Method.POST, APIsURL().CREATE_ORDER, orderObject,
                Response.Listener<JSONObject> {
                    activity.finish()
                    startActivity(Intent(context, MainActivity::class.java)
                            .putExtra("done", true)
                            .putExtra("code", it.getString("orderCode")))
                    Log.e("code", it.getString("orderCode"))
                    queue.cancelAll("order")
                }, Response.ErrorListener {
            Toast.makeText(context, "No Internet Connection, Please Try Again", Toast.LENGTH_SHORT).show()
            activity.hideLoader()
            queue.cancelAll("order")
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
        request.tag = "order"
        queue.add(request)
    }

}
