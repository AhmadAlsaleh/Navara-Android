package com.smartlife_solutions.android.navara_store.Adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.EventsBasicModel
import com.smartlife_solutions.android.navara_store.EventPreview
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation
import com.squareup.picasso.Picasso

class EventsAdapter(private val context: Context, private val events: ArrayList<EventsBasicModel>)
    : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int)
            = ViewHolder(LayoutInflater.from(p0.context)
                .inflate(R.layout.item_events_card, p0, false))

    override fun getItemCount() = events.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myFont = StaticInformation().myFont(context)
        holder.eventTitleTV.typeface = myFont
        holder.eventDateTV.typeface = myFont
        holder.eventDateTitle.typeface = myFont

        val event = events[position]
        holder.eventTitleTV.text = event.title
        holder.eventDateTV.text = event.startDate
        Picasso.with(context)
                .load(APIsURL().BASE_URL + event.image)
                .into(holder.eventIV)

        holder.eventLL.setOnClickListener {
            context.startActivity(Intent(context, EventPreview::class.java)
                    .putExtra("id", "")
                    .putExtra("info", event))
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val eventLL = itemView.findViewById<LinearLayout>(R.id.eventLL)!!
        val eventTitleTV = itemView.findViewById<TextView>(R.id.eventTV)!!
        val eventDateTitle = itemView.findViewById<TextView>(R.id.eventDateTitle)!!
        val eventDateTV = itemView.findViewById<TextView>(R.id.eventDateTV)!!
        val eventIV = itemView.findViewById<ImageView>(R.id.eventIV)!!

    }

}