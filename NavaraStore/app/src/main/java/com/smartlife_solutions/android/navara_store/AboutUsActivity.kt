package com.smartlife_solutions.android.navara_store

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_about_us.*

class AboutUsActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aboutUsDoneBTN -> onBackPressed()
            R.id.aboutUsNavaraIV -> startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse(StaticInformation().navaraWebSite())))
            R.id.aboutUsSmartLifeIV -> startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse(StaticInformation().smartlifeWebSite())))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_about_us)

        val myFont = StaticInformation().myFont(this)
        aboutUsTitle.typeface = myFont
        aboutUsDoneBTN.typeface = myFont
        aboutUsTV.typeface = myFont

        aboutUsClose.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(this))
            onBackPressed()
        }

        aboutUsDoneBTN.setOnClickListener(this)
        aboutUsNavaraIV.setOnClickListener(this)
        aboutUsSmartLifeIV.setOnClickListener(this)

    }
}
