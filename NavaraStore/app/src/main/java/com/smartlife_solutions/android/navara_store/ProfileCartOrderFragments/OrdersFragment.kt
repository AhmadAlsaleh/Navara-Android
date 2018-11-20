package com.smartlife_solutions.android.navara_store.ProfileCartOrderFragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.smartlife_solutions.android.navara_store.Adapters.OrdersRecyclerAdapter
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.Models.OrderModal
import com.smartlife_solutions.android.navara_store.ProfileCartOrders

import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation
import com.smartlife_solutions.android.navara_store.Statics
import org.json.JSONArray

class OrdersFragment : Fragment() {

    var isRTL = false

    private var orders = ArrayList<OrderModal>()
    private lateinit var noOrders: TextView
    private lateinit var ordersRV: RecyclerView

    var orderJSONArray = JSONArray()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_orders, container, false)

        if (isRTL) {
            view.rotationY = 180F
        }

        val myFont = StaticInformation().myFont(context)
        ordersRV = view.findViewById(R.id.ordersRV)
        noOrders = view.findViewById(R.id.noOrderTV)
        noOrders.typeface = myFont
        noOrders.text = Statics.getLanguageJSONObject(activity as ProfileCartOrders).getJSONObject("profileCartOrdersActivity").getString("noOrders")
        noOrders.visibility = View.GONE

        setupOrdersRV(orderJSONArray)

        return view
    }

    private fun setupOrdersRV(it: JSONArray) {
        Log.e("orders", it.toString())
        Log.e("orders count", it.length().toString())
        orders.clear()
        for (i in 0 until it.length()) {
            val orderJson = it.getJSONObject(i)

            val dateString = orderJson.getString("date").split('T')[0].split('-')
            val currentOrder = OrderModal("", orderJson.getString("name"), "",
                    "${dateString[2]}/${dateString[1]}/${dateString[0]}", "", "", 0.0, 0.0,
                    orderJson.getString("status"), orderJson.getInt("netTotalPrices").toFloat(), remark = "",
                    code = orderJson.getString("code")!!,
                    id = orderJson.getString("id"))
            orders.add(currentOrder)
        }

        val ordersList = orders.sortedWith(compareBy {it.date}).reversed()
        orders.clear()
        for (order in ordersList) {
            orders.add(order)
        }

        if (orders.isEmpty()) {
            noOrders.visibility = View.VISIBLE
        } else {
            noOrders.visibility = View.GONE
            ordersRV.setHasFixedSize(true)
            ordersRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            ordersRV.adapter = OrdersRecyclerAdapter(context!!, orders, Statics.getLanguageJSONObject(activity as ProfileCartOrders))
        }
    }


}
