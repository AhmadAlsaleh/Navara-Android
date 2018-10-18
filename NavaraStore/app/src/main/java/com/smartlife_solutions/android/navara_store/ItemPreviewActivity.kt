package com.smartlife_solutions.android.navara_store

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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

class ItemPreviewActivity : AppCompatActivity() {

    private lateinit var item: ItemBasicModel
    private val imagesList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_preview)

        itemBackIV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(this))
            onBackPressed()
        }

        if (!StaticInformation().isConnected(this)) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.previewItemFL, NoInternetFragment())
            ft.commit()
            return
        }

        addToCartIV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(this))
            try {
                if (DatabaseHelper(this).userModelIntegerRuntimeException.queryForAll()[0].token.isNotEmpty()) {
                    if (item.quantity > 0) {
                        ChooseQuantityDialog(this, item, false, this).show()
                    } else {
                        Toast.makeText(this, "Out of Stock", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    startActivity(Intent(this, LoginRegisterActivity::class.java).putExtra("main", false))
                }
            } catch (err: Exception) {
                startActivity(Intent(this, LoginRegisterActivity::class.java).putExtra("main", false))
            }
        }

        // region font
        val myFont = StaticInformation().myFont(this)
        itemTitleTV.typeface = myFont
        itemExistTV.typeface = myFont
        itemCostTV.typeface = myFont
        itemCostTextTV.typeface = myFont
        itemDescriptionTV.typeface = myFont
        itemDescriptionTextTV.typeface = myFont
        //endregion

        findViewById<TextView>(R.id.loadingTV).typeface = myFont
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
            itemTitleTV.text = it.getString("name")
            if (it.getInt("quantity") == 0) {
                itemExistTV.text = "Out of Stock"
            }
            itemCostTV.text = "${StaticInformation().formatPrice(it.getInt("price"))} S.P"
            itemDescriptionTV.text = it.getString("description")
            val images = it.getJSONArray("itemImages")
            for (i in 0 until images.length()) {
                imagesList.add(images[i].toString())
            }
            item = ItemBasicModel(id, it.getString("name"), "", "", it.getInt("quantity"), it.getInt("price").toFloat(), it.getString("thumbnailImagePath"))
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

}