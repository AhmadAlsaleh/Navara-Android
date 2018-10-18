package com.smartlife_solutions.android.navara_store

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
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
import com.smartlife_solutions.android.navara_store.Adapters.PreviewFreeItemsAdapter
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.ItemBasicModel
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.OfferBasicModel
import com.smartlife_solutions.android.navara_store.Dialogs.ChooseQuantityDialog
import kotlinx.android.synthetic.main.activity_offer_free_preview.*

class OfferFreePreviewActivity : AppCompatActivity() {

    lateinit var myFont: Typeface
    private lateinit var offer: OfferBasicModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer_free_preview)
        myFont = StaticInformation().myFont(this)!!
        offerBackIV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(this))
            onBackPressed()
        }

        if (!StaticInformation().isConnected(this)) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.previewFreeFL, NoInternetFragment())
            ft.commit()
            return
        }

        addToCartIV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(this))

            try {
                if (DatabaseHelper(this).userModelIntegerRuntimeException.queryForAll()[0].token.isNotEmpty()) {
                    ChooseQuantityDialog(this, offer, true, this).show()
                } else {
                    startActivity(Intent(this, LoginRegisterActivity::class.java).putExtra("main", false))
                }
            } catch (err: Exception) {
                startActivity(Intent(this, LoginRegisterActivity::class.java).putExtra("main", false))
            }

        }

        // region font
        offerTitleTV.typeface = myFont
        offerDetailsTV.typeface = myFont
        getFreeTV.typeface = myFont
        offerDescriptionTV.typeface = myFont
        offerDescriptionTextTV.typeface = myFont
        // endregion

        getInfo(intent.getStringExtra("id"))


        findViewById<TextView>(R.id.loadingTV).typeface = myFont
        val rotateAnimation = RotateAnimation(0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f)

        rotateAnimation.interpolator = LinearInterpolator()
        rotateAnimation.duration = 3000
        rotateAnimation.repeatCount = Animation.INFINITE
        findViewById<ImageView>(R.id.loadingIV).startAnimation(rotateAnimation)
    }

    @SuppressLint("SetTextI18n")
    private fun getInfo(id: String) {
        try {
            val queue = Volley.newRequestQueue(this)
            val jsonObjectRequest = JsonObjectRequest(APIsURL().GET_OFFER + id, null, {
                Log.e("offer", it.toString())

                offerTitleTV.text = it.getString("title")
                offerDescriptionTextTV.text = it.getString("description")
                val images = it.getJSONArray("offerImages")
                val imagesList = ArrayList<String>()
                for (i in 0 until images.length()) {
                    imagesList.add(images[i].toString())
                }
                offer = OfferBasicModel(it.getString("id"), it.getString("title"),
                        it.getString("description"), "free",
                        it.getString("thumbnailImagePath"), it.getInt("discount"),
                        it.getInt("unitNetPrice"))
                setupImages(imagesList)

                val itemsJSON = it.getJSONArray("offerItems")
                val itemsArray = ArrayList<ItemBasicModel>()
                for (i in 0 until itemsJSON.length()) {
                    val item = itemsJSON.getJSONObject(i)
                    itemsArray.add(ItemBasicModel(item.getString("id"), item.getString("name"),
                            item.getString("itemCategory"), item.getString("itemCategoryID"),
                            item.getInt("quantity"), item.getInt("price").toFloat(),
                            item.getString("thumbnailImagePath")))
                }

                val itemsArrayList = ArrayList<ItemBasicModel>()
                itemsArrayList.add(ItemBasicModel(it.getString("itemID"), it.getString("itemName"),
                        "","",1,it.getInt("unitNetPrice").toFloat(),
                        it.getString("thumbnailImagePath")))

                offerPreviewItemsRV.setHasFixedSize(true)
                offerPreviewItemsRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                offerPreviewItemsRV.adapter = PreviewFreeItemsAdapter(context = this, itemsArrayList = itemsArrayList, isAll = false)


                setupItems(itemsArray)

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

    private fun setupItems(itemsArrayListFree: ArrayList<ItemBasicModel>) {
        offerPreviewOffersRV.setHasFixedSize(true)
        offerPreviewOffersRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        offerPreviewOffersRV.adapter = PreviewFreeItemsAdapter(context = this, itemsArrayList = itemsArrayListFree, isAll = false)

    }

    private fun setupImages(imagesList: ArrayList<String>) {
        if (imagesList.size > 0) {
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
    }

    private fun setCurrentImage(position: Int) {
        for (i in 0..itemCirclesNumberLL.childCount - 1) {
            itemCirclesNumberLL.getChildAt(i).setBackgroundResource(R.drawable.white_button_background)
        }
        itemCirclesNumberLL.getChildAt(position).setBackgroundResource(R.drawable.primary_button_background)
    }
}
