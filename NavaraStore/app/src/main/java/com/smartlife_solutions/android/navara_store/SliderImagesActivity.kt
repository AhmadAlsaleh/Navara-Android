package com.smartlife_solutions.android.navara_store

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.widget.RelativeLayout
import com.smartlife_solutions.android.navara_store.Adapters.ItemImagesSlideAdapter
import kotlinx.android.synthetic.main.activity_slider_images.*

class SliderImagesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slider_images)

        setupImages(intent.getStringArrayListExtra("images"))
        sliderImagesVP.currentItem = intent.getIntExtra("position", 0)

    }

    private fun setupImages(imagesList: ArrayList<String>) {
        if (imagesList.size == 0) {
            return
        }
        val adapter = ItemImagesSlideAdapter(supportFragmentManager)
        for (image in imagesList) {
            adapter.addFragment(ImageSliderFragment(imagesList, imagesList.indexOf(image), false))
        }
        sliderImagesVP.adapter = adapter

        for (i in 0 until imagesList.size) {
            val relCirclePrimary = RelativeLayout(this)
            val relParams = RelativeLayout.LayoutParams(
                    resources.getDimension(R.dimen.point_top).toInt(),
                    resources.getDimension(R.dimen.point_top).toInt())

            val marginPixels = (resources.getDimension(R.dimen.padding_point)).toInt()
            relParams.setMargins(marginPixels, marginPixels, marginPixels, marginPixels)
            relCirclePrimary.layoutParams = relParams

            relCirclePrimary.setBackgroundResource(R.drawable.white_button_background)
            sliderImagesCirclesNumberLL.addView(relCirclePrimary)
        }
        sliderImagesCirclesNumberLL.getChildAt(0).setBackgroundResource(R.drawable.primary_button_background)

        sliderImagesVP.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

            override fun onPageSelected(p0: Int) = setCurrentImage(p0)

        })
    }

    private fun setCurrentImage(position: Int) {
        for (i in 0 until sliderImagesCirclesNumberLL.childCount) {
            sliderImagesCirclesNumberLL.getChildAt(i).setBackgroundResource(R.drawable.white_button_background)
        }
        sliderImagesCirclesNumberLL.getChildAt(position).setBackgroundResource(R.drawable.primary_button_background)
    }


}
