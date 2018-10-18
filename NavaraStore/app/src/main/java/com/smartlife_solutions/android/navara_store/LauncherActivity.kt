package com.smartlife_solutions.android.navara_store

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.*

class LauncherActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var requestQueue: RequestQueue
    private var tryTime = 10
    private val maxTryTime = 7

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        dbHelper = DatabaseHelper(this)
        requestQueue = Volley.newRequestQueue(this)

        if (StaticInformation().isConnected(this)) {
            getBasicItems()
        } else {
            waitAndGo()
        }

    }

    // region get items basic
    private fun getBasicItems() {
        val jsonArrayRequest = JsonArrayRequest(APIsURL().BASIC_ITEMS, {
            saveItemsInLocal(Gson().fromJson(it.toString(), Array<ItemBasicModel>::class.java).toList())
            requestQueue.cancelAll("item")
        }, {
//            tryTime++
            if (tryTime > maxTryTime) {
                getBasicItems()
            } else {
                waitAndGo()
            }
            requestQueue.cancelAll("item")
            Log.e("error", it.toString())
        })
        jsonArrayRequest.tag = "item"
        requestQueue.add(jsonArrayRequest)

    }

    private fun saveItemsInLocal(listBasicItems: List<ItemBasicModel>) {
        dbHelper.clearTable(ItemBasicModel::class.java)
        dbHelper.itemBasicModelIntegerRuntimeException.create(listBasicItems)
        getCategories()
    }
    // endregion

    // region offers
    private fun getOffers() {
        val jsonArrayRequest = JsonArrayRequest(APIsURL().OFFERS_GET, {
            Log.e("offers", it.toString())
            saveOffersInLocal(Gson().fromJson(it.toString(), Array<OfferBasicModel>::class.java).toList())
            requestQueue.cancelAll("offer")
        }, {
//            tryTime++
            if (tryTime > maxTryTime) {
                getOffers()
            } else {
                waitAndGo()
            }
            requestQueue.cancelAll("offer")
            Log.e("offers error", it.toString())
        })
        jsonArrayRequest.tag = "offer"
        requestQueue.add(jsonArrayRequest)
    }

    private fun saveOffersInLocal(listOffers: List<OfferBasicModel>) {
        dbHelper.clearTable(OfferBasicModel::class.java)
        dbHelper.offerBasicModelIntegerRuntimeException.create(listOffers)
        waitAndGo()
    }

    // endregion

    // region category
    private fun getCategories() {
        val jsonArrayRequest = JsonArrayRequest(APIsURL().CATEGORY_GET, {
            saveCategoriesInLocal(Gson().fromJson(it.toString(), Array<CategoryDatabaseModel>::class.java).toList())
            requestQueue.cancelAll("category")
        }, {
//            tryTime++
            if (tryTime > maxTryTime) {
                getCategories()
            } else {
                waitAndGo()
            }
            requestQueue.cancelAll("category")
            Log.e("error", it.toString())
        })
        jsonArrayRequest.tag = "category"
        requestQueue.add(jsonArrayRequest)
    }

    private fun saveCategoriesInLocal(listCategory: List<CategoryDatabaseModel>) {
        dbHelper.clearTable(CategoryDatabaseModel::class.java)
        dbHelper.categoryModelIntegerRuntimeException.create(listCategory)
        getOffers()
    }
    // endregion

    private fun waitAndGo(mSec: Long = 1000) {
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        }, mSec)
    }

}
