package com.smartlife_solutions.android.navara_store

import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.smartlife_solutions.android.navara_store.Adapters.AllOffersAdapter
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.OfferBasicModel
import kotlinx.android.synthetic.main.activity_offers.*
import java.util.*

class ProjectsActivity : AppCompatActivity() {

    private lateinit var myFont: Typeface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_projects)
        myFont = StaticInformation().myFont(this)!!

    }
}