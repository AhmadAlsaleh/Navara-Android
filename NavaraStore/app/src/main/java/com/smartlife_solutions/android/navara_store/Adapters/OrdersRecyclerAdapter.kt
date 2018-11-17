package com.smartlife_solutions.android.navara_store.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
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
import com.google.gson.Gson
import com.smartlife_solutions.android.navara_store.*
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.Dialogs.SureToDoDialog
import com.smartlife_solutions.android.navara_store.Models.OrderModal
import com.smartlife_solutions.android.navara_store.ProfileCartOrderFragments.OrdersFragment
import org.json.JSONObject
import com.smartlife_solutions.android.navara_store.StaticInformation

class OrdersRecyclerAdapter(var context: Context, var orders: ArrayList<OrderModal>, var lang: JSONObject)
    : RecyclerView.Adapter<OrdersRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(p0.context)
                    .inflate(R.layout.item_order, p0, false))

    override fun getItemCount(): Int = orders.size

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        val myFont = StaticInformation().myFont(context)
        // region font
        holder.orderDate.typeface = myFont
        holder.orderDateText.typeface = myFont
        holder.orderTotalPrice.typeface = myFont
        holder.orderTotalTV.typeface = myFont
        holder.orderCodeTV.typeface = myFont
        holder.orderCodeTVText.typeface = myFont
        // endregion

        holder.orderCodeTVText.text = lang.getJSONObject("OrderPreviewActivity").getString("code")
        holder.orderDateText.text = lang.getJSONObject("OrderPreviewActivity").getString("date")
        holder.orderTotalTV.visibility = View.GONE

        holder.orderTotalPrice.text = StaticInformation().formatPrice(order.totalPrice.toInt()) + " " + lang.getString("currencyCode")
        holder.orderDate.text = order.date
        holder.orderCodeTV.text = order.code
        holder.orderLL.setOnClickListener {
            context.startActivity(Intent(context, OrderInformationActivity::class.java)
                    .putExtra("id_order", order.id))
        }

        when {
            order.status.contains("Canceled", true) -> {
                holder.orderMoreIV.setImageResource(R.drawable.ic_close_white)
                holder.orderStatusRL.setBackgroundResource(R.drawable.ic_circle_red)
            }
            order.status.contains("Delivered", true) -> {
                holder.orderMoreIV.setImageResource(R.drawable.ic_check_white)
                holder.orderStatusRL.setBackgroundResource(R.drawable.ic_circle_green)
            }
            else -> {
                holder.orderMoreIV.setImageResource(R.drawable.ic_hourglass)
                holder.orderStatusRL.setBackgroundResource(R.drawable.ic_circle_orange)
            }
        }

        val itemPopupMenu = PopupMenu(context, holder.orderMenu)
        itemPopupMenu.menuInflater.inflate(R.menu.order_menu, itemPopupMenu.menu)
        itemPopupMenu.menu.findItem(R.id.cancelOrder).title = lang.getJSONObject("OrderPreviewActivity").getString("cancel")
        itemPopupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.cancelOrder -> {
                    val sureToDoDialog = SureToDoDialog(context,
                            lang.getJSONObject("dialogs").getJSONObject("sure").getString("cancelOrder"))
                    sureToDoDialog.show()
                    sureToDoDialog.setOnDismissListener {
                        if (sureToDoDialog.isTrue) {
                            try {
                                cancelOrder(position, holder.orderStatusRL, holder.orderMoreIV)
                            } catch (err: Exception) {}
                        }
                    }
                    true
                }

                else -> false
            }
        }
        holder.orderMenu.setOnClickListener {
            itemPopupMenu.show()
        }

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun cancelOrder(position: Int, orderStatusRL: RelativeLayout, orderMoreIV: ImageView) {
        val order = orders[position]

        if (order.status.contains("Canceled", true)) {
            Toast.makeText(context, lang.getJSONObject("OrderPreviewActivity").getString("alreadyCanceled"), Toast.LENGTH_SHORT).show()
            orderMoreIV.setImageResource(R.drawable.ic_close_white)
            orderStatusRL.setBackgroundResource(R.drawable.ic_circle_red)
            return
        }

        val queue = Volley.newRequestQueue(context)
        val cancelRequest = object : StringRequest(Request.Method.GET, APIsURL().CANCEL_ORDER + order.id, {
            try {
                orderMoreIV.setImageResource(R.drawable.ic_close_white)
                orderStatusRL.setBackgroundResource(R.drawable.ic_circle_red)
                queue.cancelAll("cancel_order")
            } catch (err: Exception) {}
        }, {
            try {
                if (it.networkResponse.statusCode == 400) {
                    Toast.makeText(context, lang.getJSONObject("OrderPreviewActivity").getString("cannotCancel"), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, lang.getString("noInternet"), Toast.LENGTH_SHORT).show()
                }
            } catch (err: Exception) {}
            queue.cancelAll("cancel_order")
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=UTF-8"
                params["Authorization"] = "Bearer ${Statics.myToken}"
                return params
            }

            override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
                var responseString = ""
                if (response != null) {
                    responseString = response.statusCode.toString()
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response))
            }
        }
        cancelRequest.tag = "cancel_order"
        queue.add(cancelRequest)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val orderLL = itemView.findViewById<LinearLayout>(R.id.orderLL)!!
        val orderTotalTV = itemView.findViewById<TextView>(R.id.orderTotalTV)!!
        val orderTotalPrice = itemView.findViewById<TextView>(R.id.orderTotalPrice)!!
        val orderDate = itemView.findViewById<TextView>(R.id.orderDateTV)!!
        val orderDateText = itemView.findViewById<TextView>(R.id.orderDateTVText)!!
        val orderMoreIV = itemView.findViewById<ImageView>(R.id.orderMoreIV)!!
        val orderStatusRL = itemView.findViewById<RelativeLayout>(R.id.orderStatusRL)!!
        val orderCodeTV = itemView.findViewById<TextView>(R.id.orderCodeTV)!!
        val orderCodeTVText = itemView.findViewById<TextView>(R.id.orderCodeTVText)!!
        val orderMenu = itemView.findViewById<ImageView>(R.id.orderMenuIV)!!
    }

}