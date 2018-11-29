package com.smartlife_solutions.android.navara_store.OrderFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.MarkerOptions
import com.smartlife_solutions.android.navara_store.*
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.ItemBasicModel
import com.smartlife_solutions.android.navara_store.R
import org.json.JSONArray
import org.json.JSONObject

@SuppressLint("ValidFragment")
class OrderSummaryFragment(var activity: OrdersActivity) : Fragment(), OnMapReadyCallback {

    lateinit var name: TextView
    lateinit var phone: TextView
    lateinit var remark: TextView
    lateinit var orderTotalInfoTV: TextView
    lateinit var useWalletCB: CheckBox
    lateinit var walletPB: ProgressBar
    lateinit var promoCodeCB: CheckBox
    lateinit var promoCodeET: EditText

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

    lateinit var lang: JSONObject

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_summary, container, false)

        lang = Statics.getLanguageJSONObject(activity).getJSONObject("makeOrderActivity").getJSONObject("summaryFragment")

        // region font
        val myFont= StaticInformation().myFont(context)
        view.findViewById<TextView>(R.id.selectedItemsTV).typeface = myFont
        view.findViewById<TextView>(R.id.selectedItemsTV).text = lang.getString("chosenItems")
        view.findViewById<TextView>(R.id.locationTV).typeface = myFont
        view.findViewById<TextView>(R.id.locationTV).text = lang.getString("location")
        view.findViewById<TextView>(R.id.contactInfoTV).typeface = myFont
        view.findViewById<TextView>(R.id.contactInfoTV).text = lang.getString("contactInfo")
        view.findViewById<TextView>(R.id.selectedDateTimeTV).typeface = myFont
        view.findViewById<TextView>(R.id.selectedDateTimeTV).text = lang.getString("time")
        view.findViewById<TextView>(R.id.nameTV).typeface = myFont
        view.findViewById<TextView>(R.id.nameTV).text = lang.getString("name")
        view.findViewById<TextView>(R.id.phoneTV).typeface = myFont
        view.findViewById<TextView>(R.id.phoneTV).text = lang.getString("phone")
        view.findViewById<TextView>(R.id.remarkTV).typeface = myFont
        view.findViewById<TextView>(R.id.remarkTV).text = lang.getString("remark")
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
        useWalletCB = view.findViewById(R.id.summaryUserWalletCB)
        useWalletCB.typeface = myFont
        walletPB = view.findViewById(R.id.summaryWalletPB)

        promoCodeCB = view.findViewById(R.id.summaryPromoCodeCB)
        promoCodeCB.typeface = myFont
        promoCodeCB.text = lang.getString("havePromoCode")
        promoCodeET = view.findViewById(R.id.summaryPromoCodeET)
        promoCodeET.typeface = myFont
        promoCodeET.hint = lang.getString("promoCode")

        val submitBTN = view.findViewById<Button>(R.id.submitOrderBTN)
        submitBTN.typeface = myFont
        submitBTN.text = lang.getString("submit")
        // endregion

        val chosenItemsLL = view.findViewById<LinearLayout>(R.id.chosenItemsLL)

        if (activity.finalSelectedItems.size > 1) {
            activity.finalSelectedItems = activity.finalSelectedItems
                    .sortedWith(compareBy({ it.offerID }, { it.price })).reversed() as ArrayList<ItemBasicModel>
        }

        for (item in activity.finalSelectedItems) {
            if (activity.finalSelectedItems.indexOf(item) == activity.finalSelectedItems.size - 1) {
                chosenItemsLL.addView(SelectedItem(context!!, item, true, lang = Statics.getLanguageJSONObject(activity)).view)
            } else {
                chosenItemsLL.addView(SelectedItem(context!!, item, false, lang = Statics.getLanguageJSONObject(activity)).view)
            }
        }

        var total = 0
        for (itemOrder in activity.finalSelectedItems) {
            total += itemOrder.quantity * itemOrder.price
        }

        orderTotalInfoTV.text = "${lang.getString("total")} ${StaticInformation().formatPrice(total)} ${Statics.getLanguageJSONObject(activity).getString("currencyCode")}"

        name.text = activity.personName
        phone.text = activity.personPhone
        remark.text = activity.personRemark

        fromTime.text = activity.fromTime

        if (activity.toTime.split(':')[0][0] == '0') {
            toTime.text = activity.toTime
        } else {
            toTime.text = "09:00 PM"
        }

        getUserWallet()

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
        orderObject.put("LocationText", activity.locationText)
        orderObject.put("Mobile", activity.personPhone)
        orderObject.put("Remark", activity.personRemark)
        orderObject.put("Name", activity.personName)
        orderObject.put("UseWallet", useWalletCB.isChecked)
        if (promoCodeCB.isChecked) {
            orderObject.put("PromoCode", promoCodeET.text.toString().trim())
        } else {
            orderObject.put("PromoCode", "")
        }

        Log.e("order information", orderObject.toString())

        activity.showLoader()

        val queue = Volley.newRequestQueue(context)
        val request = object : JsonObjectRequest(Request.Method.POST, APIsURL().CREATE_ORDER, orderObject,
                Response.Listener<JSONObject> {
                    activity.finish()
                    var days = it.get("daysToDeliver").toString()
                    val orderDelivery = Statics.getLanguageJSONObject(activity).getJSONObject("OrderPreviewActivity")
                    days = try {
                        when {
                            days.toFloat().toInt() == 0 -> orderDelivery.getString("tomorrow")
                            days.toFloat().toInt() == 1 -> "${orderDelivery.getString("during")} ${days.toFloat().toInt()} ${orderDelivery.getString("day")}"
                            else -> "${orderDelivery.getString("during")} ${days.toFloat().toInt()} ${orderDelivery.getString("days")}"
                        }
                    } catch (e: Exception) {
                        orderDelivery.getString("tomorrow")
                    }
                    startActivity(Intent(context, MainActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .putExtra("done", true)
                            .putExtra("code", it.getString("orderCode") + "\n" + days))
                    Log.e("order", it.toString())
                    queue.cancelAll("order")
                }, Response.ErrorListener {
            Toast.makeText(context, Statics.getLanguageJSONObject(activity).getString("noInternet"), Toast.LENGTH_SHORT).show()
            activity.hideLoader()
            queue.cancelAll("order")
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
        request.tag = "order"
        queue.add(request)
    }

    private fun getUserWallet() {

        useWalletCB.text = lang.getString("yourWallet")

        val queue = Volley.newRequestQueue(context)
        val getWallet = @SuppressLint("SetTextI18n")
        object : StringRequest(Request.Method.GET, APIsURL().GET_WALLET, {
            queue.cancelAll("wallet")
            try {
                Log.e("wallet", it)
                useWalletCB.text = "${lang.getString("yourWallet")} (${StaticInformation().formatPrice(it.toFloat().toInt())} ${Statics.getLanguageJSONObject(activity).getString("currencyCode")})"
                walletPB.visibility = View.GONE
            } catch (err: Exception) {}
        }, {
            queue.cancelAll("wallet")
            getUserWallet()
        }) {
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=UTF-8"
                params["Authorization"] = "Bearer ${Statics.myToken}"
                return params
            }
        }
        getWallet.tag = "wallet"
        queue.add(getWallet)

    }

}
