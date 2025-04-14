package webtech.com.courierdriver.utilities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager

import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Result
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.*


/*
Created by ​
Hannure Abdulrahim


on 6/12/2018.
 */
object GPSUtility {

    val LOCATION = 0x1
    val GPS_SETTINGS = 0x7//GPS Setting

    val MY_PERMISSIONS_REQUEST_LOCATION = 99

    ///Ask user for location permission

    fun askForPermission(permission: String, requestCode: Int?, context: Context): Boolean {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again


                ActivityCompat.requestPermissions(context, arrayOf(permission), requestCode!!)


            } else {

                ActivityCompat.requestPermissions(context, arrayOf(permission), requestCode!!)
            }
            return false


        } else {

            //Toast.makeText(context, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show()
            return true

        }
    }



    fun isLocationPermissionGranted(context: Context): Boolean {
        if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return true
        }



    fun checkLocationPermission(context: Context): Boolean {
        if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(context as Activity,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(context as Activity)
                        .setTitle("title_location_permission")
                        .setMessage("text_location_permission")
                        .setPositiveButton("ok", DialogInterface.OnClickListener { dialogInterface, i ->
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(context as Activity,
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                    MY_PERMISSIONS_REQUEST_LOCATION)
                        })
                        .create()
                        .show()


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(context as Activity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        MY_PERMISSIONS_REQUEST_LOCATION)
            }
            return false
        } else {
            return true
        }
    }


    //// This method will generate dialog box if GPS is not enable and ask user to enable it.
    fun askForGPS(client: GoogleApiClient, context: Context) {

        val UPDATE_INTERVAL = 10000  /* 10 secs */
        val FASTEST_INTERVAL: Long = 5000 /* 5 sec */

        var result: PendingResult<LocationSettingsResult>
        var mLocationRequest: LocationRequest
        mLocationRequest = LocationRequest.create()

        //mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        //mLocationRequest.setInterval(30 * 1000)
        //mLocationRequest.setFastestInterval(5 * 1000)

        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER)
        mLocationRequest.setInterval(UPDATE_INTERVAL.toLong())
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL)

        val builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
        builder.setAlwaysShow(true)
        result = LocationServices.SettingsApi.checkLocationSettings(client, builder.build())

        result.setResultCallback(ResultCallback<Result> { result ->
            val status = result.getStatus()
            when (status.getStatusCode()) {
                LocationSettingsStatusCodes.SUCCESS -> {

                }
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                    status.startResolutionForResult(context as Activity, GPS_SETTINGS)
                } catch (e: IntentSender.SendIntentException) {

                }

                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                }
            }
        })

    }

    fun isLocationEnabled(context: Context): Boolean {
        lateinit var locationManager: LocationManager
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    fun isPlayServicesAvailable(context: Context): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(context)
        return ConnectionResult.SUCCESS == status
    }






}