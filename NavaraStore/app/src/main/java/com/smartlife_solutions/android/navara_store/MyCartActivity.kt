package com.smartlife_solutions.android.navara_store

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.ItemBasicModel
import android.view.View
import android.view.animation.AnimationUtils
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import kotlinx.android.synthetic.main.activity_my_cart.*
import org.json.JSONArray

class MyCartActivity : AppCompatActivity() {

    private var items = ArrayList<ItemBasicModel>()
    private var oldScrollY = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_cart)

        cartRV.setHasFixedSize(true)
        cartRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        cartEmptyTV.typeface = StaticInformation().myFont(this)
        cartEmptyTV.text = Statics.getLanguageJSONObject(this).getJSONObject("profileCartOrdersActivity").getString("cartEmpty")

        cartTitleTV.typeface = StaticInformation().myFont(this)
        cartTitleTV.text = Statics.getLanguageJSONObject(this).getJSONObject("profileCartOrdersActivity").getString("myCart")

        orderCategoryIV.setOnClickListener {
            finish()
            startActivity(Intent(this, ItemsActivity::class.java)
                    .putExtra("fromCart", true))
        }

        orderMakeIV.setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java)
                    .putExtra("order_cart", true))
        }

        offerSV.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = offerSV.scrollY
            if (scrollY > oldScrollY) {
                hideOnScroll()
            } else {
                visibleOnScroll()
            }
            oldScrollY = scrollY
        }

        cartBackIV.setOnClickListener {
            onBackPressed()
        }

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.cartFL, LoadingFragment())
        ft.commit()

        getCart()

    }

    private fun getCart() {

        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, APIsURL().GET_CART, null, {
            setupCarts(it.getJSONArray("items"))
        }, {
            try {
                Log.e("error", it.networkResponse.statusCode.toString())
            } catch (err: Exception) {}
            queue.cancelAll("cart")
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=UTF-8"
                params["Authorization"] = "Bearer ${Statics.myToken}"
                return params
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

        }
        jsonObjectRequest.tag = "cart"
        queue.add(jsonObjectRequest)
    }

    private fun setupCarts(itemsJSON: JSONArray) {
        Log.e("cart items", itemsJSON.toString())
        items.clear()
        cartFL.visibility = View.GONE
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
            cartEmptyTV.visibility = View.VISIBLE
        } else {
            cartEmptyTV.visibility = View.GONE
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
                offersViewLL.addView(OfferPackageView(null, this, itemItems, ArrayList(), true, true, lang = Statics.getLanguageJSONObject(this)).view)
            } else {
                offersViewLL.addView(OfferPackageView(null, this, itemItems, itemOffers, true, lang = Statics.getLanguageJSONObject(this)).view)
            }
        }

        for (item in items) {
            offersViewLL.addView(ItemView(null, this, item, true, lang = Statics.getLanguageJSONObject(this)).view)
        }
    }

    private fun hideOnScroll() {
        if (orderCategoryIV.visibility == View.VISIBLE) {
            orderCategoryIV.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_down))
            orderCategoryIV.visibility = View.GONE

            orderMakeIV.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_down))
            orderMakeIV.visibility = View.GONE
        }
    }

    private fun visibleOnScroll() {
        if (orderCategoryIV.visibility == View.GONE) {
            orderCategoryIV.visibility = View.VISIBLE
            orderCategoryIV.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_up))

            orderMakeIV.visibility = View.VISIBLE
            orderMakeIV.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_up))
        }
    }

}
