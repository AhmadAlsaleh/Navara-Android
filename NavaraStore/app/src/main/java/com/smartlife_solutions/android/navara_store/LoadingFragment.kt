package com.smartlife_solutions.android.navara_store

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView

class LoadingFragment : Fragment() {

    private lateinit var myFont: Typeface

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val viewLoad = inflater.inflate(R.layout.fragment_loading, container, false)

        myFont = StaticInformation().myFont(context)!!
        viewLoad.findViewById<TextView>(R.id.loadingTV).typeface = myFont
        val image: ImageView = viewLoad.findViewById(R.id.loadingIV)

        BackRotateAsync(image).execute(null)

        return viewLoad
    }

    @SuppressLint("StaticFieldLeak")
    class BackRotateAsync(private var imageView: ImageView): AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            val rotateAnimation = RotateAnimation(0f, 360f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f)

            rotateAnimation.interpolator = LinearInterpolator()
            rotateAnimation.duration = 3000
            rotateAnimation.repeatCount = Animation.INFINITE
            imageView.startAnimation(rotateAnimation)
            return null
        }
    }

}