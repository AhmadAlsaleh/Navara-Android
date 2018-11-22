package com.smartlife_solutions.android.navara_store

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.ItemBasicModel
import com.smartlife_solutions.android.navara_store.ItemsActivityFragments.CategoryFragment
import kotlinx.android.synthetic.main.activity_items.*
import org.json.JSONObject
import java.util.*

class ItemsActivity : AppCompatActivity() {

    private var fragmentPosition = 0
    private var fromOrder = false
    private var fromCart = false
    private lateinit var lang: JSONObject

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)

        lang = Statics.getLanguageJSONObject(this)

        if (Statics.getCurrentLanguageName(this) == Statics.arabic) {
            val conf = resources.configuration
            conf.setLayoutDirection(Locale("fa"))
            resources.updateConfiguration(conf, resources.displayMetrics)

            itemHintCashPreviewLL.setBackgroundResource(R.drawable.background_cash_back_right_rtl)
            itemHintCashLL.setBackgroundResource(R.drawable.background_cash_back_right_rtl)

        } else {
            val conf = resources.configuration
            conf.setLayoutDirection(Locale("en"))
            resources.updateConfiguration(conf, resources.displayMetrics)
        }

        try {
            fromOrder = intent.getBooleanExtra("fromOrder", false)
            fromCart = intent.getBooleanExtra("fromCart", false)
        } catch (err: Exception) {}

        searchTV.typeface = StaticInformation().myFont(this)
        searchTV.text = lang.getJSONObject("itemsActivity").getString("search")

        itemsBackIV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(this))
            onBackPressed()
        }

        searchLL.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(this))
            startActivity(Intent(this, SearchItemActivity::class.java))
        }

        itemsTitleLayoutTV.typeface = StaticInformation().myFont(this)
        itemsOfflineTV.typeface = StaticInformation().myFont(this)
        itemsOfflineTV.text = lang.getString("offline")

        if (StaticInformation().isConnected(this)) {
            setupFragment(LoadingFragment())
            try {
                getBasicItems()
            } catch (err: Exception) {}
        } else {
            setupFragment(CategoryFragment())
            setHints()
        }

        checkConnection()
        checkTimer()

    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    private fun setHints() {
        val perf = getSharedPreferences("Navara", Context.MODE_PRIVATE)
        val isFirstCategory = perf?.getBoolean("search", true)
        if (isFirstCategory!!) {
            itemsHintLL.visibility = View.VISIBLE

        } else {
            itemsHintLL.visibility = View.GONE
            return
        }

        val font = StaticInformation().myFont(this)
        itemsCatHintTV.typeface = font
        itemsHintTV.typeface = font
        itemsNextBTN.typeface = font

        val lang = Statics.getLanguageJSONObject(this).getJSONObject("hints").getJSONObject("itemsActivity")
        itemsNextBTN.text = lang.getString("next")
        itemsCatHintTV.text = lang.getString("categoriesHint")
        itemsHintTV.text = lang.getString("searchHint")
        var currentHint = 0
        itemsNextBTN.setOnClickListener {
            itemsNextBTN.text = lang.getString("done")
            when (currentHint) {
                0 -> {
                    itemsHintLL.setBackgroundColor(resources.getColor(android.R.color.transparent))
                    itemsHintTV.visibility = View.GONE
                    itemsAllCatsRL.visibility = View.VISIBLE
                    itemsAllCatsRL.startAnimation(StaticInformation().fadeInAnim(this))
                }
                1 -> {
                    val editShared = perf.edit()
                    editShared.putBoolean("search", false)
                    editShared.apply()
                    itemsHintLL.visibility = View.GONE
                }
            }
            currentHint++
        }

    }

    private fun getBasicItems() {
        val requestQueue = Volley.newRequestQueue(this)
        val jsonArrayRequest = JsonArrayRequest(APIsURL().BASIC_ITEMS, {
            saveItemsInLocal(Gson().fromJson(it.toString(), Array<ItemBasicModel>::class.java).toList())
            Log.e("items", it.toString())
            requestQueue.cancelAll("item")
        }, {
            getBasicItems()
            Log.e("error", it.toString())
        })
        jsonArrayRequest.tag = "item"
        requestQueue.add(jsonArrayRequest)

    }

    private fun saveItemsInLocal(listBasicItems: List<ItemBasicModel>) {
        try {
            val dbHelper = DatabaseHelper(this)
            dbHelper.clearTable(ItemBasicModel::class.java)
            dbHelper.itemBasicModelIntegerRuntimeException.create(listBasicItems)
            setupFragment(CategoryFragment())
            setHints()
        } catch (err: Exception) {}
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

    fun setupFragment(frag: Fragment, fragmentPosition: Int = 0, title: String = "") {
        try {
            if (title.isEmpty()) {
                itemsTitleLayoutTV.text = lang.getJSONObject("itemsActivity").getString("categoriesTitle")
            } else {
                itemsTitleLayoutTV.text = title
            }
            this.fragmentPosition = fragmentPosition
            val fragmentTranslate = supportFragmentManager.beginTransaction()
            fragmentTranslate.replace(R.id.itemsFL, frag)
            fragmentTranslate.commit()
        } catch (err: Exception) {}
    }

    override fun onBackPressed() {
        if (fragmentPosition != 0) {
            setupFragment(CategoryFragment())
            return
        }
        if (fromOrder) {
            startActivity(Intent(this, OrdersActivity::class.java))
            return
        }
        if (fromCart) {
            startActivity(
                    Intent(this, ProfileCartOrders::class.java)
                            .putExtra("currentPage", 1)
                            .putExtra(StaticInformation().FINITSH_ON_BACK, true)
            )
        }
        startActivity(Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        super.onBackPressed()
    }

    fun showItemHints() {
        val myFont = StaticInformation().myFont(this)
        itemHintTV.typeface = myFont
        itemHintBTN.typeface = myFont
        itemHintTitleTV.typeface = myFont
        itemHintPriceTV.typeface = myFont
        itemHintCashBackTV.typeface = myFont
        itemHintCashPreviewTV.typeface = myFont

        itemHintRL.visibility = View.VISIBLE
        itemHintCashPreviewLL.visibility = View.GONE
        itemHintCartPreviewIV.visibility = View.VISIBLE

        itemHintCV.startAnimation(StaticInformation().slideHint(this))
        itemHintTV.startAnimation(StaticInformation().fadeInAnim(this))
        itemHintCartPreviewIV.startAnimation(StaticInformation().fadeInAnim(this))

        val lang = Statics.getLanguageJSONObject(this).getJSONObject("hints").getJSONObject("itemsList")
        itemHintTitleTV.text = lang.getString("title")
        itemHintPriceTV.text = lang.getString("price")
        itemHintCashBackTV.text = lang.getString("cashBack")
        itemHintCashPreviewTV.text = lang.getString("cashBack")
        itemHintTV.text = lang.getString("cartText")

        itemHintBTN.text = lang.getString("next")
        var clickTime = true
        itemHintBTN.setOnClickListener {
            if (clickTime) {
                itemHintBTN.text = lang.getString("done")
                itemHintTV.text = lang.getString("cashText")

                itemHintCashPreviewLL.visibility = View.VISIBLE
                itemHintCartPreviewIV.visibility = View.GONE

                itemHintTV.startAnimation(StaticInformation().fadeInAnim(this))
                itemHintCashPreviewLL.startAnimation(StaticInformation().fadeInAnim(this))

                clickTime = false
                return@setOnClickListener
            }
            val perfs = getSharedPreferences("Navara", Context.MODE_PRIVATE)
            val editPerfs = perfs.edit()
            editPerfs.putBoolean("itemHint", false)
            editPerfs.apply()
            itemHintRL.visibility = View.GONE
        }

    }

}
