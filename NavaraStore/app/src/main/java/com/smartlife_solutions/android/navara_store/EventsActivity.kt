package com.smartlife_solutions.android.navara_store

import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.smartlife_solutions.android.navara_store.Adapters.EventsAdapter
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.EventsBasicModel
import kotlinx.android.synthetic.main.activity_events.*
import kotlin.collections.ArrayList

class EventsActivity : AppCompatActivity() {

    private lateinit var myFont: Typeface
    private val events = ArrayList<EventsBasicModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)
        myFont = StaticInformation().myFont(this)!!


        eventsTitleTV.typeface = myFont
        noEventsTV.typeface = myFont

        val lang = Statics.getLanguageJSONObject(this).getJSONObject("moreFeaturesActivity")
        eventsTitleTV.text = lang.getString("events")
        noEventsTV.text = lang.getJSONObject("eventsActivity").getString("noEvents")

        eventsBackIV.setOnClickListener {
            onBackPressed()
        }

        if (StaticInformation().isConnected(this)) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.eventsFL, LoadingFragment())
            ft.commit()
            getEvents()
        } else {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.eventsFL, NoInternetFragment(Statics.getLanguageJSONObject(this).getString("noConnection")))
            ft.commit()
        }

    }


    private fun getEvents() {
        val queue = Volley.newRequestQueue(this)
        val request = JsonArrayRequest(APIsURL().EVENTS, {
            queue.cancelAll("courses")
            events.clear()
            for (i in 0 until it.length()) {
                val eventJSON = it.getJSONObject(i)
                val eventObject = EventsBasicModel("", eventJSON.getString("title"),
                        eventJSON.getString("description"), eventJSON.getString("organiztaionName"),
                        eventJSON.getString("startDate"),
                        eventJSON.getString("imagePath"),
                        eventJSON.getString("sessionsNumber"),
                        eventJSON.getString("contactName"),
                        eventJSON.getString("contactNumber"))
                events.add(eventObject)
            }

            setEvents()

        }, {
            queue.cancelAll("courses")
            Log.e("events error", it.toString())
            getEvents()
        })

        request.tag = "courses"
        queue.add(request)

    }

    private fun setEvents() {
        eventsFL.visibility = View.GONE

        if (events.isNotEmpty()) {
            noEventsTV.visibility = View.GONE
            eventsRV.setHasFixedSize(true)
            eventsRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            eventsRV.adapter = EventsAdapter(this, events, Statics.getLanguageJSONObject(this))
        } else {
            noEventsTV.visibility = View.VISIBLE
        }
    }
}