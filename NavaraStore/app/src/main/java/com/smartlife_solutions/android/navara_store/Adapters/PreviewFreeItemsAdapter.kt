package com.smartlife_solutions.android.navara_store.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.smartlife_solutions.android.navara_store.Dialogs.ChooseQuantityDialog
import com.smartlife_solutions.android.navara_store.Dialogs.SureToDoDialog
import android.os.Handler
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.smartlife_solutions.android.navara_store.*
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.ItemBasicModel
import com.smartlife_solutions.android.navara_store.ItemPreviewActivity
import com.smartlife_solutions.android.navara_store.LoginRegisterActivity
import com.smartlife_solutions.android.navara_store.ProfileCartOrderFragments.CartFragment
import com.smartlife_solutions.android.navara_store.StaticInformation
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

class PreviewFreeItemsAdapter(var fragment: CartFragment? = null, var context: Context,
                              private var itemsArrayList: ArrayList<ItemBasicModel>,
                              private var isAll: Boolean, private var isForCart: Boolean = false):
        RecyclerView.Adapter<PreviewFreeItemsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return if (isAll) {
            ViewHolder(LayoutInflater.from(p0.context)
                    .inflate(R.layout.item_items_card, p0, false))
        } else {
            ViewHolder(LayoutInflater.from(p0.context)
                    .inflate(R.layout.item_items_offers_free, p0, false))
        }
    }

    override fun getItemCount(): Int = itemsArrayList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemTitle.text = itemsArrayList[position].name
        holder.itemPriceTV.text = StaticInformation().formatPrice(itemsArrayList[position].price) + " " + itemsArrayList[position].currencyCode
        val myFont = StaticInformation().myFont(context)
        holder.itemTitle.typeface = myFont
        holder.itemPriceTV.typeface = myFont
        holder.itemPriceTitle.typeface = myFont

        Picasso.with(context)
                .load(APIsURL().BASE_URL + itemsArrayList[position].thumbnailImagePath)
                .placeholder(R.drawable.no_image)
                .into(holder.itemImage)

        if (isAll) {
            if (isForCart) {
                holder.itemCartIV.setImageResource(R.drawable.ic_close_primary)
                holder.itemCartIV.setOnClickListener {
                    it.startAnimation(StaticInformation().clickAnim(context))
                    val sureRemove = SureToDoDialog(context, "Sure to remove from Cart?")
                    sureRemove.show()
                    sureRemove.setOnDismissListener {
                        if (sureRemove.isTrue) {
                            removeFromCart(holder.itemLL, itemsArrayList[position].id)
                        }
                    }
                }
                holder.itemQuantityLL.visibility = View.VISIBLE
                holder.itemQuantityTV.typeface = myFont
                holder.itemQuantityTV.text = itemsArrayList[position].quantity.toString()

                holder.itemQuantityTV.setOnClickListener(null)

                holder.itemSelectTotalTV.text = "Total: " + StaticInformation().formatPrice(itemsArrayList[position].quantity * (itemsArrayList[position].price)) + " " + itemsArrayList[position].currencyCode

                holder.itemAddIV.setOnClickListener {
                    editQuantity(position, holder.itemSelectTotalTV, holder.itemQuantityTV, true)
                }

                holder.itemRemoveIV.setOnClickListener {
                    if (itemsArrayList[position].quantity > 1) {
                        editQuantity(position, holder.itemSelectTotalTV, holder.itemQuantityTV, false)
                    } else {
                        Toast.makeText(context, "One item at least", Toast.LENGTH_LONG).show()
                    }
                }


                if (itemsArrayList[position].isFree) {
                    holder.itemSelectTotalTV.visibility = View.GONE
                    holder.itemAddIV.setOnClickListener(null)
                    holder.itemRemoveIV.setOnClickListener(null)
                    holder.itemCartIV.setOnClickListener(null)
                    holder.itemPriceTV.text = "Free"
                    holder.itemLL.setBackgroundResource(R.color.light_blue_gray)
                } else {
                    holder.itemLL.setBackgroundResource(R.color.whiteBack)
                    holder.itemSelectTotalTV.text = "Total: ${StaticInformation().formatPrice((itemsArrayList[position].price) * itemsArrayList[position].quantity)} S.P"
                    holder.itemPriceTV.text = "${(StaticInformation().formatPrice(itemsArrayList[position].price))} S.P"
                }

            } else {
                holder.itemQuantityLL.visibility = View.GONE
                holder.itemCartIV.setOnClickListener {
                    it.startAnimation(StaticInformation().clickAnim(context))

                    try {
                        if (DatabaseHelper(context).userModelIntegerRuntimeException.queryForAll()[0].token.isNotEmpty()) {
                            if (itemsArrayList[position].quantity > 0) {
                                ChooseQuantityDialog(context, itemsArrayList[position], false).show()
                            } else {
                                Toast.makeText(context, "Out of Stock", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            context.startActivity(Intent(context, LoginRegisterActivity::class.java).putExtra("main", false))
                        }
                    } catch (err: Exception) {
                        context.startActivity(Intent(context, LoginRegisterActivity::class.java).putExtra("main", false))
                    }

                }
            }
            holder.itemLL.setOnClickListener {
                it.startAnimation(StaticInformation().clickAnim(context))
                context.startActivity(
                        Intent(context, ItemPreviewActivity::class.java)
                                .putExtra("id", itemsArrayList[position].id))
            }
        } else {
            if (itemsArrayList.size == (position + 1)) {
                holder.itemDividerRL.visibility = View.GONE
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun editQuantity(position: Int, totalTV: TextView, quantityTV: TextView, isAdd: Boolean) {
        val id = itemsArrayList[position].id
        val quantity: Int = if (isAdd) {
            itemsArrayList[position].quantity + 1
        } else {
            itemsArrayList[position].quantity - 1
        }
        quantityTV.text = quantity.toString()
        totalTV.text = "Total: " + StaticInformation().formatPrice(quantity * (itemsArrayList[position].price)) + " " + itemsArrayList[position].currencyCode

        val jsonBody = JSONObject()
        jsonBody.put("ItemID", id)
        jsonBody.put("Quantity", quantity)
        val myToken= try {
            DatabaseHelper(context).userModelIntegerRuntimeException.queryForAll()[0].token
        } catch (err: Exception) {
            ""
        }
        val requestBody: String = jsonBody.toString()
        Log.e("item", requestBody)
        val queue = Volley.newRequestQueue(context)
        val request = object : StringRequest(Request.Method.POST, APIsURL().ADD_ITEM_TO_CART,
                Response.Listener<String> {
                    Toast.makeText(context, "Quantity has changed Successfully", Toast.LENGTH_SHORT).show()
                    itemsArrayList[position].quantity = quantity
                    queue.cancelAll("add")
                }, Response.ErrorListener {
            Toast.makeText(context, "No Internet Connection, Please Try Again", Toast.LENGTH_SHORT).show()
            quantityTV.text = itemsArrayList[position].quantity.toString()
            totalTV.text = "Total: " + StaticInformation().formatPrice(itemsArrayList[position].quantity * (itemsArrayList[position].price)) + " " + itemsArrayList[position].currencyCode
            Log.e("error", it.toString())
            queue.cancelAll("add")
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

    private fun removeFromCart(itemLL: LinearLayout, id: String) {

        val myToken= try {
            DatabaseHelper(context).userModelIntegerRuntimeException.queryForAll()[0].token
        } catch (err: Exception) {
            ""
        }
        val jsonBody = JSONObject()
        jsonBody.put("itemID", id)
        val requestBody: String = jsonBody.toString()
        val queue = Volley.newRequestQueue(context)
        val request = object : StringRequest(Request.Method.POST, APIsURL().REMOVE_FROM_CART,
                Response.Listener<String> {
                    Toast.makeText(context, "Item has removed Successfully", Toast.LENGTH_SHORT).show()
                    itemLL.startAnimation(StaticInformation().fadeOutAnim(context))
                    Handler().postDelayed({
                        try {
                            itemLL.visibility = View.GONE
//                            fragment?.getCartItems()
                        } catch (err: Exception) {}
                    }, 500)
                    queue.cancelAll("change")
                }, Response.ErrorListener {
            Toast.makeText(context, "No Internet Connection, Please Try Again", Toast.LENGTH_SHORT).show()
            queue.cancelAll("change")
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

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val itemLL = itemView.findViewById<LinearLayout>(R.id.itemLL)!!
        val itemTitle = itemView.findViewById<TextView>(R.id.itemTV)!!
        val itemImage = itemView.findViewById<CircleImageView>(R.id.itemIV)!!
        val itemPriceTV = itemView.findViewById<TextView>(R.id.itemPriceTV)!!
        val itemPriceTitle = itemView.findViewById<TextView>(R.id.itemPriceTitle)!!
        lateinit var itemQuantityTV: TextView
        lateinit var itemDividerRL: RelativeLayout
        lateinit var itemCartIV: ImageView
        lateinit var itemQuantityLL: LinearLayout
        lateinit var itemAddIV: ImageView
        lateinit var itemRemoveIV: ImageView
        lateinit var itemSelectTotalTV: TextView
        lateinit var itemAddRemoveLL: LinearLayout

        init {
            try {
                itemAddRemoveLL = itemView.findViewById(R.id.itemAddRemoveBackLL)!!
            } catch (err: Exception) {}
            try {
                itemSelectTotalTV = itemView.findViewById(R.id.itemSelectTotalTV)!!
            } catch (err: Exception) {}
            try {
                itemAddIV = itemView.findViewById(R.id.itemAddIV)!!
            } catch (err: Exception) {}
            try {
                itemRemoveIV = itemView.findViewById(R.id.itemRemoveIV)!!
            } catch (err: Exception) {}
            try {
                itemQuantityLL = itemView.findViewById(R.id.chooseQuantityLL)!!
            } catch (err: Exception) {}

            try {
                itemDividerRL = itemView.findViewById(R.id.itemDividerRL)!!
            } catch (err: Exception) {}

            try {
                itemCartIV = itemView.findViewById(R.id.itemCartIV)!!
            } catch (err: Exception) {}

            try {
                itemQuantityTV = itemView.findViewById(R.id.itemQuantityTV)!!
            } catch (err: Exception) {}
        }
    }

}