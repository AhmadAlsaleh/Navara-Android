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
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.ProjectBasicModel
import com.smartlife_solutions.android.navara_store.ProjectPreview
import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation
import com.smartlife_solutions.android.navara_store.Statics
import com.squareup.picasso.Picasso
import org.json.JSONObject

class ProjectsAdapter(private val context: Context,
                      private val projects: ArrayList<ProjectBasicModel>, private val lang: JSONObject)
    : RecyclerView.Adapter<ProjectsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int)
            = ViewHolder(LayoutInflater.from(p0.context)
                .inflate(R.layout.item_projects_card, p0, false))

    override fun getItemCount() = projects.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myFont = StaticInformation().myFont(context)
        holder.projectTitleTV.typeface = myFont
        holder.projectDescriptionTV.typeface = myFont

        val project = projects[position]
        holder.projectTitleTV.text = project.name
        holder.projectDescriptionTV.text = project.description

        var mainImage = APIsURL().BASE_URL
        if (project.images.isNotEmpty()) {
            mainImage += project.images[0]
        }
        Picasso.with(context)
                .load(mainImage)
                .into(holder.projectIV)

        holder.projectLL.setOnClickListener {
            Statics.setProjectBasicModel(project)
            context.startActivity(Intent(context, ProjectPreview::class.java))
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val projectLL = itemView.findViewById<LinearLayout>(R.id.projectLL)!!
        val projectTitleTV = itemView.findViewById<TextView>(R.id.projectTV)!!
        val projectDescriptionTV = itemView.findViewById<TextView>(R.id.projectDescriptionTV)!!
        val projectIV = itemView.findViewById<ImageView>(R.id.projectIV)!!

    }

}