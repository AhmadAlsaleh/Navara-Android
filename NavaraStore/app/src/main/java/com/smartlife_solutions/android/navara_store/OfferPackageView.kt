package com.smartlife_solutions.android.navara_store

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.smartlife_solutions.android.navara_store.Adapters.PreviewFreeItemsAdapter
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.ItemBasicModel
import com.smartlife_solutions.android.navara_store.Dialogs.SureToDoDialog
import com.smartlife_solutions.android.navara_store.OrderFragments.OrderSelectItemsFragment
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

@SuppressLint("ViewConstructor, SetTextI18n")
class OfferPackageView(var fragment: Fragment, context: Context, private var items: ArrayList<ItemBasicModel>, offers: ArrayList<ItemBasicModel>,
                       var fromCart: Boolean, disOffer: Boolean = false,
                       var lang: JSONObject) : View(context) {

    val view = inflate(context, R.layout.item_offer_package, null)!!
    private val offerCV: CardView = view.findViewById(R.id.offerCV)
    private val itemsRV: RecyclerView = view.findViewById(R.id.offerItemsRV)
    private val offersRV: RecyclerView = view.findViewById(R.id.offerOffersRV)
    private val getFreeTV: TextView = view.findViewById(R.id.getFreeTV)
    private val offerCB: CheckBox = view.findViewById(R.id.offerCB)
    private val offerDeleteIV: ImageView = view.findViewById(R.id.offerDeleteIV)
    private val itemRemoveIV: ImageView = view.findViewById(R.id.itemRemoveIV)
    private val itemQuantityTV: TextView = view.findViewById(R.id.itemQuantityTV)
    private val itemAddIV: ImageView = view.findViewById(R.id.itemAddIV)
    private val itemSelectTotalTV: TextView = view.findViewById(R.id.itemSelectTotalTV)
    private val offerGiftIV: ImageView = view.findViewById(R.id.offerGiftIV)
    private val offerPercentTV: TextView = view.findViewById(R.id.offerPercentTV)

    init {
        getFreeTV.typeface = StaticInformation().myFont(context)
        getFreeTV.text = lang.getJSONObject("offerFreePreviewActivity").getString("andGet")
        itemSelectTotalTV.typeface = StaticInformation().myFont(context)
        itemSelectTotalTV.setOnClickListener(null)
        itemSelectTotalTV.text = (StaticInformation().formatPrice(items[0].quantity * items[0].price)) + " " + lang.getString("currencyCode")
        itemQuantityTV.typeface = StaticInformation().myFont(context)
        itemQuantityTV.text = items[0].quantity.toString()

        itemAddIV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            val jsonObject = JSONObject()
            jsonObject.put("OfferID", items[0].offerID)
            jsonObject.put("Quantity", itemQuantityTV.text.toString())
            editQuantity(itemSelectTotalTV, itemQuantityTV, true)
        }

        offerCB.setOnCheckedChangeListener { _, isChecked ->
            for (item in (fragment as OrderSelectItemsFragment).activity.itemsHash.values) {
                if (item == items[0]) {
                    item.isChecked = isChecked
                    for (offer in (fragment as OrderSelectItemsFragment).activity.offersHash) {
                        if (offer.offerID == item.offerID) {
                            offer.isChecked = isChecked
                        }
                    }
                }
            }
        }

        itemRemoveIV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            if (items[0].quantity > 1) {
                val jsonObject = JSONObject()
                jsonObject.put("OfferID", items[0].offerID)
                jsonObject.put("Quantity", itemQuantityTV.text.toString())
                editQuantity(itemSelectTotalTV, itemQuantityTV, false)
            } else {
                Toast.makeText(context, lang.getJSONObject("itemsList").getString("oneItem"), Toast.LENGTH_SHORT).show()
            }
        }

        offerDeleteIV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            val sureRemove = SureToDoDialog(context, lang.getJSONObject("dialogs").getJSONObject("sure").getString("removeFromCart"))
            sureRemove.show()
            sureRemove.setOnDismissListener {
                if (sureRemove.isTrue) {
                    removeFromCart(items[0].offerID)
                }
            }
        }

        for (i in items) {
            i.cashBack = "0.0"
        }
        for (o in offers) {
            o.cashBack = "0.0"
        }
        itemsRV.setHasFixedSize(true)
        itemsRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        itemsRV.adapter = PreviewFreeItemsAdapter(context = context, itemsArrayList = items, isAll = false, lang = lang)

        offersRV.setHasFixedSize(true)
        offersRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        offersRV.adapter = PreviewFreeItemsAdapter(context = context, itemsArrayList = offers, isAll = false, lang = lang)

        if (disOffer) {
            getFreeTV.visibility = View.GONE
            offerGiftIV.visibility = View.GONE
            offerPercentTV.visibility = View.VISIBLE
            offerCV.setOnClickListener {
                context.startActivity(Intent(context, OfferPreviewActivity::class.java)
                        .putExtra("id", items[0].offerID))
            }
        } else {
            getFreeTV.visibility = View.VISIBLE
            offerGiftIV.visibility = View.VISIBLE
            offerPercentTV.visibility = View.GONE
            offerCV.setOnClickListener {
                context.startActivity(Intent(context, OfferFreePreviewActivity::class.java).putExtra("id", items[0].offerID))
            }
        }

        if (fromCart) {
            offerCB.visibility = View.GONE
            offerDeleteIV.visibility = View.VISIBLE
        } else {
            offerCB.visibility = View.VISIBLE
            offerDeleteIV.visibility = View.GONE
        }
    }

    private fun removeFromCart(id: String) {

        val jsonBody = JSONObject()
        jsonBody.put("offerID", id)
        val requestBody: String = jsonBody.toString()
        val queue = Volley.newRequestQueue(context)
        val request = object : StringRequest(Request.Method.POST, APIsURL().REMOVE_OFFER_FROM_CART,
                Response.Listener<String> {
                    Toast.makeText(context, lang.getJSONObject("offersActivity").getString("offerRemoved"), Toast.LENGTH_SHORT).show()
                    offerCV.startAnimation(StaticInformation().fadeOutAnim(context))
                    Handler().postDelayed({
                        try {
                            offerCV.visibility = View.GONE
                            if (fromCart) {
//                                (fragment as CartFragment).getCartItems()
                            }
                        } catch (err: Exception) {}
                    }, 500)
                    queue.cancelAll("change")
                }, Response.ErrorListener {
            Toast.makeText(context, lang.getString("noInternet"), Toast.LENGTH_SHORT).show()
            Log.e("error", it.toString())
            queue.cancelAll("change")
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

            override fun getBody(): ByteArray? {
                return try {
                    requestBody.toByteArray(Charset.forName("utf-8"))
                } catch (err: UnsupportedEncodingException) {
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
        request.tag = "change"
        queue.add(request)
    }

    @SuppressLint("SetTextI18n")
    private fun editQuantity(totalTV: TextView, quantityTV: TextView, isAdd: Boolean) {
        val id = items[0].offerID
        val quantity: Int = if (isAdd) {
            items[0].quantity + 1
        } else {
            items[0].quantity - 1
        }
        quantityTV.text = quantity.toString()
        totalTV.text = StaticInformation().formatPrice(quantity * (items[0].price)) + " " + lang.getString("currencyCode")

        val jsonBody = JSONObject()
        jsonBody.put("OfferID", id)
        jsonBody.put("Quantity", quantity)

        val requestBody: String = jsonBody.toString()
        Log.e("item", requestBody)
        val queue = Volley.newRequestQueue(context)
        val request = object : StringRequest(Request.Method.POST, APIsURL().ADD_OFFER_TO_CART,
                Response.Listener<String> {
                    Toast.makeText(context, lang.getJSONObject("itemsList").getString("changeQuantity"), Toast.LENGTH_SHORT).show()
                    items[0].quantity = quantity
                    queue.cancelAll("add")
                }, Response.ErrorListener {
            Toast.makeText(context, lang.getString("noInternet"), Toast.LENGTH_SHORT).show()
            quantityTV.text = items[0].quantity.toString()
            totalTV.text = StaticInformation().formatPrice(items[0].quantity * (items[0].price)) + " " + lang.getString("currencyCode")
            Log.e("error", it.toString())
            queue.cancelAll("add")
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

            override fun getBody(): ByteArray? {
                return try {
                    requestBody.toByteArray(Charset.forName("utf-8"))
                } catch (err: Exception) {
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
        request.tag = "add"
        queue.add(request)
    }


}