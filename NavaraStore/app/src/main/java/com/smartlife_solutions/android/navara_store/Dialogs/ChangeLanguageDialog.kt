package com.smartlife_solutions.android.navara_store.Dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation
import kotlinx.android.synthetic.main.dialog_change_language.*

class ChangeLanguageDialog(context: Context) : Dialog(context), View.OnClickListener {

    lateinit var myFont: Typeface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_change_language)
        myFont = StaticInformation().myFont(context)!!

        // region font
        changeLanguageTitle.typeface = myFont
        changeLanguageBTN.typeface = myFont
        // endregion

        arabicRB.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                englishRB.isChecked = false
            }
        }

        englishRB.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                arabicRB.isChecked = false
            }
        }

        languageClose.setOnClickListener(this)
        arabicLL.setOnClickListener(this)
        changeLanguageBTN.setOnClickListener(this)
        englishLL.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        v?.startAnimation(StaticInformation().clickAnim(context))
        when (v?.id) {
            R.id.languageClose, R.id.changeLanguageBTN -> dismiss()
            R.id.arabicLL -> arabicClicked()
            R.id.englishLL -> englishClicked()
        }
    }

    private fun englishClicked() {
        englishRB.isChecked = true
    }

    private fun arabicClicked() {
        arabicRB.isChecked = true
        Toast.makeText(context, "Arabic Language will come soon", Toast.LENGTH_LONG).show()
    }
}