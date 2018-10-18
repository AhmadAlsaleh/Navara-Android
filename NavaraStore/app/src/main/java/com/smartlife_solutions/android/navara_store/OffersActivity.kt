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

class OffersActivity : AppCompatActivity() {

    private lateinit var myFont: Typeface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offers)
        myFont = StaticInformation().myFont(this)!!

        latestOfferTitle.typeface = myFont
        offersBackIV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(this))
            onBackPressed()
        }
        val offersArrayList: ArrayList<OfferBasicModel> = DatabaseHelper(this)
                .offerBasicModelIntegerRuntimeException.queryForAll() as ArrayList<OfferBasicModel>

        offersRV.setHasFixedSize(true)
        offersRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        offersRV.adapter = AllOffersAdapter(this, offersArrayList)

        itemsOfflineTV.typeface = myFont
        checkConnection()
        checkTimer()

    }

    private fun checkTimer() {
        Handler().postDelayed({
            checkConnection()
        }, StaticInformation().CHECK_INTERNET)
    }

    private fun checkConnection() {
        try {
            if (StaticInformation().isConnected(this)) {
                itemsOfflineTV.visibility = View.GONE
            } else {
                itemsOfflineTV.visibility = View.VISIBLE
            }
            checkTimer()
        } catch (err: Exception) {}
    }

}
