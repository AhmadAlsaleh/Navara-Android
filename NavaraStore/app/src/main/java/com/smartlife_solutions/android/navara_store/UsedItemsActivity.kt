package com.smartlife_solutions.android.navara_store

import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.smartlife_solutions.android.navara_store.Adapters.PreviewFreeItemsAdapter
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.ItemBasicModel
import kotlinx.android.synthetic.main.activity_used_items.*
import org.json.JSONObject

class UsedItemsActivity : AppCompatActivity() {

    private var items = ArrayList<ItemBasicModel>()
    private lateinit var myFont: Typeface
    private lateinit var lang: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_used_items)

        lang = Statics.getLanguageJSONObject(this)
        myFont = StaticInformation().myFont(this)!!
        usedItemsTitleTV.typeface = myFont
        usedItemsTitleTV.text = lang.getJSONObject("moreFeaturesActivity").getString("usedItems")
        noUsedItemsTV.typeface = myFont

        usedItemsBackIV.setOnClickListener {
            onBackPressed()
        }

        if (StaticInformation().isConnected(this)) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.usedItemsFL, LoadingFragment())
            ft.commit()
            getItems()
        } else {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.usedItemsFL, NoInternetFragment())
            ft.commit()
        }

    }

    private fun getItems() {
        val queue = Volley.newRequestQueue(this)
        val usedItemsRequest = JsonArrayRequest(APIsURL().GET_USED_ITEMS, {
            queue.cancelAll("used")
            items.clear()
            val itemsList = Gson().fromJson(it.toString(), Array<ItemBasicModel>::class.java).toList()

            for (item in itemsList) {
                items.add(item)
            }

            setItems()

//            for (i in 0 until it.length()) {
//                val itemJSON = it.getJSONObject(i)
//                items.add(ItemBasicModel(itemJSON.getString("id"),
//                        itemJSON.getString("name"),
//                        itemJSON.getString("itemCategory"),
//                        itemJSON.getString("itemCategoryID"),
//                        itemJSON.getInt("quantity"),
//                        (itemJSON.get("price") as Double).toFloat(),
//                        itemJSON.getString("thumbnailImagePath"),
//                        itemJSON.getString("cashBack"),
//                        itemJSON.getString("")))
//            }

        }, {
            queue.cancelAll("used")
            Log.e("Used Items error", it.toString())
            getItems()
        })
        usedItemsRequest.tag = "used"
        queue.add(usedItemsRequest)

    }

    private fun setItems() {
        usedItemsFL.visibility = View.GONE
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
