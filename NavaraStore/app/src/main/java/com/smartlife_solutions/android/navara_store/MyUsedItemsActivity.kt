package com.smartlife_solutions.android.navara_store

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.smartlife_solutions.android.navara_store.Adapters.PreviewFreeItemsAdapter
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.ItemBasicModel
import com.smartlife_solutions.android.navara_store.R.id.myUsedItemsNoItemTV
import kotlinx.android.synthetic.main.activity_my_used_items.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*

class MyUsedItemsActivity : AppCompatActivity() {

    private lateinit var lang: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_used_items)

        lang = Statics.getLanguageJSONObject(this)

        if (Statics.getCurrentLanguageName(this) == Statics.arabic) {
            val conf = resources.configuration
            conf.setLayoutDirection(Locale("fa"))
            resources.updateConfiguration(conf, resources.displayMetrics)
        } else {
            val conf = resources.configuration
            conf.setLayoutDirection(Locale("en"))
            resources.updateConfiguration(conf, resources.displayMetrics)
        }

        if (!StaticInformation().isConnected(this)) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.myUsedItemsFL, NoInternetFragment(lang.getString("noConnection")))
            ft.commit()
            return
        }

        val myFont = StaticInformation().myFont(this)
        val langC = lang.getJSONObject("myUsedItemsActivity")
        myUsedItemsTitle.typeface = myFont
        myUsedItemsTitle.text = langC.getString("title")
        myUsedItemsNoItemTV.typeface = myFont
        myUsedItemsNoItemTV.text = langC.getString("noItems")
        myUsedItemsAddBTN.typeface = myFont
        myUsedItemsAddBTN.text = langC.getString("addUsedItem")

        myUsedItemsBackIV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(this))
            onBackPressed()
        }

        myUsedItemsAddBTN.setOnClickListener {
            startActivity(Intent(this, AddItemActivity::class.java)
                    .putExtra("name", intent.getStringExtra("name"))
                    .putExtra("countryCode", intent.getStringExtra("countryCode"))
                    .putExtra("mobile", intent.getStringExtra("mobile")))
        }

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.myUsedItemsFL, LoadingFragment())
        ft.commit()

        myUsedItemsRV.setHasFixedSize(true)
        myUsedItemsRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    }

    override fun onResume() {
        super.onResume()
        loadMyUsedItems()
    }

    private fun loadMyUsedItems() {
        myUsedItemsFL.visibility = View.VISIBLE
        val queue = Volley.newRequestQueue(this)
        val request = object : StringRequest(Request.Method.GET, APIsURL().GET_OWN_ITEMS, {
            try {
                Log.e("own", it)
                myUsedItemsFL.visibility = View.GONE
                setupMyUsedItems(JSONArray(it))
            } catch (err: Exception) {}
            queue.cancelAll("own")
        }, {
            Log.e("own error", it.toString())
            queue.cancelAll("own")
        }) {
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=UTF-8"
                params["Authorization"] = "Bearer ${Statics.myToken}"
                return params
            }
        }
        request.tag = "own"
        queue.add(request)
    }

    private fun setupMyUsedItems(myUsedItems: JSONArray) {
        if (myUsedItems.length() == 0) {
            myUsedItemsNoItemTV.visibility = View.VISIBLE
        } else {
            val items = ArrayList<ItemBasicModel>()
            for (i in 0 until myUsedItems.length()) {
                val itemObject = myUsedItems.getJSONObject(i)
                val currentItem = ItemBasicModel(
                        itemObject["id"].toString(),
                        itemObject["name"].toString(),
                        "Used Items",
                        itemObject["itemCategoryID"].toString(),
                        itemObject["quantity"].toString().toInt(),
                        itemObject["price"].toString().toFloat(),
                        itemObject["thumbnailImagePath"].toString(),
                        itemObject["cashBack"].toString(),
                        "", 0)
                currentItem.isEnable = itemObject.getBoolean("isEnable")
                items.add(currentItem)
            }
            myUsedItemsNoItemTV.visibility = View.GONE
            myUsedItemsRV.adapter = PreviewFreeItemsAdapter(context = this, itemsArrayList = items, isAll = true, isMyUsedItems = true, lang = Statics.getLanguageJSONObject(this))
        }
    }

}
