package com.smartlife_solutions.android.navara_store.OrderFragments

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.smartlife_solutions.android.navara_store.Dialogs.LocationRemarkDialog
import com.smartlife_solutions.android.navara_store.MarkerInfoWindow
import com.smartlife_solutions.android.navara_store.OrdersActivity

import com.smartlife_solutions.android.navara_store.R
import com.smartlife_solutions.android.navara_store.StaticInformation

@SuppressLint("ValidFragment")
class OrderChooseLocationFragment(var activity: OrdersActivity) : Fragment(), OnMapReadyCallback {

    lateinit var searchET: AutoCompleteTextView
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_choose_location, container, false)

        searchET = view.findViewById(R.id.searchET)
        searchET.typeface = StaticInformation().myFont(context)
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
            googleMap.setInfoWindowAdapter(MarkerInfoWindow(context!!, (searchET.text.toString() + "\n" + activity.locationRemarkText).trim()))
            googleMap.addMarker(MarkerOptions().position(activity.latLng!!)).showInfoWindow()
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(activity.latLng, StaticInformation().ZOOM_VAL))
        } catch (err: Exception) {}
    }

}
