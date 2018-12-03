package com.smartlife_solutions.android.navara_store.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.smartlife_solutions.android.navara_store.CoursePreview
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.CoursesBasicModel
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation
import com.squareup.picasso.Picasso
import org.json.JSONObject

class CoursesAdapter(private val context: Context,
                     private val courses: ArrayList<CoursesBasicModel>, private val lang: JSONObject)
    : RecyclerView.Adapter<CoursesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int)
            = ViewHolder(LayoutInflater.from(p0.context)
                .inflate(R.layout.item_courses_card, p0, false))

    override fun getItemCount() = courses.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myFont = StaticInformation().myFont(context)
        holder.courseTitleTV.typeface = myFont
        holder.courseCostTV.typeface = myFont
        holder.courseCostTitle.typeface = myFont
        holder.courseCostTitle.text = lang.getJSONObject("moreFeaturesActivity")
                .getJSONObject("coursesActivity").getString("cost")

        val course = courses[position]
        holder.courseTitleTV.text = course.title
        holder.courseCostTV.text = StaticInformation().formatPrice(course.cost) + " " + lang.getString("currencyCode")
        Picasso.with(context)
                .load(APIsURL().BASE_URL + course.image)
                .into(holder.courseIV)

        holder.courseLL.setOnClickListener {
            context.startActivity(Intent(context, CoursePreview::class.java)
                    .putExtra("id", "")
                    .putExtra("info", course))
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val courseLL = itemView.findViewById<LinearLayout>(R.id.courseLL)!!
        val courseTitleTV = itemView.findViewById<TextView>(R.id.courseTV)!!
        val courseCostTitle = itemView.findViewById<TextView>(R.id.courseCostTitle)!!
        val courseCostTV = itemView.findViewById<TextView>(R.id.courseCostTV)!!
        val courseIV = itemView.findViewById<ImageView>(R.id.courseIV)!!

    }

}