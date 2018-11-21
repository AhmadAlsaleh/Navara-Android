package com.smartlife_solutions.android.navara_store.ItemsActivityFragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smartlife_solutions.android.navara_store.Adapters.PreviewFreeItemsAdapter
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.ItemBasicModel
import com.smartlife_solutions.android.navara_store.ItemsActivity
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.Statics
import kotlin.collections.ArrayList

@SuppressLint("ValidFragment")
class CategoryItemsFragment(private var categoryID: String) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_category_items, container, false)

        try {
            val items = DatabaseHelper(context)
                    .itemBasicModelIntegerRuntimeException
                    .queryBuilder()
                    .orderBy("name", true)
                    .where().eq("item_category_id", categoryID).query() as ArrayList<ItemBasicModel>

            val itemsRV = view.findViewById<RecyclerView>(R.id.categoryItemsRV)
            itemsRV.setHasFixedSize(true)
            itemsRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            itemsRV.adapter = PreviewFreeItemsAdapter(context = context!!, itemsArrayList = items, isAll = true,
                    lang = Statics.getLanguageJSONObject(activity as ItemsActivity))

            setHints()

        } catch (err: Exception) {
            (activity as ItemsActivity).onBackPressed()
        }
        return  view
    }

    private fun setHints() {
        val itemsActivity = (activity as ItemsActivity)
        val perfs = itemsActivity.getSharedPreferences("Navara", Context.MODE_PRIVATE)
        if (!perfs.getBoolean("itemHint", true)) {
            return
        }
        val editPerfs = perfs.edit()
        editPerfs.putBoolean("itemHint", false)
        editPerfs.apply()

        itemsActivity.showItemHints()
    }

}
