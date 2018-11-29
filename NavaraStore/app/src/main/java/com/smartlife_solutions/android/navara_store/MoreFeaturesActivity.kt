package com.smartlife_solutions.android.navara_store

import android.content.Intent
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_more_features.*
import kotlinx.android.synthetic.main.item_offer_package.*

class MoreFeaturesActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var myFont: Typeface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_features)

        myFont = StaticInformation().myFont(this)!!
        moreTitleTV.typeface = myFont
        offersTV.typeface = myFont
        projectsTV.typeface = myFont
        eventTV.typeface = myFont
        usedItemsTV.typeface = myFont
        coursesTV.typeface = myFont

        moreBackIV.setOnClickListener(this)
        usedItemsCV.setOnClickListener(this)
        offersCV.setOnClickListener(this)
        eventsCV.setOnClickListener(this)
        coursesCV.setOnClickListener(this)
        projectsCV.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.moreBackIV -> onBackPressed()
            R.id.usedItemsCV -> startActivity(Intent(this, UsedItemsActivity::class.java))
            R.id.offersCV -> startActivity(Intent(this, OffersActivity::class.java))
            R.id.eventsCV -> startActivity(Intent(this, EventsActivity::class.java))
            R.id.coursesCV -> startActivity(Intent(this, CoursesActivity::class.java))
            R.id.projectsCV -> startActivity(Intent(this, ProjectsActivity::class.java))
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}
