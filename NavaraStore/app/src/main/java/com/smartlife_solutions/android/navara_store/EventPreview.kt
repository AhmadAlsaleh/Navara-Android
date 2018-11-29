package com.smartlife_solutions.android.navara_store

import android.graphics.Typeface
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.View
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.EventsBasicModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_event_preview.*
import org.json.JSONObject
import java.util.*

class EventPreview : AppCompatActivity() {

    private lateinit var lang: JSONObject
    private lateinit var myFont: Typeface

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_preview)

        lang = Statics.getLanguageJSONObject(this)
        myFont = StaticInformation().myFont(this)!!

        // region setup
        if (!StaticInformation().isConnected(this)) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.courseFL, NoInternetFragment(lang.getString("noConnection")))
            ft.commit()
            return
        }

        if (Statics.getCurrentLanguageName(this) == Statics.arabic) {
            val conf = resources.configuration
            conf.setLayoutDirection(Locale("fa"))
            resources.updateConfiguration(conf, resources.displayMetrics)
        } else {
            val conf = resources.configuration
            conf.setLayoutDirection(Locale("en"))
            resources.updateConfiguration(conf, resources.displayMetrics)
        }

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.courseFL, LoadingFragment())
        ft.commit()

        eventTitleTV.typeface = myFont
        eventStartDateTV.typeface = myFont
        eventSessionsTV.typeface = myFont
        eventDescriptionTitleTV.typeface = myFont
        eventDescriptionTV.typeface = myFont
        eventContactUsTextTV.typeface = myFont
        eventNameTitleTV.typeface = myFont
        eventNameTV.typeface = myFont
        eventMobileTitleTV.typeface = myFont
        eventMobileTV.typeface = myFont
        eventContactUsTV.typeface = myFont

        // endregion

//        getCourse(intent.getStringExtra("id"))

        val event = intent.getSerializableExtra("info") as EventsBasicModel
        eventHeadIV.setOnClickListener {
            onBackPressed()
        }
        Picasso.with(this)
                .load(APIsURL().BASE_URL + event.image)
                .placeholder(R.drawable.navara_logo)
                .into(eventHeadIV)
        eventTitleTV.text = event.title
        eventStartDateTV.text = event.startDate
        eventSessionsTV.text = event.sessionsCount
        eventDescriptionTV.text = event.description
        eventNameTV.text = event.contact

        eventFL.visibility = View.GONE

    }

    private fun getCourse(id: String) {
        val queue = Volley.newRequestQueue(this)
        val request = JsonObjectRequest(Request.Method.GET, "" + id, null, {
            queue.cancelAll("course")
            Log.e("course", it.toString())



        }, {
            Log.e("course error", it.toString())
            queue.cancelAll("course")
            getCourse(id)
        })

        request.tag = "course"
        queue.add(request)
    }

}
