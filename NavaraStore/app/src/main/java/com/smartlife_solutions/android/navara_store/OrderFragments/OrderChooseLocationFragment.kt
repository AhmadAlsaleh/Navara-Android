package com.smartlife_solutions.android.navara_store.OrderFragments

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.smartlife_solutions.android.navara_store.*
import com.smartlife_solutions.android.navara_store.Dialogs.LocationRemarkDialog
import com.smartlife_solutions.android.navara_store.R

@SuppressLint("ValidFragment")
class OrderChooseLocationFragment(var activity: OrdersActivity) : Fragment(), OnMapReadyCallback {

    lateinit var searchET: AutoCompleteTextView
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var chooseLocationPBRL: RelativeLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_choose_location, container, false)

        chooseLocationPBRL = view.findViewById(R.id.chooseLocationPBRL)
        searchET = view.findViewById(R.id.searchET)

        val locationHintsTV = view.findViewById<TextView>(R.id.locationHintsTV)
        locationHintsTV.typeface = StaticInformation().myFont(context)
        searchET.typeface = StaticInformation().myFont(context)

        val lang = Statics.getLanguageJSONObject(activity).getJSONObject("makeOrderActivity").getJSONObject("locationFragment")
        searchET.hint = lang.getString("hintSearch")
        locationHintsTV.text = lang.getString("hint")

        searchET.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.action == KeyEvent.ACTION_DOWN
                    || event.action == KeyEvent.KEYCODE_ENTER) {
                geoLocate()
            }
            true
        }
        searchET.setOnClickListener {
            searchET.setText("")
        }

        searchET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                activity.locationText = s?.toString()!!
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        if (activity.latLng != null) {
            setupMap(true)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.chooseLocationF)
        if (mapView != null) {
            mapView.onCreate(null)
            mapView.onResume()
            mapView.getMapAsync(this)
        }

    }

    override fun onMapReady(gMap: GoogleMap?) {
        MapsInitializer.initialize(context)
        googleMap = gMap!!
        setupMap()
        googleMap.setOnInfoWindowClickListener {
            LocationRemarkDialog(context!!, searchET.text.toString() + "\n" + activity.locationRemarkText, this, activity.summaryFragment, activity).show()
        }

        googleMap.setOnMapClickListener {
            activity.latLng = it
            setupMap(true)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun geoGetAddress() {
        try {
            val geoCoder = Geocoder(context)
            val address = geoCoder.getFromLocation(activity.latLng?.latitude!!, activity.latLng?.longitude!!, 1)[0]
            searchET.setText(address.getAddressLine(0))
            chooseLocationPBRL.visibility = View.GONE
        } catch (err: Exception) {}
    }

    private fun geoLocate() {
        try {
            StaticInformation().hideKeyboard(activity)
            val geoCoder = Geocoder(context)
            val addressList: List<Address>
            try {
                addressList = geoCoder.getFromLocationName(searchET.text.toString(), 1)
                if (addressList.isNotEmpty()) {
                    activity.latLng = LatLng(addressList[0].latitude, addressList[0].longitude)
                    setupMap()
                } else {
                    Toast.makeText(context, "Not found", Toast.LENGTH_LONG).show()
                    chooseLocationPBRL.visibility = View.GONE
                }
            } catch (err: Exception) {
            }
        } catch (err: Exception) {}
    }

    fun setupMap(isSearch: Boolean = false) {
        if (isSearch) {
            geoGetAddress()
        }
        try {
            googleMap.clear()
            googleMap.setInfoWindowAdapter(MarkerInfoWindow(context!!, (searchET.text.toString() + "\n" + activity.locationRemarkText).trim(), lang = Statics.getLanguageJSONObject(activity).getJSONObject("dialogs").getJSONObject("locationRemark")))
            googleMap.addMarker(MarkerOptions().position(activity.latLng!!)).showInfoWindow()
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(activity.latLng, StaticInformation().ZOOM_VAL))
        } catch (err: Exception) {}
    }

}
