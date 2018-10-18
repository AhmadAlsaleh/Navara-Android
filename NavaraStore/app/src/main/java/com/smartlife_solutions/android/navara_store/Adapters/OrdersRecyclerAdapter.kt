package com.smartlife_solutions.android.navara_store.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.smartlife_solutions.android.navara_store.Models.OrderModal
import com.smartlife_solutions.android.navara_store.OrderInformationActivity
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation

class OrdersRecyclerAdapter(var context: Context, var orders: ArrayList<OrderModal>)
    : RecyclerView.Adapter<OrdersRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(p0.context)
                    .inflate(R.layout.item_order, p0, false))

    override fun getItemCount(): Int = orders.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        val myFont = StaticInformation().myFont(context)
        // region font
        holder.orderDate.typeface = myFont
        holder.orderTotalPrice.typeface = myFont
        holder.orderTotalTV.typeface = myFont
        holder.orderCodeTV.typeface = myFont
        holder.orderCodeTVText.typeface = myFont
        // endregion

        holder.orderTotalPrice.text = StaticInformation().formatPrice(order.totalPrice.toInt()) + " " + order.currencyCode
        holder.orderDate.text = order.date
        holder.orderCodeTV.text = order.code
        holder.orderLL.setOnClickListener {
//            it.startAnimation(StaticInformation().clickAnim(context))
            context.startActivity(Intent(context, OrderInformationActivity::class.java)
                    .putExtra("id_order", order.id))
        }

        if (order.status == 0) {
            holder.orderMoreIV.setImageResource(R.drawable.ic_hourglass)
            holder.orderStatusRL.setBackgroundResource(R.drawable.ic_circle_orange)
        } else {
            holder.orderMoreIV.setImageResource(R.drawable.ic_check_white)
            holder.orderStatusRL.setBackgroundResource(R.drawable.ic_circle_green)
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val orderLL = itemView.findViewById<LinearLayout>(R.id.orderLL)!!
        val orderTotalTV = itemView.findViewById<TextView>(R.id.orderTotalTV)!!
        val orderTotalPrice = itemView.findViewById<TextView>(R.id.orderTotalPrice)!!
        val orderDate = itemView.findViewById<TextView>(R.id.orderDateTV)!!
        val orderMoreIV = itemView.findViewById<ImageView>(R.id.orderMoreIV)!!
        val orderStatusRL = itemView.findViewById<RelativeLayout>(R.id.orderStatusRL)!!
        val orderCodeTV = itemView.findViewById<TextView>(R.id.orderCodeTV)!!
        val orderCodeTVText = itemView.findViewById<TextView>(R.id.orderCodeTVText)!!
    }

}