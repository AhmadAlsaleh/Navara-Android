package com.smartlife_solutions.android.navara_store

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.smartlife_solutions.android.navara_store.Adapters.PreviewFreeItemsAdapter
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.ItemBasicModel
import kotlinx.android.synthetic.main.activity_search_item.*
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*

class SearchItemActivity : AppCompatActivity() {

    private var items = ArrayList<ItemBasicModel>()
    private var handler = Handler()
    private var runSearch = Runnable {
        sendSearchWord()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_item)

        searchBackIV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(this))
            onBackPressed()
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

        items = DatabaseHelper(this).itemBasicModelIntegerRuntimeException
                .queryBuilder()
                .orderBy("name", true)
                .query() as ArrayList<ItemBasicModel>

        setupSearch()

        itemsOfflineTV.typeface = StaticInformation().myFont(this)
        itemsOfflineTV.text = Statics.getLanguageJSONObject(this).getString("offline")
        checkConnection()
        Handler().postDelayed({
            checkConnection()
        }, StaticInformation().CHECK_INTERNET)

    }

    private fun countDownSearch() {
        try {
            handler.removeCallbacks(runSearch)
            handler.postDelayed(runSearch, 2000)
        } catch (err: Exception) {}
    }

    private fun checkConnection() {
        try {
            if (StaticInformation().isConnected(this)) {
                itemsOfflineTV.visibility = View.GONE
            } else {
                itemsOfflineTV.visibility = View.VISIBLE
            }
        } catch (err: Exception) {}
    }

    private fun setupSearch() {
        searchRV.setHasFixedSize(true)
        searchRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        setSearchItems("")

        searchWordET.typeface = StaticInformation().myFont(this)
        searchWordET.hint = Statics.getLanguageJSONObject(this).getJSONObject("itemsActivity").getString("searchHint")
        searchWordET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setSearchItems(s.toString())
                countDownSearch()
            }
        })

        searchWordET.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                sendSearchWord()
                StaticInformation().hideKeyboard(this)
                true
            } else {
                false
            }
        }
    }

    private fun sendSearchWord() {
        if (searchWordET.text.toString().isEmpty()) {
            return
        }
        val searchWord = JSONObject()
        searchWord.put("SearchText", searchWordET.text.toString())
        val queue = Volley.newRequestQueue(this)
        val sendSearchRequest = object : StringRequest(Request.Method.POST, APIsURL().SEARCH, {
            Log.e("search", it.toString())
            queue.cancelAll("search")
        }, {
            Log.e("search", it.toString())
            queue.cancelAll("search")
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray? {
                return try {
                    searchWord.toString().toByteArray(Charset.forName("utf-8"))
                } catch (err: UnsupportedEncodingException) {
                    null
                }
            }

            override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
                var responseString = ""
                if (response != null) {
                    responseString = response.statusCode.toString()
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response))
            }

        }
        sendSearchRequest.tag = "search"
        queue.add(sendSearchRequest)
    }

    private fun setSearchItems(searchWord: String) {
        val subItems = ArrayList<ItemBasicModel>()
        for (item in items) {
            if (item.name.contains(searchWord, ignoreCase = true)) {
                subItems.add(item)
            }
        }
        searchRV.adapter = PreviewFreeItemsAdapter(context = this, itemsArrayList = subItems, isAll = true, lang = Statics.getLanguageJSONObject(this))
    }

    override fun onBackPressed() {
        if (searchWordET.length() == 0) {
            super.onBackPressed()
        } else {
            searchWordET.setText("")
        }
    }
}
