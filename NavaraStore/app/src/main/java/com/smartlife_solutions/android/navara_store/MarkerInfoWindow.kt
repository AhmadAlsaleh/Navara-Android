package com.smartlife_solutions.android.navara_store

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import org.json.JSONObject

class MarkerInfoWindow(var context: Context, var title: String, var lang: JSONObject): GoogleMap.InfoWindowAdapter {

    private lateinit var titleTV: TextView

    @SuppressLint("InflateParams")
    private val mWindow = LayoutInflater.from(context).inflate(R.layout.marker_info_window, null)

    private fun setWindowContent(view: View) {
        val myFont = StaticInformation().myFont(context)
        view.findViewById<TextView>(R.id.markerInfoTitle).typeface = myFont
        view.findViewById<TextView>(R.id.markerInfoTitle).text = lang.getString("title")
        titleTV = view.findViewById(R.id.markerInfoTV)
        titleTV.typeface = myFont
        titleTV.text = title
    }

    override fun getInfoContents(p0: Marker?): View {
        setWindowContent(mWindow)
        return mWindow
    }

    override fun getInfoWindow(p0: Marker?): View {
        setWindowContent(mWindow)
        return mWindow
    }
}