package com.smartlife_solutions.android.navara_store.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.Dialogs.FilterItemsDialog
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation
import com.squareup.picasso.Picasso

class FilterSpinnerAdapter(private val context: Context, var filterItemsDialog: FilterItemsDialog): BaseAdapter() {

    val inflater: LayoutInflater = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?)!!

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = inflater.inflate(R.layout.item_categories_select_card, null)
        }

        val category = filterItemsDialog.categories[position]
        val title = view?.findViewById<TextView>(R.id.categoryTV)
        val image = view?.findViewById<ImageView>(R.id.categoryIV)
        val checkBox = view?.findViewById<CheckBox>(R.id.categoryCB)

        Picasso.with(context)
                .load(APIsURL().BASE_URL + category.imagePath)
                .placeholder(R.drawable.no_image)
                .into(image)

        title?.text = category.name
        title?.typeface = StaticInformation().myFont(context)

        checkBox?.isChecked = category.isSelected
        checkBox?.setOnCheckedChangeListener { _, isChecked ->
            category.isSelected = isChecked
            filterItemsDialog.categories[position].isSelected = isChecked
        }

        return view!!
    }

    override fun getItem(position: Int) = filterItemsDialog.categories[position]!!

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = filterItemsDialog.categories.size

}