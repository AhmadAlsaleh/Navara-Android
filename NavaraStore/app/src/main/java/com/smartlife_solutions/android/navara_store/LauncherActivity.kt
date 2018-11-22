package com.smartlife_solutions.android.navara_store

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.*
import com.google.gson.Gson
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.*
import com.smartlife_solutions.android.navara_store.Dialogs.ChangeLanguageDialog
import com.smartlife_solutions.android.navara_store.Notifications.TimerServiceNotification
import com.smartlife_solutions.android.navara_store.StaticInformation
import java.io.FileOutputStream
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

class LauncherActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var requestQueue: RequestQueue
    private lateinit var perfs: SharedPreferences
    private var isFirst = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        dbHelper = DatabaseHelper(this)
        requestQueue = Volley.newRequestQueue(this)

        perfs = getSharedPreferences("Navara", Context.MODE_PRIVATE)
        isFirst = perfs.getBoolean("isFirst", true)
        if (isFirst) {
            if (StaticInformation().isConnected(this)) {
                Toast.makeText(this, "Preparing Navara Language and Data, Please wait ...", Toast.LENGTH_LONG).show()
                getEnglishFile()
            } else {
                Toast.makeText(this, "Navara Can\'t start without internet at First Time\nCheck Your Connection and Try Again", Toast.LENGTH_LONG).show()
                return
            }
        } else {
            getEnglishFile()
        }
    }

    private fun startRun() {
        if (StaticInformation().isConnected(this)) {
            getBasicItems()
        } else {
            waitAndGo()
        }

        startService(Intent(this, TimerServiceNotification::class.java))

    }

    // region language file

    private fun saveFile(name: String, content: String) {
        Log.e(name, "Saved")
        val perfs = getSharedPreferences("Navara", Context.MODE_PRIVATE).edit()
        perfs.putString(name, content)
        perfs.apply()

    }

    private fun getArabicFile() {
        val queue = Volley.newRequestQueue(this)
        val arabicRequest = JsonObjectRequest(Request.Method.GET, APIsURL().ARABIC_FILE, null, {
            Log.e(Statics.arabic, it.toString())
            try {
                saveFile(Statics.arabic, it.toString())

                if (perfs.getBoolean("isFirst", true)) {
                    val selectLanguage = ChangeLanguageDialog(this,
                            lang = Statics.getLanguageJSONObject(this).getJSONObject("dialogs")
                                    .getJSONObject("changeLanguage"),
                            activity = this, fromMain = false)
                    selectLanguage.show()
                    selectLanguage.setOnDismissListener {
                        startRun()
                    }
                    val e = perfs.edit()
                    e.putBoolean("isFirst", false)
                    e.apply()
                } else {
                    startRun()
                }
            } catch (err: Exception) {
                startRun()
            }

            queue.cancelAll(Statics.arabic)
        }, {
            Log.e(Statics.arabic, it.toString())
//            getArabicFile()
            startRun()
            queue.cancelAll(Statics.arabic)
        })
        arabicRequest.tag = Statics.arabic
        queue.add(arabicRequest)
    }

    private fun getEnglishFile() {
        val queue = Volley.newRequestQueue(this)
        val englishRequest = object : StringRequest(Request.Method.GET, APIsURL().ENGLISH_FILE, {
            try {
                Log.e(Statics.english, it)
                saveFile(Statics.english, it)
            } catch (err: Exception) {
                startRun()
            }
            getArabicFile()
            queue.cancelAll(Statics.english)
        }, {
            Log.e(Statics.english, it.toString())
//            getEnglishFile()
            startRun()
            queue.cancelAll(Statics.english)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
        }
        englishRequest.tag = Statics.english
        queue.add(englishRequest)
    }

    // endregion

    // region get items basic
    private fun getBasicItems() {
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
            getOffers()
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
            getCategories()
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

    @SuppressLint("WrongConstant")
    private fun waitAndGo(mSec: Long = 1000) {
        Statics.myToken = try {
            DatabaseHelper(this).userModelIntegerRuntimeException.queryForAll()[0].token
        } catch (err: Exception) {
            ""
        }
        goToApp(Intent(this, MainActivity::class.java), mSec)
    }

    private fun goToApp(intent: Intent, mSec: Long) {
        Handler().postDelayed({
            startActivity(intent)
            this.finish()
        }, mSec)
    }

}
