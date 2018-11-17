package com.smartlife_solutions.android.navara_store.Dialogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation
import kotlinx.android.synthetic.main.dialog_about_us.*
import org.json.JSONObject

class AboutUsDialog(context: Context, var lang: JSONObject): Dialog(context), View.OnClickListener {

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aboutUsDoneBTN -> dismiss()
            R.id.aboutUsNavaraIV -> context.startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse(StaticInformation().navaraWebSite())))
            R.id.aboutUsSmartLifeIV -> context.startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse(StaticInformation().smartlifeWebSite())))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_about_us)

        val myFont = StaticInformation().myFont(context)
        aboutUsTitle.typeface = myFont
        aboutUsTitleTV.typeface = myFont
        aboutUsDoneBTN.typeface = myFont
        aboutUsTV.typeface = myFont
        aboutUsDevTV.typeface = myFont

        aboutUsTitle.text = lang.getString("title")
        aboutUsTV.text = lang.getString("message")
        aboutUsDoneBTN.text = lang.getString("button")

        aboutUsClose.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(context))
            dismiss()
        }

        aboutUsDoneBTN.setOnClickListener(this)
        aboutUsNavaraIV.setOnClickListener(this)
        aboutUsSmartLifeIV.setOnClickListener(this)

    }
}