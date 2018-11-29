package com.smartlife_solutions.android.navara_store.ProfileCartOrderFragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.smartlife_solutions.android.navara_store.*
import com.smartlife_solutions.android.navara_store.*
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.ItemBasicModel

import org.json.JSONArray

class CartFragment : Fragment() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }
/*
    private fun setupCarts(itemsJSON: JSONArray) {
        Log.e("cart items", itemsJSON.toString())
        items.clear()
        offersViewLL.removeAllViewsInLayout()
        val itemsHash = HashMap<String, ItemBasicModel>()
        val offersHash = ArrayList<ItemBasicModel>()

        try {
            for (i in 0 until itemsJSON.length()) {
                val item = itemsJSON.getJSONObject(i)
                val itemObject = ItemBasicModel(item.getString("itemID"), item.getString("itemName"),
                        "", "",
                        item.get("quantity") as Int, (item.get("unitNetPrice") as Double).toFloat(),
                        item.getString("itemThumbnail"), item.get("cashBack").toString(),
                        item.getString("accountID"), 0)
//                itemObject.name2 = item.getString("itemName2")
                itemObject.isFree = item.getBoolean("isFree")
                itemObject.discount = item.getInt("unitDiscount")
                itemObject.offerID = item.getString("offerID")

                when {
                    itemObject.isFree -> offersHash.add(itemObject)
                    itemObject.offerID.isNotEmpty() -> itemsHash[itemObject.offerID] = itemObject
                    else -> items.add(itemObject)
                }
            }
        } catch (err: Exception) {
            Log.e("error parse", err.message)
        }
        if (items.isEmpty() && itemsHash.isEmpty()) {
            cartEmpty.visibility = View.VISIBLE
        } else {
            cartEmpty.visibility = View.GONE
        }
        
        for (itemOfferID in itemsHash.keys) {
            val itemItems = ArrayList<ItemBasicModel>()
            val itemOffers = ArrayList<ItemBasicModel>()
            itemItems.add(itemsHash[itemOfferID]!!)

            for (offerItems in offersHash) {
                if (itemOfferID == offerItems.offerID) {
                    itemOffers.add(offerItems)
                }
            }

            if (itemItems[0].discount != 0) {
                offersViewLL.addView(OfferPackageView(this, context!!, itemItems, ArrayList(), true, true, lang = Statics.getLanguageJSONObject(activity as ProfileCartOrders)).view)
            } else {
                offersViewLL.addView(OfferPackageView(this, context!!, itemItems, itemOffers, true, lang = Statics.getLanguageJSONObject(activity as ProfileCartOrders)).view)
            }
        }

        for (item in items) {
            offersViewLL.addView(ItemView(this, context!!, item, true, lang = Statics.getLanguageJSONObject(activity as ProfileCartOrders)).view)
        }
    }

    private fun hideOnScroll() {
        if (categoryIV.visibility == View.VISIBLE) {
            categoryIV.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_down))
            categoryIV.visibility = View.GONE

            makeOrderIV.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_down))
            makeOrderIV.visibility = View.GONE
        }
    }

    private fun visibleOnScroll() {
        if (categoryIV.visibility == View.GONE) {
            categoryIV.visibility = View.VISIBLE
            categoryIV.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_up))

            makeOrderIV.visibility = View.VISIBLE
            makeOrderIV.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_up))
        }
    }
    */

}
