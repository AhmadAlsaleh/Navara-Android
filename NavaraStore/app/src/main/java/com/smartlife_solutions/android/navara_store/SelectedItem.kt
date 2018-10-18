package com.smartlife_solutions.android.navara_store

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.ItemBasicModel
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

@SuppressLint("SetTextI18n")
class SelectedItem(context: Context, itemModel: ItemBasicModel, isLast: Boolean = false) {

    val view = View.inflate(context, R.layout.item_items_offers_free, null)!!

    init {
        val myFont = StaticInformation().myFont(context)
        val itemTitle = view.findViewById<TextView>(R.id.itemTV)
        itemTitle.typeface = myFont
        val itemPriceTitle = view.findViewById<TextView>(R.id.itemPriceTitle)
        itemPriceTitle.typeface = myFont
        itemPriceTitle.text = "Price: "
        val itemPriceTV = view.findViewById<TextView>(R.id.itemPriceTV)
        itemPriceTV.typeface = myFont
        val itemIV = view.findViewById<CircleImageView>(R.id.itemIV)
        Picasso.with(context)
                .load(APIsURL().BASE_URL + itemModel.thumbnailImagePath)
                .placeholder(R.drawable.no_image)
                .into(itemIV)
        Log.e("image", APIsURL().BASE_URL + itemModel.thumbnailImagePath)
        itemTitle.text = itemModel.name
        itemPriceTV.text = "${itemModel.quantity} * ${StaticInformation().formatPrice(itemModel.price)} = " +
                "${StaticInformation().formatPrice(itemModel.quantity * itemModel.price)} ${itemModel.currencyCode}"

        if (isLast) {
            view.findViewById<RelativeLayout>(R.id.itemDividerRL).visibility = View.GONE
        }

    }

}