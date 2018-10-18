package com.smartlife_solutions.android.navara_store

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.View
import com.smartlife_solutions.android.navara_store.ItemsActivityFragments.CategoryFragment
import kotlinx.android.synthetic.main.activity_items.*

class ItemsActivity : AppCompatActivity() {

    private var fragmentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)

        searchTV.typeface = StaticInformation().myFont(this)

        itemsBackIV.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(this))
            onBackPressed()
        }

        searchLL.setOnClickListener {
            it.startAnimation(StaticInformation().clickAnim(this))
            startActivity(Intent(this, SearchItemActivity::class.java))
        }

        setupFragment(CategoryFragment())

        itemsTitleLayoutTV.typeface = StaticInformation().myFont(this)
        itemsOfflineTV.typeface = StaticInformation().myFont(this)

        checkConnection()
        checkTimer()

    }

    private fun checkTimer() {
        Handler().postDelayed({
            checkConnection()
        }, StaticInformation().CHECK_INTERNET)
    }

    private fun checkConnection() {
        try {
            if (StaticInformation().isConnected(this)) {
                itemsOfflineTV.visibility = View.GONE
            } else {
                itemsOfflineTV.visibility = View.VISIBLE
            }
            checkTimer()
        } catch (err: Exception) {}
    }

    fun setupFragment(frag: Fragment, fragmentPosition: Int = 0, title: String = "Our Categories") {
        this.fragmentPosition = fragmentPosition
        itemsTitleLayoutTV.text = title
        val fragmentTranslate = supportFragmentManager.beginTransaction()
        fragmentTranslate.replace(R.id.itemsFL, frag)
        fragmentTranslate.commit()
    }

    override fun onBackPressed() {
        if (fragmentPosition != 0) {
            setupFragment(CategoryFragment())
            return
        }
        super.onBackPressed()
    }

}
