package com.smartlife_solutions.android.navara_store.ProfileCartOrderFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import com.smartlife_solutions.android.navara_store.*
import com.smartlife_solutions.android.navara_store.Adapters.PreviewFreeItemsAdapter
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.ItemBasicModel
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

@SuppressLint("ValidFragment")
class MyUsedItemsFragment(private val activity: ProfileCartOrders) : Fragment() {

    var isRTL = false
    private lateinit var lang: JSONObject
    private lateinit var myUsedItemsAddBTN: Button
    private lateinit var myUsedItemsNoItemTV: TextView
    private lateinit var myUsedItemsRV: RecyclerView
    var myUsedArrayJSON = JSONArray()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_my_used_items, container, false)
        lang = Statics.getLanguageJSONObject(activity)
        if (isRTL) {
            view.rotationY = 180F
        }

        val myFont = StaticInformation().myFont(activity)
        val langC = lang.getJSONObject("myUsedItemsActivity")
        myUsedItemsNoItemTV = view.findViewById(R.id.myUsedItemsNoItemTV)
        myUsedItemsNoItemTV.typeface = myFont
        myUsedItemsNoItemTV.text = langC.getString("noItems")
        myUsedItemsAddBTN = view.findViewById(R.id.myUsedItemsAddBTN)
        myUsedItemsAddBTN.typeface = myFont
        myUsedItemsAddBTN.text = langC.getString("addUsedItem")

        myUsedItemsAddBTN.setOnClickListener {
            startActivity(Intent(context, AddItemActivity::class.java)
                    .putExtra("name", activity.profileFragment.nameString)
                    .putExtra("countryCode", activity.profileFragment.countryCode)
                    .putExtra("mobile", activity.profileFragment.phoneString))
        }

        myUsedItemsRV = view.findViewById(R.id.myUsedItemsRV)
        myUsedItemsRV.setHasFixedSize(true)
        myUsedItemsRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        myUsedItemsRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) { // hide
                    hideOnScroll()
                } else if (dy < 0) { // show
                    visibleOnScroll()
                }
            }
        })

        setupMyUsedItems(myUsedArrayJSON)

        return view
    }

    private fun hideOnScroll() {
        if (myUsedItemsAddBTN.visibility == View.VISIBLE) {
            myUsedItemsAddBTN.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_down))
            myUsedItemsAddBTN.visibility = View.GONE
        }
    }

    private fun visibleOnScroll() {
        if (myUsedItemsAddBTN.visibility == View.GONE) {
            myUsedItemsAddBTN.visibility = View.VISIBLE
            myUsedItemsAddBTN.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_up))
        }
    }

    fun setupMyUsedItems(myUsedItems: JSONArray) {
        try {
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
                myUsedItemsRV.adapter = PreviewFreeItemsAdapter(context = context!!, itemsArrayList = items, isAll = true, isMyUsedItems = true, lang = Statics.getLanguageJSONObject(activity))
            }
        } catch (err: Exception) {}
    }

}
