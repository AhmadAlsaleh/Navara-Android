package com.smartlife_solutions.android.navara_store

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.support.v4.app.Fragment
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
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.ItemBasicModel
import com.smartlife_solutions.android.navara_store.Dialogs.SureToDoDialog
import com.smartlife_solutions.android.navara_store.OrderFragments.OrderSelectItemsFragment
import com.smartlife_solutions.android.navara_store.ProfileCartOrderFragments.CartFragment
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*

@SuppressLint("SetTextI18n", "ViewConstructor")
class ItemView(var fragment: Fragment, context: Context, var item: ItemBasicModel, var fromCart: Boolean, var lang: JSONObject)
    : View(context) {

    val view = inflate(context, R.layout.item_items_card, null)!!
    init {

        val itemLL = view.findViewById<LinearLayout>(R.id.itemLL)
        val itemIV = view.findViewById<CircleImageView>(R.id.itemIV)
        val itemTV = view.findViewById<TextView>(R.id.itemTV)
        val itemPriceTitle = view.findViewById<TextView>(R.id.itemPriceTitle)
        val itemPriceTV = view.findViewById<TextView>(R.id.itemPriceTV)
        val itemRemoveIV = view.findViewById<ImageView>(R.id.itemRemoveIV)
        val itemQuantityTV = view.findViewById<TextView>(R.id.itemQuantityTV)
        val itemAddIV = view.findViewById<ImageView>(R.id.itemAddIV)
        val itemSelectTotalTV = view.findViewById<TextView>(R.id.itemSelectTotalTV)
        val itemCartIV = view.findViewById<ImageView>(R.id.itemCartIV)
        val itemCB = view.findViewById<CheckBox>(R.id.itemCB)
        val itemCashBackLL: LinearLayout = view.findViewById(R.id.itemItemsCashBackLL)
        val itemCashBackTV: TextView = view.findViewById(R.id.itemItemsCashBackTV)
        itemCartIV.setImageResource(R.drawable.ic_close_primary)

        try {
            if (item.cashBack.toFloat().toInt() == 0) {
                itemCashBackLL.visibility = View.GONE
            } else {
                itemCashBackLL.visibility = View.VISIBLE
                itemCashBackTV.text = item.cashBack.toFloat().toInt().toString() + " ${lang.getString("currencyCode")}"
            }
        } catch (err: Exception) {
            itemCashBackLL.visibility = View.GONE
        }

        if (!fromCart) {
            itemCB.visibility = View.VISIBLE
            itemCartIV.visibility = View.GONE
        }

        itemCB.setOnCheckedChangeListener { _, isChecked ->
            val i = (fragment as OrderSelectItemsFragment).activity.items.indexOf(item)
            (fragment as OrderSelectItemsFragment).activity.items[i].isChecked = isChecked
        }

        itemCartIV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            val sureRemove = SureToDoDialog(context, lang.getJSONObject("dialogs").getJSONObject("sure").getString("removeFromCart"))
            sureRemove.show()
            sureRemove.setOnDismissListener {
                if (sureRemove.isTrue) {
                    removeFromCart(itemLL, item.id)
                }
            }
        }

        itemRemoveIV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            if (item.quantity > 1) {
                editQuantity(itemSelectTotalTV, itemQuantityTV, false)
            } else {
                Toast.makeText(context, lang.getJSONObject("itemsList").getString("oneItem"), Toast.LENGTH_LONG).show()
            }
        }

        itemAddIV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            editQuantity(itemSelectTotalTV, itemQuantityTV, true)
        }

        Picasso.with(context)
                .load(APIsURL().BASE_URL + item.thumbnailImagePath)
                .into(itemIV)

        itemLL.setOnClickListener {
            context.startActivity(Intent(context, ItemPreviewActivity::class.java).putExtra("id", item.id))
        }

        itemTV.text = item.name
        itemPriceTV.text = StaticInformation().formatPrice(item.price) + " " + lang.getString("currencyCode")
        itemQuantityTV.text = item.quantity.toString()
        itemSelectTotalTV.text = StaticInformation().formatPrice(item.quantity * item.price) + " " + lang.getString("currencyCode")

    }

    private fun removeFromCart(itemView: View, id: String) {

        val jsonBody = JSONObject()
        jsonBody.put("itemID", id)
        val requestBody: String = jsonBody.toString()
        val queue = Volley.newRequestQueue(context)
        val request = object : StringRequest(Request.Method.POST, APIsURL().REMOVE_FROM_CART,
                Response.Listener<String> {
                    Toast.makeText(context, lang.getJSONObject("itemsList").getString("itemRemoved"), Toast.LENGTH_SHORT).show()
                    itemView.startAnimation(StaticInformation().fadeOutAnim(context))
                    Handler().postDelayed({
                        try {
                            itemView.visibility = View.GONE
                            if (fromCart) {
//                                (fragment as CartFragment).getCartItems()
                            }
                        } catch (err: Exception) {}
                    }, 500)
                    queue.cancelAll("change")
                }, Response.ErrorListener {
            Toast.makeText(context, lang.getString("noInternet"), Toast.LENGTH_SHORT).show()
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
        val id = item.id
        val quantity: Int = if (isAdd) {
            item.quantity + 1
        } else {
            item.quantity - 1
        }
        quantityTV.text = quantity.toString()
        totalTV.text = StaticInformation().formatPrice(quantity * (item.price)) + " " + lang.getString("currencyCode")

        val jsonBody = JSONObject()
        jsonBody.put("ItemID", id)
        jsonBody.put("Quantity", quantity)

        val requestBody: String = jsonBody.toString()
        Log.e("item", requestBody)
        val queue = Volley.newRequestQueue(context)
        val request = object : StringRequest(Request.Method.POST, APIsURL().ADD_ITEM_TO_CART,
                Response.Listener<String> {
                    Toast.makeText(context, lang.getJSONObject("itemsList").getString("changeQuantity"), Toast.LENGTH_SHORT).show()
                    item.quantity = quantity
                    queue.cancelAll("add")
                }, Response.ErrorListener {
            Toast.makeText(context, lang.getString("noInternet"), Toast.LENGTH_SHORT).show()
            quantityTV.text = item.quantity.toString()
            totalTV.text = StaticInformation().formatPrice(item.quantity * (item.price)) + " " + lang.getString("currencyCode")
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