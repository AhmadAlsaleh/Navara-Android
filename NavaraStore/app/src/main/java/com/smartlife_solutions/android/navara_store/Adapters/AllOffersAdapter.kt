package com.smartlife_solutions.android.navara_store.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.smartlife_solutions.android.navara_store.*
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.OfferBasicModel
import com.smartlife_solutions.android.navara_store.Dialogs.ChooseQuantityDialog
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AllOffersAdapter(var context: Context, private var offersArrayList: ArrayList<OfferBasicModel>):
        RecyclerView.Adapter<AllOffersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(p0.context)
                    .inflate(R.layout.item_offer, p0, false))

    override fun getItemCount(): Int = offersArrayList.size

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myFont = StaticInformation().myFont(context)
        // region font
        holder.offerTitleTV.typeface = myFont
        holder.offerDiscountTV.typeface = myFont
        holder.offerPercentTV.typeface = myFont
        holder.offerDescriptionTV.typeface = myFont
        // endregion
        Picasso.with(context)
                .load(APIsURL().BASE_URL + offersArrayList[position].thumbnailImagePath)
                .placeholder(R.drawable.no_image)
                .into(holder.offerIV)

        holder.offerItemLL.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            if ("free" == offersArrayList[position].offerType.toLowerCase()) {
                context.startActivity(Intent(context, OfferFreePreviewActivity::class.java).putExtra("id", offersArrayList[position].id))
            } else {
                context.startActivity(Intent(context, OfferPreviewActivity::class.java).putExtra("id", offersArrayList[position].id))
            }
        }
        holder.offerTitleTV.text = offersArrayList[position].title
        holder.offerDescriptionTV.text = offersArrayList[position].shortDescription
        if ("free" == offersArrayList[position].offerType.toLowerCase()) {
            holder.offerPercentTV.visibility = View.GONE
            holder.offerGiftIV.visibility = View.VISIBLE
            holder.offerDiscountTV.setTextColor(context.getColor(R.color.navaraPrimary))
            holder.offerDiscountTV.text = "free"
        } else {
            holder.offerPercentTV.visibility = View.VISIBLE
            holder.offerGiftIV.visibility = View.GONE
            holder.offerDiscountTV.setTextColor(context.getColor(R.color.green_background))
            holder.offerDiscountTV.text = "-${offersArrayList[position].discount}%"
        }

        holder.offerCartIV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))

            try {
                if (DatabaseHelper(context).userModelIntegerRuntimeException.queryForAll()[0].token.isNotEmpty()) {
                    ChooseQuantityDialog(context, offersArrayList[position], true).show()
                } else {
                    context.startActivity(Intent(context, LoginRegisterActivity::class.java).putExtra("main", false))
                }
            } catch (err: Exception) {
                context.startActivity(Intent(context, LoginRegisterActivity::class.java).putExtra("main", false))
            }

        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val offerItemLL = itemView.findViewById<LinearLayout>(R.id.offerItemLL)!!
        val offerGiftIV = itemView.findViewById<ImageView>(R.id.offerGiftIV)!!
        val offerIV = itemView.findViewById<CircleImageView>(R.id.offerItemIV)!!
        val offerTitleTV = itemView.findViewById<TextView>(R.id.offerItemTitleTV)!!
        val offerDescriptionTV = itemView.findViewById<TextView>(R.id.offerItemDescriptionTV)!!
        val offerDiscountTV = itemView.findViewById<TextView>(R.id.offerDiscountTV)!!
        val offerPercentTV = itemView.findViewById<TextView>(R.id.offerPercentTV)!!
        val offerCartIV = itemView.findViewById<ImageView>(R.id.offerCartIV)!!
    }

}