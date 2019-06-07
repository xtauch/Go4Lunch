package tauch.xavier.go4lunch.fragments

import android.Manifest
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*


import tauch.xavier.go4lunch.R

import com.google.android.gms.maps.SupportMapFragment
import android.widget.Toast

import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import tauch.xavier.go4lunch.BuildConfig
import tauch.xavier.go4lunch.activities.RestaurantActivity
import tauch.xavier.go4lunch.models.search.SearchResponse
import tauch.xavier.go4lunch.utils.GooglePlacesStream
import java.util.ArrayList


/**
 * Created by Xavier TAUCH on 25/03/2019.
 */
class MapFragment :
    Fragment(),
    OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener {

    private lateinit var deviceCurrentPosition: LatLng

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var mDisposable: Disposable? = null

    var mPreferences: SharedPreferences? = null

    lateinit var mMarker: Marker
    var mMarkerList: MutableList<Marker> = ArrayList()

    private lateinit var mMap: GoogleMap

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.maps, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //Create SharedPreferences File
        mPreferences = activity!!.applicationContext.getSharedPreferences(PREFS_MAPS, MODE_PRIVATE)

        //Create location services client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!.applicationContext)

        //Check if user allows us to use his location
        if (ContextCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location ->
                    // Got last known location. In some rare situations this can be null.
                    mPreferences!!.edit().clear().apply()
                    mPreferences!!.edit().putString(LOCATION_LAT, location.latitude.toString()).apply()
                    mPreferences!!.edit().putString(LOCATION_LNG, location.longitude.toString()).apply()
                }
        }
        return view
    }





    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Before enabling the My Location layer, you must request location permission from the user
        if (ContextCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            mMap.setOnMyLocationButtonClickListener(this)
            mMap.setOnMyLocationClickListener(this)

            val userLat: Double = mPreferences?.getString(LOCATION_LAT, "48.864716")!!.toDouble()
            val userLng: Double = mPreferences?.getString(LOCATION_LNG, "2.349014")!!.toDouble()
            deviceCurrentPosition = LatLng(userLat,userLng)

        }

        // Draw Markers
        executeHttpNearbyRestaurantsRequest()

        // Move the camera instantly to location with a zoom of 15.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deviceCurrentPosition, 15f))
    }


    override fun onMyLocationClick(location: Location) {
        Toast.makeText(activity!!.applicationContext, "Current location:\n$location", Toast.LENGTH_LONG).show()
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(activity!!.applicationContext, "MyLocation button clicked", Toast.LENGTH_SHORT).show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

       /** Called when the user clicks a marker. */

    override fun onMarkerClick(tMarker: Marker): Boolean {
           startActivity(Intent(context, RestaurantActivity::class.java))
            tMarker.tag




        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false
    }

    // ------------------------------
    //  HTTP REQUEST (RxJAVA)
    // ------------------------------


    // Execute our GooglePlacesStream
    private fun executeHttpNearbyRestaurantsRequest() {
        // Execute the stream subscribing to Observable defined inside GooglePlacesStream
        this.mDisposable =
            GooglePlacesStream.fetchNearbyRestaurants("","restaurant","distance", BuildConfig.API_KEY_GOOGLE_PLACES).subscribeWith(object : DisposableObserver<SearchResponse>() {

                override fun onNext(searchResponse: SearchResponse) {
                    Log.e("ListViewF - onNext", "On Next ListViewFragment")

                    for (result in searchResponse.results) {
                    drawRestaurantMarker(result.geometry!!.location)
                    makeMarkerList(drawRestaurantMarker(result.geometry.location))
                    giveTag()
                    }

                }


                override fun onError(e: Throwable) {
                    Log.e("ListViewF - onError", Log.getStackTraceString(e))
                    Toast.makeText(context, "Une erreur est survenue ", Toast.LENGTH_LONG).show()
                }

                override fun onComplete() {
                    Log.e("ListViewF - onComplete", "On Complete ListViewFragment")

                }
            })
    }


    fun drawRestaurantMarker(restaurantLocation: tauch.xavier.go4lunch.models.search.Location): Marker{
        return mMap.addMarker(MarkerOptions()
            .position(LatLng(restaurantLocation.lat, restaurantLocation.lng))
            .title("Marker in "))
    }


    fun setMarker(marker: Marker, mTag: Int){
        marker.tag = mTag
    }

    fun giveTag(){
        val count = 0
        for (marker in mMarkerList){
            setMarker(marker, count.inc())
        }
    }

    fun makeMarkerList(marker: Marker){
        mMarkerList.add(marker)
    }

    companion object {

        const val PREFS_MAPS = "com.go4lunch.maps.prefs"
        const val LOCATION_LAT = "userLat"
        const val LOCATION_LNG = "userLng"

        fun newInstance(): MapFragment {
            return MapFragment()
        }
    }
}