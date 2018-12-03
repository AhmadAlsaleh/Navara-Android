package com.smartlife_solutions.android.navara_store

import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.smartlife_solutions.android.navara_store.Adapters.AllOffersAdapter
import com.smartlife_solutions.android.navara_store.Adapters.ProjectsAdapter
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.*
import kotlinx.android.synthetic.main.activity_offers.*
import kotlinx.android.synthetic.main.activity_projects.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class ProjectsActivity : AppCompatActivity() {

    private lateinit var myFont: Typeface
    private lateinit var lang: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_projects)
        myFont = StaticInformation().myFont(this)!!
        lang = Statics.getLanguageJSONObject(this)

        projectsBackIV.setOnClickListener {
            onBackPressed()
        }

        projectsTitleTV.typeface = myFont
        projectsTitleTV.text = lang.getJSONObject("moreFeaturesActivity").getString("projects")

        if (StaticInformation().isConnected(this)) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.projectsFL, LoadingFragment())
            ft.commit()
            getProjects()
        } else {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.projectsFL, NoInternetFragment())
            ft.commit()
        }

    }

    private fun setProjects(projectsJSONArray: JSONArray) {

        projectsFL.visibility = View.GONE

        val projects = ArrayList<ProjectBasicModel>()
        for (i in 0 until projectsJSONArray.length()) {
            val projectJSON = projectsJSONArray.getJSONObject(i)

            val images = ArrayList<String>()
            for (image in 0 until projectJSON.getJSONArray("projectImages").length()) {
                images.add(projectJSON.getJSONArray("projectImages").getString(image))
            }

            val items = ArrayList<ItemBasicModel>()
            for (item in 0 until projectJSON.getJSONArray("projectItems").length()) {
                val itemJSON = projectJSON.getJSONArray("projectItems").getJSONObject(item)
                items.add(ItemBasicModel(itemJSON.getString("id"),
                        itemJSON.getString("name"),
                        itemJSON.getString("itemCategory"),
                        itemJSON.getString("itemCategoryID"),
                        itemJSON.getInt("quantity"),
                        (itemJSON.get("price") as Double).toFloat(),
                        itemJSON.getString("thumbnailImagePath"),
                        itemJSON.getString("cashBack"),
                        itemJSON.getString("accountID"),
                        itemJSON.getString("daysToBeAvilable").toIntOrNull()))
            }

            projects.add(ProjectBasicModel(projectJSON.getString("name"),
                    projectJSON.getString("description"),
                    images,
                    items))
        }

        projectsRV.setHasFixedSize(true)
        projectsRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        projectsRV.adapter = ProjectsAdapter(this, projects, lang)

    }

    private fun getProjects() {
        val queue = Volley.newRequestQueue(this)
        val projectsRequest = JsonArrayRequest(APIsURL().PROJECTS, {
            Log.e("projects", it.toString())
            queue.cancelAll("projects")
            setProjects(it)
        }, {
            Log.e("projects error", it.toString())
            queue.cancelAll("projects")
            getProjects()
        })
        projectsRequest.tag = "projects"
        queue.add(projectsRequest)
    }

}