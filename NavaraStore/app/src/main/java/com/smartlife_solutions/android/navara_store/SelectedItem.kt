package com.smartlife_solutions.android.navara_store

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.ItemBasicModel
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject

@SuppressLint("SetTextI18n, ResourceAsColor")
class SelectedItem(context: Context, itemModel: ItemBasicModel, isLast: Boolean = false, var lang: JSONObject) {

    val view = View.inflate(context, R.layout.item_items_offers_free, null)!!
    private var langC: JSONObject = lang.getJSONObject("itemsList")

    init {
        val myFont = StaticInformation().myFont(context)

        val itemTitle = view.findViewById<TextView>(R.id.itemTV)
        itemTitle.typeface = myFont
        val itemPriceTitle = view.findViewById<TextView>(R.id.itemPriceTitle)
        itemPriceTitle.typeface = myFont
        itemPriceTitle.text = "${langC.getString("price")} "
        val itemPriceTV = view.findViewById<TextView>(R.id.itemPriceTV)
        itemPriceTV.typeface = myFont

        val itemIV = view.findViewById<CircleImageView>(R.id.itemIV)
        Picasso.with(context)
                .load(APIsURL().BASE_URL + itemModel.thumbnailImagePath)
                .placeholder(R.drawable.no_image)
                .into(itemIV)

        itemTitle.text = itemModel.name
        itemPriceTV.text = "${itemModel.quantity} * ${StaticInformation().formatPrice(itemModel.price)} = " +
                "${StaticInformation().formatPrice(itemModel.quantity * itemModel.price)} ${lang.getString("currencyCode")}"

        if (itemModel.price == 0) {
            itemPriceTV.visibility = View.GONE
            itemPriceTitle.text = langC.getString("free")
            itemPriceTitle.setTextColor(R.color.red_background)
        }

        val itemCashLL: LinearLayout = view.findViewById(R.id.itemItemsCashBackLL)
        val itemCashTV: TextView = view.findViewById(R.id.itemItemsCashBackTV)
        try {
            if (itemModel.cashBack == "null" || itemModel.cashBack.toFloat().toInt() == 0) {
                itemCashLL.visibility = View.GONE
            } else {
                itemCashLL.visibility = View.VISIBLE
                if (Statics.getCurrentLanguageName(null) == Statics.arabic) {
                    itemCashLL.setBackgroundResource(R.drawable.background_cash_back_right_rtl)
                }
                itemCashTV.text = itemModel.cashBack.toFloat().toInt().toString() + " " + lang.getString("currencyCode")
            }
        } catch (err: Exception) {
            itemCashLL.visibility = View.GONE
        }

        if (isLast) {
            view.findViewById<RelativeLayout>(R.id.itemDividerRL).visibility = View.GONE
        }

    }

}