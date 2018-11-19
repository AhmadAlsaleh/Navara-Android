package com.smartlife_solutions.android.navara_store

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.squareup.picasso.Picasso

@SuppressLint("ValidFragment")
class ImageSliderFragment(var imagesList: ArrayList<String>, var position: Int,
                          var showFull: Boolean = false, val isRTL: Boolean = false): Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.item_image, container, false)
        val image = view.findViewById<ImageView>(R.id.itemHeroIV)

        try {
            Picasso.with(context)
                    .load(APIsURL().BASE_URL + imagesList[position])
                    .into(image)
        } catch (err: Exception) {
            Log.e("image slide", err.message)
        }

        if (showFull) {
            image.setOnClickListener {
                startActivity(Intent(context, SliderImagesActivity::class.java)
                        .putExtra("position", position)
                        .putExtra("images", imagesList))
            }
        }

        if (isRTL) {
            view.rotationY = 180F
        }

        return view
    }
}