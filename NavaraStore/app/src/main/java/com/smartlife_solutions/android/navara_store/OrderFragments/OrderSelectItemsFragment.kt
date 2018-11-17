package com.smartlife_solutions.android.navara_store.OrderFragments


import android.annotation.SuppressLint
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.smartlife_solutions.android.navara_store.*
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.ItemBasicModel
import com.smartlife_solutions.android.navara_store.ItemView
import com.smartlife_solutions.android.navara_store.OfferPackageView
import com.smartlife_solutions.android.navara_store.OrdersActivity
import com.smartlife_solutions.android.navara_store.StaticInformation

@SuppressLint("ValidFragment")
class OrderSelectItemsFragment (var activity: OrdersActivity) : Fragment() {

    private lateinit var emptyCart: TextView
    private lateinit var selectItemsRV: RecyclerView
    lateinit var selectItemsLL: LinearLayout
    private lateinit var selectCategoryIV: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_select_items, container, false)

        val lang = Statics.getLanguageJSONObject(activity).getJSONObject("makeOrderActivity").getJSONObject("cartFragment")
        val titleItems = view.findViewById<TextView>(R.id.selectItemTitle)
        titleItems.typeface = StaticInformation().myFont(context) // title font
        titleItems.text = lang.getString("title")

        emptyCart = view.findViewById(R.id.cartEmptyTV)
        emptyCart.typeface = StaticInformation().myFont(context)
        emptyCart.text = lang.getString("emptyCart")

        selectItemsLL = view.findViewById(R.id.selectItemsLL)
        selectItemsRV = view.findViewById(R.id.selectItemsRV)
        selectItemsRV.setHasFixedSize(true)
        selectItemsRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        selectCategoryIV = view.findViewById(R.id.selectItemsCategoryIV)
        selectCategoryIV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            activity.finish()
            startActivity(Intent(context, ItemsActivity::class.java)
                    .putExtra("fromOrder", true))
        }

        setItems()

        return view
    }

    fun setItems() {
        if (activity.items.isEmpty() && activity.itemsHash.isEmpty()) {
            emptyCart.visibility = View.VISIBLE
            selectCategoryIV.visibility = View.VISIBLE
        } else {
            emptyCart.visibility = View.GONE
            selectCategoryIV.visibility = View.GONE
        }

        for (itemOfferID in activity.itemsHash.keys) {
            val itemItems = ArrayList<ItemBasicModel>()
            val itemOffers = ArrayList<ItemBasicModel>()
            itemItems.add(activity.itemsHash[itemOfferID]!!)

            for (offerItems in activity.offersHash) {
                if (activity.itemsHash[itemOfferID]!!.offerID == offerItems.offerID) {
                    itemOffers.add(offerItems)
                }
            }

            if (itemItems[0].discount != 0) {
                selectItemsLL.addView(OfferPackageView(this, context!!, itemItems, ArrayList(), false, true, lang = Statics.getLanguageJSONObject(activity)).view)
            } else {
                selectItemsLL.addView(OfferPackageView(this, context!!, itemItems, itemOffers, false, lang = Statics.getLanguageJSONObject(activity)).view)
            }
        }

        for (item in activity.items) {
            selectItemsLL.addView(ItemView(this, context!!, item, false, lang = Statics.getLanguageJSONObject(activity)).view)
        }

    }

}
