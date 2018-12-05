package com.smartlife_solutions.android.navara_store.Dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import com.smartlife_solutions.android.navara_store.Adapters.FilterSpinnerAdapter
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.CategoryDatabaseModel
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation
import com.smartlife_solutions.android.navara_store.Statics
import kotlinx.android.synthetic.main.dialog_filter_items.*

class FilterItemsDialog(context: Context): Dialog(context) {

    private lateinit var myFont: Typeface
    var isDone = false
    lateinit var categories: ArrayList<CategoryDatabaseModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_filter_items)

        myFont = StaticInformation().myFont(context)!!
        filterTitle.typeface = myFont
        keyWordsTV.typeface = myFont
        keyWordsET.typeface = myFont
        highPriceTV.typeface = myFont
        highPriceET.typeface = myFont
        filterBtn.typeface = myFont
        filterClose.setOnClickListener {
            isDone = false
            dismiss()
        }

        filterBtn.setOnClickListener {
            isDone = true
            dismiss()
        }

        categories = Statics.getCategoryDatabaseModelArrayList()

        filterSpinner.adapter = FilterSpinnerAdapter(context, this)

    }
}