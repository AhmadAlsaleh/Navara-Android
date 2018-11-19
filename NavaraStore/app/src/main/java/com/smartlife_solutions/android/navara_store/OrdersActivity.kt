package com.smartlife_solutions.android.navara_store

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.model.LatLng
import com.smartlife_solutions.android.navara_store.Adapters.MakeOrderPagerAdapter
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.ItemBasicModel
import com.smartlife_solutions.android.navara_store.Dialogs.SureToDoDialog
import com.smartlife_solutions.android.navara_store.OrderFragments.*
import kotlinx.android.synthetic.main.activity_orders.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class OrdersActivity : AppCompatActivity(), View.OnClickListener {

    // region variables
    private lateinit var selectItemsFragment: OrderSelectItemsFragment
    lateinit var chooseLocationFragment: OrderChooseLocationFragment
    private lateinit var chooseVisibleTimeFragment: OrderChooseVisibleTimeFragment
    private lateinit var personalInformationFragment: OrderPersonalInformationFragment
    lateinit var summaryFragment: OrderSummaryFragment
    val items = ArrayList<ItemBasicModel>()
    val itemsHash = HashMap<String, ItemBasicModel>()
    val offersHash = ArrayList<ItemBasicModel>()

    private var isFromCart = false

    private var isSelected = false
    private var isLocation = false

    internal var finalSelectedItems = ArrayList<ItemBasicModel>() // items selected

    internal var latLng: LatLng? = null // location
    var locationRemarkText = "" // location remark
    var locationText = "" // location text

    internal var fromTime = "09:00 AM" // from time
    internal var toTime = "21:00" // to time

    var personName = "" // name
    var personPhone = "" // phone
    var personRemark = "" // person remark
    // endregion

    private lateinit var lang: JSONObject
    var firstOrder
        get() = getSharedPreferences("Navara", Context.MODE_PRIVATE).getBoolean("order", true)
        set(isFirst) {
            val perfs = getSharedPreferences("Navara", Context.MODE_PRIVATE).edit()
            perfs.putBoolean("order", isFirst)
            perfs.apply()
        }

    override fun onClick(v: View?) {
        v?.startAnimation(StaticInformation().clickAnim(this))
        when (v?.id) {
            R.id.orderBackIV -> onBackPressed()
            R.id.orderIcon1 -> setCurrentPage(0)
            R.id.orderIcon2 -> setCurrentPage(1)
            R.id.orderIcon3 -> setCurrentPage(2)
            R.id.orderIcon4 -> setCurrentPage(3)
            R.id.orderIcon5 -> setCurrentPage(4)
            R.id.addOrderPreviousLL -> setCurrentPage(addOrderVP.currentItem - 1)
            R.id.addOrderNextLL -> setCurrentPage(addOrderVP.currentItem + 1)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        val ft = supportFragmentManager.beginTransaction()
        if (!StaticInformation().isConnected(this)) {
            ft.replace(R.id.orderLoaderFL, NoInternetFragment(Statics.getLanguageJSONObject(this).getString("noConnection")))
            ft.commit()
            return
        }
        setHint()

        if (Statics.getCurrentLanguageName(this) == Statics.arabic) {
            val conf = resources.configuration
            conf.setLayoutDirection(Locale("fa"))
            resources.updateConfiguration(conf, resources.displayMetrics)
        } else {
            val conf = resources.configuration
            conf.setLayoutDirection(Locale("en"))
            resources.updateConfiguration(conf, resources.displayMetrics)
        }

        isSelected = false
        isLocation = false

        selectItemsFragment = OrderSelectItemsFragment(this)
        chooseLocationFragment = OrderChooseLocationFragment(this)
        chooseVisibleTimeFragment = OrderChooseVisibleTimeFragment(this)
        personalInformationFragment = OrderPersonalInformationFragment(this)
        summaryFragment = OrderSummaryFragment(this)

        isFromCart = intent.getBooleanExtra("order_cart", false)

        ft.replace(R.id.orderLoaderFL, LoadingFragment())
        ft.commit()

        if (checkRequestPermission()) {
            getMyLocation()
        }

        setupPager()

        // region font
        lang = Statics.getLanguageJSONObject(this).getJSONObject("makeOrderActivity")
        val myFont = StaticInformation().myFont(this)
        titleLayoutTV.typeface = myFont
        titleLayoutTV.text = lang.getString("title")
        previousTV.typeface = myFont
        previousTV.text = lang.getString("previous")
        nextTV.typeface = myFont
        nextTV.text = lang.getString("next")
        // endregion

        // region on click listeners
        orderBackIV.setOnClickListener(this)
        addOrderNextLL.setOnClickListener(this)
        addOrderPreviousLL.setOnClickListener(this)
        orderIcon1.setOnClickListener(this)
        orderIcon2.setOnClickListener(this)
        orderIcon3.setOnClickListener(this)
        orderIcon4.setOnClickListener(this)
        orderIcon5.setOnClickListener(this)
        // endregion

        getCartItems()
        getUserInfo()
    }

    @SuppressLint("ResourceType")
    private fun setHint() {
        startBTN.setOnClickListener {
            startBTN.visibility = View.GONE
            orderHintRL.setBackgroundResource(resources.getColor(android.R.color.transparent))
            orderHintRL.isClickable = false
            orderHintRL.isFocusable = false
            orderHintTV.startAnimation(StaticInformation().slideHint(this))
        }
    }

    private fun getUserInfo() {

        val queue = Volley.newRequestQueue(this)
        val request = object : JsonObjectRequest(Request.Method.GET, APIsURL().GET_USER_INFORMATION, null, {
            personName = it.getString("name")
            personPhone = if (it.getString("mobile") == "null") { "" } else { it.getString("mobile") }
            queue.cancelAll("info")
        }, {
            Log.e("error", "error")
            queue.cancelAll("info")
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=UTF-8"
                params["Authorization"] = "Bearer ${Statics.myToken}"
                return params
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

        }
        request.tag = "info"
        queue.add(request)
    }

    fun hideLoader() {
        orderLoaderFL.visibility = View.GONE
    }

    fun showLoader() {
        orderLoaderFL.visibility = View.VISIBLE
    }

    // region cart
    private fun getCartItems() {

        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, APIsURL().GET_CART, null, {
            setupCarts(it.getJSONArray("items"))
        }, {
            try {
                Log.e("error", it.networkResponse.statusCode.toString())
            } catch (err: Exception) {}
            queue.cancelAll("cart")
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=UTF-8"
                params["Authorization"] = "Bearer ${Statics.myToken}"
                return params
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

        }
        jsonObjectRequest.tag = "cart"
        queue.add(jsonObjectRequest)
    }

    private fun setupCarts(itemsJSON: JSONArray) {
        Log.e("cart items", itemsJSON.toString())

        Log.e("cart items", itemsJSON.toString())
        items.clear()
        selectItemsFragment.selectItemsLL.removeAllViewsInLayout()
        itemsHash.clear()
        offersHash.clear()

        try {
            for (i in 0 until itemsJSON.length()) {
                val item = itemsJSON.getJSONObject(i)
                val itemObject = ItemBasicModel(item.getString("itemID"), item.getString("itemName"),
                        "", "",
                        item.get("quantity") as Int, (item.get("unitNetPrice") as Double).toFloat(),
                        item.getString("itemThumbnail"), item.get("cashBack").toString(),
                        item.getString("accountID"), 0)
                itemObject.isFree = item.getBoolean("isFree")
                itemObject.discount = item.getInt("unitDiscount")
                itemObject.offerID = item.getString("offerID")

                when {
                    itemObject.isFree -> offersHash.add(itemObject)
                    itemObject.offerID.isNotEmpty() -> itemsHash[itemObject.offerID] = itemObject
                    else -> items.add(itemObject)
                }
            }
        } catch (err: Exception) {
            Log.e("error parse", err.message)
        }

        selectItemsFragment.setItems()
        isSelected = true
        hideLoader()
    }
    // endregion

    // region get location
    private fun checkRequestPermission(): Boolean {
        val permissionLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                val perms = HashMap<String, Int>()
                perms[Manifest.permission.ACCESS_FINE_LOCATION] = PackageManager.PERMISSION_GRANTED
                if (grantResults.isNotEmpty()) {
                    for (i in 0 until permissions.size) {
                        perms[permissions[i]] = grantResults[i]
                    }
                    if (perms[Manifest.permission.ACCESS_FINE_LOCATION] == PackageManager.PERMISSION_GRANTED) {
                        getMyLocation()
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showDialogOK("Permission Required", DialogInterface.OnClickListener { dialog, which ->
                                when (which) {
                                    DialogInterface.BUTTON_POSITIVE -> checkRequestPermission()
                                    DialogInterface.BUTTON_NEGATIVE -> checkRequestPermission()
                                }
                            })
                        }
                    }
                }
            }
        }
    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show()
    }

    private fun getMyLocation() {
        val locationManager: LocationManager? = getSystemService(LOCATION_SERVICE) as LocationManager?
        try {
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, mLocationListener)
        } catch(ex: SecurityException) {}
    }

    private var isFirstLocation = true
    private var mLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            if (isFirstLocation) {
                latLng = LatLng(location.latitude, location.longitude)
                chooseLocationFragment.setupMap(true)
                isFirstLocation = false
                isLocation = true
                if (isSelected && isLocation) {
                    hideLoader()
                }
            }
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }
    // endregion

    override fun onBackPressed() {

        if (!StaticInformation().isConnected(this) && orderLoaderFL.visibility == View.VISIBLE) {
            finish()
            if (!isFromCart) {
                startActivity(Intent(this, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            }
            return
        }

        val currentPosition = addOrderVP.currentItem
        if (currentPosition != 0) {
            addOrderVP.currentItem = currentPosition - 1
            return
        }

        val sureCancel = SureToDoDialog(this, Statics.getLanguageJSONObject(this).getJSONObject("dialogs").getJSONObject("sure").getString("cancelOrder"))
        sureCancel.show()
        sureCancel.setOnDismissListener {
            if (sureCancel.isTrue) {
                this.finish()
                if (!isFromCart) {
                    startActivity(Intent(this, MainActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                }
            }
        }
    }

    private fun setCurrentPage(position: Int) {
        when (position) {
            0 -> addOrderVP.currentItem = position
            1 -> checkSelected()
            2 -> checkLocation()
            3 -> checkTime()
            4 -> checkInformation()
        }
    }

    // region check fragments
    private fun checkSelected(): Boolean {
        finalSelectedItems.clear()
        for (item in items) {
            if (item.isChecked) {
                finalSelectedItems.add(item)
            }
        }

        for (item in itemsHash.values) {
            if (item.isChecked) {
                finalSelectedItems.add(item)
            }
        }

        for (offer in offersHash) {
            if (offer.isChecked) {
                finalSelectedItems.add(offer)
            }
        }

        if (finalSelectedItems.size != 0) {
            var total = 0
            for (item in finalSelectedItems) {
                total += item.quantity * item.price
            }
            if (total < 1000) {
                Toast.makeText(this, lang.getString("lessPrice"), Toast.LENGTH_SHORT).show()
                return false
            }

            addOrderVP.currentItem = 1
            return true
        }

        Toast.makeText(this, lang.getString("lessItems"), Toast.LENGTH_SHORT).show()
        return false
    }

    private fun checkLocation(): Boolean {
        if (!checkSelected()) {
            return false
        }

        if (latLng == null) {
            Toast.makeText(this, lang.getString("locationVal"), Toast.LENGTH_LONG).show()
            return false
        }

        chooseLocationFragment.setupMap(true)
        addOrderVP.currentItem = 2
        return true
    }

    private fun checkTime(): Boolean {
        if (!checkLocation()) {
            return false
        }
        addOrderVP.currentItem = 3
        return true
    }

    private fun checkInformation(): Boolean {
        if (!checkTime()) {
            return false
        }

        return if (personalInformationFragment.name.text.isNotEmpty()) {
            if (StaticInformation().isPhone(personalInformationFragment.phone.text.toString())) {
                personName = personalInformationFragment.name.text.toString()
                personPhone = personalInformationFragment.phone.text.toString()
                personRemark = personalInformationFragment.remark.text.toString()

                summaryFragment.name.text = personName
                summaryFragment.phone.text = personPhone
                summaryFragment.remark.text = personRemark
                addOrderVP.currentItem = 4
                true
            } else {
                Toast.makeText(this, lang.getString("phoneVal"), Toast.LENGTH_LONG).show()
                false
            }
        } else {
            Toast.makeText(this, lang.getString("nameVal"), Toast.LENGTH_LONG).show()
            false
        }
    }
    // endregion

    private fun setupPager() {
        val adapter = MakeOrderPagerAdapter(supportFragmentManager)
        adapter.addFragment(selectItemsFragment)
        adapter.addFragment(chooseLocationFragment)
        adapter.addFragment(chooseVisibleTimeFragment)
        adapter.addFragment(personalInformationFragment)
        adapter.addFragment(summaryFragment)
        addOrderVP.adapter = adapter

        addOrderVP.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

            override fun onPageSelected(p0: Int) {

                addOrderPreviousLL.visibility = View.VISIBLE
                addOrderNextLL.visibility = View.VISIBLE

                setGrayIcons()
                when (p0) {
                    0 ->  {
                        orderIcon1.setImageResource(R.drawable.ic_order_list)
                        addOrderPreviousLL.visibility = View.GONE
                    }
                    1 -> orderIcon2.setImageResource(R.drawable.ic_loc)
                    2 -> orderIcon3.setImageResource(R.drawable.ic_av_timer)
                    3 -> orderIcon4.setImageResource(R.drawable.ic_profile)
                    4 ->  {
                        orderIcon5.setImageResource(R.drawable.ic_check)
                        addOrderNextLL.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun setGrayIcons() {
        orderIcon1.setImageResource(R.drawable.ic_order_list_gray)
        orderIcon2.setImageResource(R.drawable.ic_loc_gray)
        orderIcon3.setImageResource(R.drawable.ic_av_timer_gray)
        orderIcon4.setImageResource(R.drawable.ic_profile_gray)
        orderIcon5.setImageResource(R.drawable.ic_check_gray)
    }

}
