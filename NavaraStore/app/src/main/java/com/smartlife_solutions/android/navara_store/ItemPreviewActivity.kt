package com.smartlife_solutions.android.navara_store

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.smartlife_solutions.android.navara_store.Adapters.ItemImagesSlideAdapter
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.ItemBasicModel
import com.smartlife_solutions.android.navara_store.Dialogs.ChooseQuantityDialog
import kotlinx.android.synthetic.main.activity_item_preview.*
import org.json.JSONObject
import java.util.*

class ItemPreviewActivity : AppCompatActivity() {

    private lateinit var item: ItemBasicModel
    private val imagesList = ArrayList<String>()
    private lateinit var lang: JSONObject
    private lateinit var langC: JSONObject

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_preview)

        lang = Statics.getLanguageJSONObject(this)
        langC = lang.getJSONObject("itemPreviewActivity")

        if (Statics.getCurrentLanguageName(this) == Statics.arabic) {
            val conf = resources.configuration
            conf.setLayoutDirection(Locale("fa"))
            resources.updateConfiguration(conf, resources.displayMetrics)
        } else {
            val conf = resources.configuration
            conf.setLayoutDirection(Locale("en"))
            resources.updateConfiguration(conf, resources.displayMetrics)
        }

        itemBackIV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(this))
            onBackPressed()
        }

        if (!StaticInformation().isConnected(this)) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.previewItemFL, NoInternetFragment(lang.getString("noConnection")))
            ft.commit()
            return
        }

        addToCartIV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(this))
            try {
                Statics.myToken = DatabaseHelper(this).userModelIntegerRuntimeException.queryForAll()[0].token
            } catch (err: Exception) {
                Statics.myToken = ""
                startActivity(Intent(this, LoginRegisterActivity::class.java).putExtra("main", false))
                return@setOnClickListener
            }
            if (Statics.myToken.isNotEmpty()) {
                Log.e("item days", item.daysToBeAvilable.toString())
                if (item.daysToBeAvilable > 0) {
                    Toast.makeText(this, langC.getString("outOfStock"), Toast.LENGTH_SHORT).show()
                }
                ChooseQuantityDialog(this, item, false, lang = Statics.getLanguageJSONObject(this)).show()
            } else {
                startActivity(Intent(this, LoginRegisterActivity::class.java).putExtra("main", false))
            }
        }

        itemContactUsFAB.setOnClickListener {
            StaticInformation().openWhatsApp(this, "ItemID: ${item.id}\nTitle: ${item.name}")
        }

        // region font
        val myFont = StaticInformation().myFont(this)
        itemTitleTV.typeface = myFont
        itemExistTV.typeface = myFont
        itemDaysTV.typeface = myFont
        itemDaysNoteTV.typeface = myFont
        itemDaysNoteTV.text = langC.getString("starNote")
        itemCostTV.typeface = myFont
        itemCostTextTV.typeface = myFont
        itemCostTextTV.text = langC.getString("price")
        itemDescriptionTV.typeface = myFont
        itemDescriptionTV2.typeface = myFont
        itemDescriptionTextTV.typeface = myFont
        itemDescriptionTextTV.text = langC.getString("description")
        itemCashBackTV.typeface = myFont
        itemContactUsTextTV.typeface = myFont
        itemContactUsTextTV.text = langC.getString("contactInformation")
        itemOwnerTextTV.typeface = myFont
        itemOwnerTextTV.text = langC.getString("name")
        itemOwnerTV.typeface = myFont
        itemPhoneTextTV.typeface = myFont
        itemPhoneTextTV.text = langC.getString("mobile")
        itemPhoneTV.typeface = myFont
        itemContactUsTV.typeface = myFont
        itemContactUsTV.text = langC.getString("contactUs")
        //endregion

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
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest = JsonObjectRequest(APIsURL().GET_ITEM + id, null, {
            Log.e("item", it.toString())
            itemTitleTV.text = it.getString("name")
            var days = 0
            try {
                days = it.getInt("daysToBeAvilable")
            } catch (e: Exception) {}
            Log.e("days", days.toString())
            if (it.getInt("quantity") == 0 || days > 0) {
                itemExistTV.text = langC.getString("outOfStock")
                itemDaysLL.visibility = View.VISIBLE
                when (days) {
                    0 -> itemDaysTV.text = langC.getString("soon")
                    1 -> itemDaysTV.text = "1 ${langC.getString("day")}"
                    else -> itemDaysTV.text = "$days ${langC.getString("days")}"
                }
            } else {
                itemExistTV.text = langC.getString("existInOurStock")
                itemDaysLL.visibility = View.GONE
            }
            itemCostTV.text = "${StaticInformation().formatPrice(it.getInt("price"))} ${lang.getString("currencyCode")}"
            itemDescriptionTV.text = it.getString("description")
            itemDescriptionTV2.text = it.getString("description2")


            if (it.getString("cashBack") == "null" ||
                    it.getString("cashBack").toFloat().toInt() == 0) {
                itemCashBackLL.visibility = View.GONE
            } else {
                itemCashBackLL.visibility = View.VISIBLE
                itemCashBackTV.text = "${it.getString("cashBack").toFloat().toInt()} ${lang.getString("currencyCode")}"
            }

            val images = it.getJSONArray("itemImages")
            for (i in 0 until images.length()) {
                imagesList.add(images[i].toString())
            }
            item = ItemBasicModel(
                    id,
                    it.getString("name"),
                    "", "",
                    it.getInt("quantity"),
                    it.getInt("price").toFloat(),
                    it.getString("thumbnailImagePath"),
                    it.getString("cashBack"),
                    it.getString("accountID"),
                    days)

            if (item.accountID.isNotEmpty()) {
                addToCartIV.visibility = View.GONE
                itemExistTV.visibility = View.GONE
                itemContactUsLL.visibility = View.VISIBLE
                itemOwnerTV.text = it.getString("owner")
                itemPhoneTV.text = it.getString("mobile")
            } else {
                itemContactUsLL.visibility = View.GONE
            }
            item.cashBack = it.getString("cashBack")
            setupImages(imagesList)

            findViewById<RelativeLayout>(R.id.loadingRL).visibility = View.GONE
            queue.cancelAll("item")
        }, {
            Log.e("error", "ERROR")
            queue.cancelAll("item")
        })
        jsonObjectRequest.tag = "item"
        queue.add(jsonObjectRequest)
    }

    private fun setupImages(imagesList: ArrayList<String>) {
        if (imagesList.size == 0) {
            return
        }
        val adapter = ItemImagesSlideAdapter(supportFragmentManager)
        for (image in imagesList) {
            adapter.addFragment(ImageSliderFragment(imagesList, imagesList.indexOf(image), true))
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