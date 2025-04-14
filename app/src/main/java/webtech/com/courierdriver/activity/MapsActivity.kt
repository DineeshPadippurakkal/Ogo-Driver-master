package webtech.com.courierdriver.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import webtech.com.courierdriver.R
import webtech.com.courierdriver.utilities.PreferenceHelper

class MapsActivity : MasterAppCombatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var preferenceHelper: PreferenceHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // fixing portrait mode problem for SDK 26 if using windowIsTranslucent = true
      /*  if (Build.VERSION.SDK_INT == 26) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }*/

        preferenceHelper = PreferenceHelper(this@MapsActivity)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
        mMap.getUiSettings().setMyLocationButtonEnabled(true)
        mMap.getUiSettings().setZoomControlsEnabled(true)


        // Add a marker in driver current location  and move the camera


        if( (preferenceHelper!!.lastLat!=null) && (preferenceHelper!!.lastLong!=null) )
        {

            val driverCurrentLOcation = LatLng(preferenceHelper!!.lastLat!!.toDouble(), (preferenceHelper!!.lastLong!!.toDouble()))

            mMap.addMarker(MarkerOptions().position(driverCurrentLOcation).title("Driver Current Location"))

            //mMap!!.moveCamera(CameraUpdateFactory.newLatLng(driverCurrentLOcation))
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(driverCurrentLOcation.latitude, driverCurrentLOcation.longitude), 14F))



        }



    }


    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(this@MapsActivity, MainActivity::class.java)
        startActivity(i)
        finish()

    }


    public override fun onResume() {
        super.onResume()
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    }

    public override fun onPause() {
        super.onPause()
        ///these below two line will disable landscape mode ,check onResume() also
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

    }




}
