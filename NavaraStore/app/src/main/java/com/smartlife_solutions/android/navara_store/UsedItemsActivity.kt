package com.smartlife_solutions.android.navara_store

import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.smartlife_solutions.android.navara_store.Adapters.PreviewFreeItemsAdapter
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.ItemBasicModel
import kotlinx.android.synthetic.main.activity_used_items.*

class UsedItemsActivity : AppCompatActivity() {

    private val items = ArrayList<ItemBasicModel>()
    private lateinit var myFont: Typeface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_used_items)

        myFont = StaticInformation().myFont(this)!!
        usedItemsTitleTV.typeface = myFont
        noUsedItemsTV.typeface = myFont

        usedItemsBackIV.setOnClickListener {
            onBackPressed()
        }

        setItems()

    }

    private fun setItems() {
        if (items.isNotEmpty()) {
            noUsedItemsTV.visibility = View.GONE
            usedItemsRV.setHasFixedSize(true)
            usedItemsRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            usedItemsRV.adapter = PreviewFreeItemsAdapter(context = this, itemsArrayList = items, isAll = true,
                    lang = Statics.getLanguageJSONObject(this))
        } else {
            noUsedItemsTV.visibility = View.VISIBLE
        }
    }

}
