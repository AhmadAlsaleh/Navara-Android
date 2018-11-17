package com.smartlife_solutions.android.navara_store.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation
import com.smartlife_solutions.android.navara_store.Statics
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.nio.charset.Charset

class SelectItemsRecyclerAdapter(var context: Context, var items: ArrayList<ItemBasicModel>)
    : RecyclerView.Adapter<SelectItemsRecyclerAdapter.ViewHolder>() {

    private lateinit var mapTagCheckBox: HashMap<String, CheckBox>
    private lateinit var mapTagPosition: HashMap<String, Int>

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(p0.context)
                    .inflate(R.layout.item_select_item_card, p0, false))

    override fun getItemCount(): Int = items.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // region font
        val myFont = StaticInformation().myFont(context)
        holder.itemTV.typeface = myFont
        holder.itemPriceTV.typeface = myFont
        holder.itemPriceTitle.typeface = myFont
        holder.itemQuantity.typeface = myFont
        // endregion

        mapTagCheckBox = HashMap()
        mapTagPosition = HashMap()
        for (i in items) {
            if (i.offerID.isNotEmpty()) {
                mapTagCheckBox[i.offerID] = holder.itemCB
                mapTagPosition[i.offerID] = position
            }
        }

        val item = items[position]
        holder.itemTV.text = item.name
        holder.itemPriceTV.text = StaticInformation().formatPrice(item.price) + " " + item.currencyCode
        holder.itemQuantity.text = item.quantity.toString()
        holder.itemCB.isChecked = item.isChecked
        holder.itemSelectTotalTV.text = "Total: ${StaticInformation().formatPrice((item.quantity * item.price))} ${item.currencyCode}"

        Picasso.with(context)
                .load(APIsURL().BASE_URL + item.thumbnailImagePath)
                .placeholder(R.drawable.no_image)
                .into(holder.itemIV)

        holder.itemRemove.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            if (item.quantity == 1) {
                Toast.makeText(context, "One at least", Toast.LENGTH_SHORT).show()
            } else {
                editQuantity(position, holder.itemSelectTotalTV ,holder.itemQuantity, false)
            }
        }

        holder.itemAdd.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            editQuantity(position, holder.itemSelectTotalTV, holder.itemQuantity, true)
        }

        if (!item.isFree) {
            holder.itemLL.setBackgroundResource(R.color.whiteBack)
            holder.itemCB.setOnCheckedChangeListener { _, isChecked ->
                run {
                    item.isChecked = isChecked
                    items[position].isChecked = isChecked
                    if (items[position].offerID.isNotEmpty()) {
                        for (it in mapTagCheckBox.keys) {
                            if (it == items[position].offerID) {
                                mapTagCheckBox[it]?.isChecked = isChecked
                                items[mapTagPosition[it]!!].isChecked = isChecked
                            }
                        }
                    }
                }
            }
        } else {
            holder.itemCB.setOnClickListener {
                holder.itemCB.isChecked = false
            }
            holder.itemAdd.setOnClickListener(null)
            holder.itemRemove.setOnClickListener(null)
            holder.itemPriceTV.text = "Free"
            holder.itemSelectTotalTV.visibility = View.GONE
            holder.itemLL.setBackgroundResource(R.color.light_blue_gray)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun editQuantity(position: Int, totalTV: TextView, quantityTV: TextView, isAdd: Boolean) {
        val id = items[position].id
        val quantity: Int = if (isAdd) {
            items[position].quantity + 1
        } else {
            items[position].quantity - 1
        }
        quantityTV.text = quantity.toString()
        totalTV.text = "Total: " + StaticInformation().formatPrice(quantity * (items[position].price)) + " " + items[position].currencyCode


        val jsonBody = JSONObject()
        jsonBody.put("ItemID", id)
        jsonBody.put("Quantity", quantity)

        val requestBody: String = jsonBody.toString()
        Log.e("item", requestBody)
        val queue = Volley.newRequestQueue(context)
        val request = object : StringRequest(Request.Method.POST, APIsURL().ADD_ITEM_TO_CART,
                Response.Listener<String> {
                    Toast.makeText(context, "Quantity has changed Successfully", Toast.LENGTH_SHORT).show()
                    items[position].quantity = quantity
                    queue.cancelAll("add")
                }, Response.ErrorListener {
            Toast.makeText(context, "No Internet Connection, Please Try Again", Toast.LENGTH_SHORT).show()
            quantityTV.text = items[position].quantity.toString()
            totalTV.text = "Total: " + StaticInformation().formatPrice(items[position].quantity * (items[position].price)) + " " + items[position].currencyCode
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

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemIV = itemView.findViewById<ImageView>(R.id.itemIV)!!
        val itemLL = itemView.findViewById<LinearLayout>(R.id.itemLL)!!
        val itemTV = itemView.findViewById<TextView>(R.id.itemTV)!!
        val itemPriceTitle = itemView.findViewById<TextView>(R.id.itemPriceTitle)!!
        val itemPriceTV = itemView.findViewById<TextView>(R.id.itemPriceTV)!!
        val itemRemove = itemView.findViewById<ImageView>(R.id.itemRemoveIV)!!
        val itemAdd = itemView.findViewById<ImageView>(R.id.itemAddIV)!!
        val itemQuantity = itemView.findViewById<TextView>(R.id.itemQuantityTV)!!
        val itemCB = itemView.findViewById<CheckBox>(R.id.itemCB)!!
        val itemSelectTotalTV = itemView.findViewById<TextView>(R.id.itemSelectTotalTV)!!
    }
}