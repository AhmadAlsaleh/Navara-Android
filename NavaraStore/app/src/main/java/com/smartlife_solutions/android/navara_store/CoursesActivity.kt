package com.smartlife_solutions.android.navara_store

import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.smartlife_solutions.android.navara_store.Adapters.AllOffersAdapter
import com.smartlife_solutions.android.navara_store.Adapters.CoursesAdapter
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.CoursesBasicModel
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.OfferBasicModel
import kotlinx.android.synthetic.main.activity_courses.*
import kotlinx.android.synthetic.main.activity_offers.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class CoursesActivity : AppCompatActivity() {

    private lateinit var myFont: Typeface
    private val courses = ArrayList<CoursesBasicModel>()
    private lateinit var lang: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courses)
        myFont = StaticInformation().myFont(this)!!
        lang = Statics.getLanguageJSONObject(this)

        coursesTitleTV.typeface = myFont
        noCoursesTV.typeface = myFont

        val lang = Statics.getLanguageJSONObject(this).getJSONObject("moreFeaturesActivity")
        coursesTitleTV.text = lang.getString("courses")
        noCoursesTV.text = lang.getJSONObject("coursesActivity").getString("noCourses")

        coursesBackIV.setOnClickListener {
            onBackPressed()
        }

        if (StaticInformation().isConnected(this)) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.coursesFL, LoadingFragment())
            ft.commit()
            getCourses()
        } else {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.coursesFL, NoInternetFragment(Statics.getLanguageJSONObject(this).getString("noConnection")))
            ft.commit()
        }

    }

    private fun getCourses() {
        val queue = Volley.newRequestQueue(this)
        val request = JsonArrayRequest(APIsURL().COURSES, {
            queue.cancelAll("courses")
            courses.clear()
            for (i in 0 until it.length()) {
                val courseJSON = it.getJSONObject(i)
                val courseObject = CoursesBasicModel("", courseJSON.getString("title"),
                        courseJSON.getString("description"), (courseJSON.get("cost") as Double).toFloat(),
                        courseJSON.getString("startDate"),
                        courseJSON.getString("imagePath"),
                        courseJSON.getString("sessionsNumber"),
                        courseJSON.getString("contactName"),
                        courseJSON.getString("contactNumber"))
                courses.add(courseObject)
            }

            setCourses()

        }, {
            queue.cancelAll("courses")
            getCourses()
        })

        request.tag = "courses"
        queue.add(request)

    }

    private fun setCourses() {
        coursesFL.visibility = View.GONE

        if (courses.isNotEmpty()) {
            noCoursesTV.visibility = View.GONE
            coursesRV.setHasFixedSize(true)
            coursesRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            coursesRV.adapter = CoursesAdapter(this, courses, lang)
        } else {
            noCoursesTV.visibility = View.VISIBLE
        }
    }

}