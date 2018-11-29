package com.smartlife_solutions.android.navara_store

import android.annotation.SuppressLint
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
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.CoursesBasicModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_course_preview.*
import org.json.JSONObject
import java.util.*

class CoursePreview : AppCompatActivity() {

    private lateinit var lang: JSONObject
    private lateinit var langC: JSONObject
    private lateinit var myFont: Typeface

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_preview)

        lang = Statics.getLanguageJSONObject(this)
        langC = lang.getJSONObject("moreFeaturesActivity").getJSONObject("coursesActivity")

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

        courseTitleTV.typeface = myFont
        courseCostTextTV.typeface = myFont
        courseCostTextTV.text = langC.getString("cost")
        courseCostTV.typeface = myFont
        courseStartDateTV.typeface = myFont
        courseSessionsTV.typeface = myFont
        DescriptionTitleTV.typeface = myFont
        DescriptionTitleTV.text = langC.getString("description")
        courseDescriptionTV.typeface = myFont
        courseContactUsTextTV.typeface = myFont
        courseNameTV.typeface = myFont
        courseMobileTitleTV.typeface = myFont
        courseMobileTV.typeface = myFont
        courseContactUsTV.typeface = myFont
        // endregion

//        getCourse(intent.getStringExtra("id"))

        val course = intent.getSerializableExtra("info") as CoursesBasicModel
        courseBackIV.setOnClickListener {
            onBackPressed()
        }
        Picasso.with(this)
                .load(APIsURL().BASE_URL + course.image)
                .placeholder(R.drawable.navara_logo)
                .into(courseHeadIV)
        courseTitleTV.text = course.title
        courseCostTV.text = StaticInformation().formatPrice(course.cost) + " " + lang.getString("currencyCode")
        courseStartDateTV.text = course.startDate
        courseSessionsTV.text = course.sessionsCount
        courseDescriptionTV.text = course.description
        courseNameTV.text = course.contact

        courseFL.visibility = View.GONE

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
