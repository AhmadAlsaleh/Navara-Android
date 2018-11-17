package com.smartlife_solutions.android.navara_store.Dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.smartlife_solutions.android.navara_store.*
import kotlinx.android.synthetic.main.dialog_change_language.*
import org.json.JSONObject

class ChangeLanguageDialog(context: Context, var current: String = Statics.english, var lang: JSONObject, var activity: Activity, var fromMain: Boolean = false) : Dialog(context), View.OnClickListener {

    lateinit var myFont: Typeface
    var isDone = false
    var newLanguage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_change_language)
        myFont = StaticInformation().myFont(context)!!
        newLanguage = current
        // region font
        changeLanguageTitle.typeface = myFont
        changeLanguageBTN.typeface = myFont
        // endregion

        changeLanguageTitle.text = lang.getString("title")
        changeLanguageBTN.text = lang.getString("button")

        // region set current language
        if (current == Statics.arabic) {
            arabicRB.isChecked = true
            englishRB.isChecked = false
        } else {
            englishRB.isChecked = true
            arabicRB.isChecked = false
        }
        // endregion

        arabicRB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                englishRB.isChecked = false
                newLanguage = Statics.arabic
            }
        }

        englishRB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                arabicRB.isChecked = false
                newLanguage = Statics.english
            }
        }

        languageClose.setOnClickListener(this)
        arabicLL.setOnClickListener(this)
        changeLanguageBTN.setOnClickListener(this)
        englishLL.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.changeLanguageBTN -> {
                isDone = true
                if (fromMain) {
                    if (newLanguage == Statics.getCurrentLanguageName(activity as MainActivity)) {
                        dismiss()
                    } else {
                        Statics.setCurrentLanguageName(activity as MainActivity, newLanguage)
                        activity.finish()
                        activity.startActivity(activity.packageManager.getLaunchIntentForPackage(activity.packageName)?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    }
                } else {
                    if (newLanguage == Statics.arabic) {
                        Statics.setCurrentLanguageName(activity as LauncherActivity, newLanguage)
                    }
                }
                dismiss()
            }
            R.id.languageClose -> dismiss()
            R.id.arabicLL -> arabicClicked()
            R.id.englishLL -> englishClicked()
        }
    }

    private fun englishClicked() {
        englishRB.isChecked = true
    }

    private fun arabicClicked() {
        arabicRB.isChecked = true
    }
}