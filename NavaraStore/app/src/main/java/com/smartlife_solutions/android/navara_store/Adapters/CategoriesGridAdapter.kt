package com.smartlife_solutions.android.navara_store.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.CategoryDatabaseModel
import com.smartlife_solutions.android.navara_store.ItemsActivity
import com.smartlife_solutions.android.navara_store.ItemsActivityFragments.CategoryItemsFragment
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CategoriesGridAdapter(private var activity: ItemsActivity,
                            private var categoriesArrayList: ArrayList<CategoryDatabaseModel>):
        RecyclerView.Adapter<CategoriesGridAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(p0.context)
                    .inflate(R.layout.item_categoy, p0, false))

    override fun getItemCount(): Int = categoriesArrayList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentCategory = categoriesArrayList[position]
        holder.categoryTitle.text = currentCategory.name
        holder.categoryTitle.typeface = StaticInformation().myFont(activity.applicationContext)
        Picasso.with(activity.applicationContext)
                .load(APIsURL().BASE_URL + currentCategory.imagePath)
                .placeholder(R.drawable.no_image)
                .into(holder.categoryIcon)

        holder.categoryLayout.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(activity.applicationContext))
            activity.setupFragment(CategoryItemsFragment(currentCategory.id), 1, currentCategory.name)
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryTitle = itemView.findViewById<TextView>(R.id.categoryTV)!!
        val categoryIcon = itemView.findViewById<CircleImageView>(R.id.categoryIV)!!
        val categoryLayout = itemView.findViewById<LinearLayout>(R.id.categoryLL)!!
    }
}