package com.smartlife_solutions.android.navara_store

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.smartlife_solutions.android.navara_store.Adapters.ItemImagesSlideAdapter
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.OfferBasicModel
import com.smartlife_solutions.android.navara_store.Dialogs.ChooseQuantityDialog
import kotlinx.android.synthetic.main.activity_offer_preview.*
import org.json.JSONObject
import java.util.*

class OfferPreviewActivity : AppCompatActivity() {

    lateinit var myFont: Typeface
    private lateinit var offer: OfferBasicModel
    private lateinit var lang: JSONObject
    private lateinit var langC: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer_preview)

        lang = Statics.getLanguageJSONObject(this)
        langC = lang.getJSONObject("offerDisPreviewActivity")

        if (Statics.getCurrentLanguageName(this) == Statics.arabic) {
            val conf = resources.configuration
            conf.setLayoutDirection(Locale("fa"))
            resources.updateConfiguration(conf, resources.displayMetrics)

            offerPreviewPercent.setBackgroundResource(R.drawable.offer_preview_icon_rtl)

        } else {
            val conf = resources.configuration
            conf.setLayoutDirection(Locale("en"))
            resources.updateConfiguration(conf, resources.displayMetrics)
        }

        myFont = StaticInformation().myFont(this)!!
        offerBackIV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(this))
            onBackPressed()
        }

        if (!StaticInformation().isConnected(this)) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.previewDisFL, NoInternetFragment(Statics.getLanguageJSONObject(this).getString("noConnection")))
            ft.commit()
            return
        }

        addToCartIV.setOnClickListener {

            try {
                if (Statics.myToken.isNotEmpty()) {
                    ChooseQuantityDialog(this, offer, true, Statics.getLanguageJSONObject(this)).show()
                } else {
                    startActivity(Intent(this, LoginRegisterActivity::class.java).putExtra("main", false))
                }
            } catch (err: Exception) {
                startActivity(Intent(this, LoginRegisterActivity::class.java).putExtra("main", false))
            }

            it.startAnimation(StaticInformation().clickAnim(this))
        }

        offerDisContactUsFAB.setOnClickListener {
            StaticInformation().openWhatsApp(this, offer.title)
        }

        // region font
        offerPreviewPercent.typeface = myFont
        offerTitleTV.typeface = myFont
        itemExistTV.typeface = myFont
        offerCostTV.typeface = myFont
        offerCostTV.text = langC.getString("price")
        offerDiscountTV.typeface = myFont
        offerOldCostTV.typeface = myFont
        offerOldCostTV.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        offerNewCostTV.typeface = myFont
        offerDescriptionTV.typeface = myFont
        offerDescriptionTV.text = langC.getString("description")
        offerDescriptionTextTV.typeface = myFont
        offerDescriptionTextTV2.typeface = myFont
        offerDisContactUsTV.typeface = myFont
        offerDisContactUsTV.text = langC.getString("contactUs")
        // endregion

        findViewById<TextView>(R.id.loadingTV).typeface = myFont
        findViewById<TextView>(R.id.loadingTV).text = lang.getString("loading")
        val rotateAnimation = RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f)

        rotateAnimation.interpolator = LinearInterpolator()
        rotateAnimation.duration = 3000
        rotateAnimation.repeatCount = Animation.INFINITE
        findViewById<ImageView>(R.id.loadingIV).startAnimation(rotateAnimation)

        getInfo(intent.getStringExtra("id"))
    }


    @SuppressLint("SetTextI18n")
    private fun getInfo(id: String) {
        try {
            val queue = Volley.newRequestQueue(this)
            val jsonObjectRequest = JsonObjectRequest(APIsURL().GET_OFFER + id, null, {
                Log.e("offer", it.toString())

                offerTitleTV.text = it.getString("title")
                offerDiscountTV.text = "-" + it.getInt("discount") + "%"
                offerOldCostTV.text = "${StaticInformation().formatPrice(it.getInt("unitPrice"))} ${lang.getString("currencyCode")}"
                offerNewCostTV.text = "${StaticInformation().formatPrice(it.getInt("unitNetPrice"))} ${lang.getString("currencyCode")}"
                offerDescriptionTextTV.text = it.getString("description")
                try {
                    offerDescriptionTextTV2.text = it.getString("description2")
                } catch (err: Exception) {}
                val images = it.getJSONArray("offerImages")
                val imagesList = ArrayList<String>()
                for (i in 0 until images.length()) {
                    imagesList.add(images[i].toString())
                }
                offer = OfferBasicModel(it.getString("id"), offerTitleTV.text.toString(),
                        it.getString("description"), "discount",
                        it.getString("thumbnailImagePath"), it.getInt("discount"),
                        it.getInt("unitNetPrice"))
                setupImages(imagesList)
                findViewById<RelativeLayout>(R.id.loadingRL).visibility = View.GONE
                queue.cancelAll("offer")
            }, {
                Log.e("error", "ERROR")
                queue.cancelAll("offer")
            })
            jsonObjectRequest.tag = "offer"
            queue.add(jsonObjectRequest)
        } catch (err: Exception) {
            Log.e("error", err.message!!)
        }
    }

    private fun setupImages(imagesList: ArrayList<String>) {
        if (imagesList.size > 0) {
            val adapter = ItemImagesSlideAdapter(supportFragmentManager)
            if (Statics.getCurrentLanguageName(this) == Statics.english) {
                for (image in imagesList) {
                    adapter.addFragment(ImageSliderFragment(imagesList, imagesList.indexOf(image), true))
                }
            } else {
                itemImagesVP.rotationY = 180F
                for (image in imagesList) {
                    adapter.addFragment(ImageSliderFragment(imagesList, imagesList.indexOf(image), true, true))
                }
            }
            itemImagesVP.adapter = adapter

            for (i in 0 until imagesList.size) {
                val relCirclePrimary = RelativeLayout(this)
                val relParams = RelativeLayout.LayoutParams(
                        resources.getDimension(R.dimen.point_top).toInt(),
                        resources.getDimension(R.dimen.point_top).toInt())

                val marginPixels = (resources.getDimension(R.dimen.padding_point)).toInt()
                relParams.setMargins(marginPixels, marginPixels, marginPixels, marginPixels)
                relCirclePrimary.layoutParams = relParams

                relCirclePrimary.setBackgroundResource(R.drawable.white_button_background)
                itemCirclesNumberLL.addView(relCirclePrimary)
            }
            itemCirclesNumberLL.getChildAt(0).setBackgroundResource(R.drawable.primary_button_background)

            itemImagesVP.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(p0: Int) {}
                override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

                override fun onPageSelected(p0: Int) = setCurrentImage(p0)

            })
        }
    }

    private fun setCurrentImage(position: Int) {
        for (i in 0 until itemCirclesNumberLL.childCount) {
            itemCirclesNumberLL.getChildAt(i).setBackgroundResource(R.drawable.white_button_background)
        }
        itemCirclesNumberLL.getChildAt(position).setBackgroundResource(R.drawable.primary_button_background)
    }

    override fun onBackPressed() {
        if (intent.getBooleanExtra(Statics.fromNotification, false)) {
            startActivity(Intent(this, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
        super.onBackPressed()
    }
}
