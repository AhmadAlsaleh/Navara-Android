package com.smartlife_solutions.android.navara_store.ItemsActivityFragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smartlife_solutions.android.navara_store.Adapters.CategoriesGridAdapter
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.CategoryDatabaseModel
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.ItemsActivity
import com.smartlife_solutions.android.navara_store.R

class CategoryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_category, container, false)

        try {
            val categoriesArrayList = DatabaseHelper(context)
                    .categoryModelIntegerRuntimeException.queryForAll() as ArrayList<CategoryDatabaseModel>

            val categoriesGrid = view.findViewById<RecyclerView>(R.id.categoriesGridRV)
            categoriesGrid.setHasFixedSize(true)
            categoriesGrid.layoutManager = GridLayoutManager(context, 3)
            categoriesGrid.adapter = CategoriesGridAdapter(activity as ItemsActivity, categoriesArrayList)
        } catch (err: Exception) {
            (activity as ItemsActivity).onBackPressed()
        }

        return view
    }


}
