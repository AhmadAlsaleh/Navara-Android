package com.smartlife_solutions.android.navara_store

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.RelativeLayout
import com.smartlife_solutions.android.navara_store.Adapters.ItemImagesSlideAdapter
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.ProjectBasicModel
import kotlinx.android.synthetic.main.activity_project_preview.*
import org.json.JSONObject
import java.util.*

class ProjectPreview : AppCompatActivity() {

    private lateinit var lang: JSONObject
    private lateinit var myFont: Typeface

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_preview)

        lang = Statics.getLanguageJSONObject(this)
        val langC = lang.getJSONObject("moreFeaturesActivity").getJSONObject("projectsActivity")
        myFont = StaticInformation().myFont(this)!!

        projectBackIV.setOnClickListener {
            onBackPressed()
        }

        projectTitleTV.typeface = myFont
        projectDescriptionTextTV.typeface = myFont
        projectDescriptionTextTV.text = langC.getString("description")
        projectDescriptionTV.typeface = myFont
        projectItemsTextTV.typeface = myFont
        projectItemsTextTV.text = langC.getString("items")

        if (Statics.getCurrentLanguageName(this) == Statics.arabic) {
            val conf = resources.configuration
            conf.setLayoutDirection(Locale("fa"))
            resources.updateConfiguration(conf, resources.displayMetrics)
        } else {
            val conf = resources.configuration
            conf.setLayoutDirection(Locale("en"))
            resources.updateConfiguration(conf, resources.displayMetrics)
        }

        if (!StaticInformation().isConnected(this)) {intent.getSerializableExtra("info") as ProjectBasicModel
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.projectFL, NoInternetFragment(lang.getString("noConnection")))
            ft.commit()
            return
        }

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.projectFL, LoadingFragment())
        ft.commit()

        val project = Statics.getProjectBasicModel()!!
        projectTitleTV.text = project.name
        projectDescriptionTV.text = project.description
        setupImages(project.images)

        for (item in project.items) {
            projectItemsLL.addView(ItemView(null, this, item, true, lang = Statics.getLanguageJSONObject(this)).view)
        }

        projectFL.visibility = View.GONE

    }

    private fun setupImages(imagesList: ArrayList<String>) {
        if (imagesList.size == 0) {
            return
        }
        val adapter = ItemImagesSlideAdapter(supportFragmentManager)
        if (Statics.getCurrentLanguageName(this) == Statics.english) {
            for (image in imagesList) {
                adapter.addFragment(ImageSliderFragment(imagesList, imagesList.indexOf(image), true))
            }
        } else {
            projectImagesVP.rotationY = 180F
            for (image in imagesList) {
                adapter.addFragment(ImageSliderFragment(imagesList, imagesList.indexOf(image), true, true))
            }
        }

        projectImagesVP.adapter = adapter

        for (i in 0 until imagesList.size) {
            val relCirclePrimary = RelativeLayout(this)
            val relParams = RelativeLayout.LayoutParams(
                    resources.getDimension(R.dimen.point_top).toInt(),
                    resources.getDimension(R.dimen.point_top).toInt())

            val marginPixels = (resources.getDimension(R.dimen.padding_point)).toInt()
            relParams.setMargins(marginPixels, marginPixels, marginPixels, marginPixels)
            relCirclePrimary.layoutParams = relParams

            relCirclePrimary.setBackgroundResource(R.drawable.white_button_background)
            projectCirclesNumberLL.addView(relCirclePrimary)
        }
        projectCirclesNumberLL.getChildAt(0).setBackgroundResource(R.drawable.primary_button_background)
        projectImagesVP.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

            override fun onPageSelected(p0: Int) = setCurrentImage(p0)
        })
    }

    private fun setCurrentImage(position: Int) {
        for (i in 0 until projectCirclesNumberLL.childCount) {
            projectCirclesNumberLL.getChildAt(i).setBackgroundResource(R.drawable.white_button_background)
        }
        projectCirclesNumberLL.getChildAt(position).setBackgroundResource(R.drawable.primary_button_background)
    }

}
