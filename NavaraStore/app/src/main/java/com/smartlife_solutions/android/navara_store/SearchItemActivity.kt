package com.smartlife_solutions.android.navara_store

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.smartlife_solutions.android.navara_store.Adapters.PreviewFreeItemsAdapter
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.ItemBasicModel
import kotlinx.android.synthetic.main.activity_search_item.*

class SearchItemActivity : AppCompatActivity() {

    private var items = ArrayList<ItemBasicModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_item)

        searchBackIV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(this))
            onBackPressed()
        }

        items = DatabaseHelper(this).itemBasicModelIntegerRuntimeException
                .queryForAll() as ArrayList<ItemBasicModel>
        setupSearch()

        itemsOfflineTV.typeface = StaticInformation().myFont(this)
        checkConnection()
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
        } catch (err: Exception) {}
    }


    private fun setupSearch() {
        searchRV.setHasFixedSize(true)
        searchRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        setSearchItems("")

        searchWordET.typeface = StaticInformation().myFont(this)
        searchWordET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setSearchItems(s.toString())
            }

        })
    }

    fun setSearchItems(searchWord: String) {
        val subItems = ArrayList<ItemBasicModel>()
        for (item in items) {
            if (item.name.contains(searchWord, ignoreCase = true)) {
                subItems.add(item)
            }
        }
        searchRV.adapter = PreviewFreeItemsAdapter(context = this, itemsArrayList = subItems, isAll = true)
    }

    override fun onBackPressed() {
        if (searchWordET.length() == 0) {
            super.onBackPressed()
        } else {
            searchWordET.setText("")
        }
    }
}
