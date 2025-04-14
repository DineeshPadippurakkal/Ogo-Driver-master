package webtech.com.courierdriver.service

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import org.greenrobot.eventbus.EventBus
import webtech.com.courierdriver.LocationUtils.LocationServiceHelper
import webtech.com.courierdriver.R
import webtech.com.courierdriver.activity.MainActivity
import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.events.LocationChangedEvent
import webtech.com.courierdriver.events.PermissionFailedEvent
import webtech.com.courierdriver.firebase.UpdateRealTimeDriverApplicationStatus
import webtech.com.courierdriver.firebase.UpdateRealTimeDriverDetails
import webtech.com.courierdriver.firebase.UpdateRealTimeDriverOnlineStatus
import webtech.com.courierdriver.firebase.UpdateRealTimeDriverOrderStatus
import webtech.com.courierdriver.utilities.Language
import webtech.com.courierdriver.utilities.LogUtils
import webtech.com.courierdriver.utilities.PreferenceHelper
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/*
Created by ​
Hannure Abdulrahim


on 7/31/2019.
 */


/* This class will perform two task
 * 1) Update driver location in  real time fire base database
 * 2) Update driver location by calling web service on server side
 * 3) //location for ( both ) >= Android 8 (Oreo) and  < Android 8 (Oreo)
 * 4) here some time few nodes are deleting hence updating all nodes in realtime firebase database
 * [ var APP_STATUS_NODE = "appSt"
    var DRIVER_DETAILS_NODE = "drD"
    var LOCATION_NODE = "lo"
    var ONLINE_STATUS_NODE = "onSt"
    var ORDER_STATUS_NODE = "orSt"]
 * */


class LocationForegroundService : Service(), LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {




    private var mLocationRequest: LocationRequest? = null

    //private val fusedLocationProviderApi = LocationServices.FusedLocationApi
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var preferenceHelper: PreferenceHelper? = null
    var FASTEST_INTERVAL = OGoConstant.LOCATION_UPDATE_PERIOD_MSEC // use whatever suits you
    var currentLocation: Location? = null
    var locationUpdatedAt = java.lang.Long.MIN_VALUE
    private var servicesAvailable = false

    private var shouldStop = false


    companion object {
        val CHANNEL_ID = javaClass.simpleName
        val notificationChannelName = javaClass.simpleName+" Channel"

        var isLocationForegroundServiceStarted = false

    }
    internal class Constants {
        interface ACTION {
            companion object {
                val MAIN_ACTION = "webtech.com.courierdriver.action.main"
                val STARTFOREGROUND_ACTION = "webtech.com.courierdriver.action.startforeground"
                val STOPFOREGROUND_ACTION = "webtech.com.courierdriver.action.stopforeground"

            }
        }
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    override fun onCreate() {
        super.onCreate()


        setLocationRequest()
        servicesAvailable = servicesConnected()
        setUpLocationClientIfNeeded()

        isLocationForegroundServiceStarted = true


    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        ////check intent is null or not its crashing becouse of this
        // use Intent? instead of Intent  with handle this issue
//        if (null == intent || null == intent.action) {
//            val source = if (null == intent) "intent" else "action"
//            LogUtils.error(LogUtils.TAG, source + " was null, flags=" + flags + " bits=" + Integer.toBinaryString(flags))
//            LogUtils.error(LogUtils.TAG, "intent is null in "+ javaClass.simpleName )
//
//        }




        intent?.let {

            if (intent.action.equals(Constants.ACTION.STARTFOREGROUND_ACTION))
            {
                LogUtils.error(LogUtils.TAG, javaClass.simpleName + " Received Start  Foreground Intent...")
                // your start service code
                setUpNotifcationChannelIfNeeded(intent)
                if (!servicesAvailable || mGoogleApiClient!!.isConnected())
                    return START_STICKY
                setUpLocationClientIfNeeded()

                if (mGoogleApiClient != null) {
                    if (!mGoogleApiClient!!.isConnected || !mGoogleApiClient!!.isConnecting()) {
                        mGoogleApiClient!!.connect()
                    }
                }



            }else if (intent.getAction().equals( Constants.ACTION.STOPFOREGROUND_ACTION)){

                LogUtils.error(LogUtils.TAG, javaClass.simpleName + " Received Stop Foreground Intent...")
                ///first stop location update callback
                stopLocationUpdates()
                //your end service code

                stopForeground(true);
                //stopSelfResult(startId);
                stopSelf()


            }
        }




        //return START_NOT_STICKY
        return START_STICKY
        //return super.onStartCommand(intent, flags, startId)


    }


//if you call stopService(), then the method onDestroy() in the service is called

    override fun onDestroy() {
        stopForeground(true)
        LogUtils.error(LogUtils.TAG, javaClass.simpleName + " stopped...")

        isLocationForegroundServiceStarted = false

    }



    /**
     * Create a new location client, using the enclosing class to
     * handle callbacks.
     */
    @Synchronized
    protected fun buildGoogleApiClient() {
        this.mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
    }

    private fun setUpLocationClientIfNeeded() {
        if (mGoogleApiClient == null)
            buildGoogleApiClient()
    }

    private fun servicesConnected(): Boolean {
        // Check that Google Play services is available
        val resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        // If Google Play services is available

        return ConnectionResult.SUCCESS == resultCode
    }


    internal var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {

            val mLastLocation = locationResult!!.lastLocation


            LogUtils.error(LogUtils.TAG, "${javaClass.simpleName} : onLocationResult :  mLastLocation.latitude :  " + mLastLocation.latitude)
            LogUtils.error(LogUtils.TAG, "${javaClass.simpleName} : onLocationResult :  mLastLocation.longitude :  " + mLastLocation.longitude)
//            for (currentLocation in locationResult!!.locations) {
//                // Update UI with location data
//                LogUtils.error(LogUtils.TAG, "onLocationResult :  currentLocation.latitude :  " + currentLocation.latitude)
//                LogUtils.error(LogUtils.TAG, "onLocationResult :  currentLocation.longitude :  " + currentLocation.longitude)
//
//            }

            if (mLastLocation != null)
                updateLocationHere(mLastLocation)


        }

    }


    // private double tempLat= 29.3663148;
    //private double tempLon= 47.9679723;


    override fun onConnected(connectionHint: Bundle?) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            EventBus.getDefault().post(PermissionFailedEvent())
            return
        }

        try {

            getLastLocation()
            startLocationUpdates()

        } catch (e: SecurityException) {
            LogUtils.error(LogUtils.TAG, e.printStackTrace().toString())

        } catch (e: Exception) {
            LogUtils.error(LogUtils.TAG, e.printStackTrace().toString())

        }


    }

    override fun onConnectionSuspended(i: Int) {

        LogUtils.info(LogUtils.TAG, "Suspended$i")
        LogUtils.info(LogUtils.TAG, "DriverLocation " + javaClass.simpleName + "Location Suspended")

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

        LogUtils.info(LogUtils.TAG, "Failed" + connectionResult.errorCode)
        LogUtils.info(LogUtils.TAG, "DriverLocation " + javaClass.simpleName + "Location Failed")

    }





    override fun onLocationChanged(location: Location?) {
        if (location != null) {

            //// no update location request via  made by fusedLocationProviderApi  so this will never call
            /// instead update location request made  fusedLocationProviderClient so location cal back control will go to onLocationResult
            LogUtils.error(LogUtils.TAG, "onLocationChanged :  location.latitude :  " + location.latitude)
            LogUtils.error(LogUtils.TAG, "onLocationChanged :  location.longitude :  " + location.longitude)

        }


    }


    private fun setUpNotifcationChannelIfNeeded(intent: Intent?) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            // from here service will start as Foreground Service ( user will see notification always)
            val input = intent?.getStringExtra("inputExtra")
            createNotificationChannel()

            showNotification(getString(R.string.location_note_title),getString(R.string.location_note))



            //do heavy work on a background thread
            //stopSelf();

        }else{

        }

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                    CHANNEL_ID,
                    notificationChannelName,
                    NotificationManager.IMPORTANCE_LOW // will mute sound
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }



    @RequiresApi(Build.VERSION_CODES.N)
    private fun showNotification(contentTitleStr:String, contentMsgStr : String )
    {

        var preferenceHelper: PreferenceHelper? = null
        preferenceHelper = PreferenceHelper(this)
        var notification: Notification? = null

        if (preferenceHelper!!.language == Language.ENGLISH) {

            notification = buildNotification(contentTitleStr,contentMsgStr)

        } else {
            notification = buildNotification(contentTitleStr,contentMsgStr)
        }


        startForeground(OGoConstant.NOTIFICATION_FORGROUND_SERVICE_ID, notification)


    }

    private fun buildNotification(contentTitleStr:String ,contentMsgStr : String): Notification? {

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent,  PendingIntent.FLAG_IMMUTABLE)

       return NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(contentTitleStr)//
                .setContentText(contentMsgStr)
                .setSmallIcon(R.mipmap.delivery_launcher)
                // You must set the priority to support Android 7.1 and lower
                .setPriority(NotificationCompat.PRIORITY_LOW) // Set priority to PRIORITY_LOW to mute notification sound
                .setContentIntent(pendingIntent)
                .build()
    }




    private fun setLocationRequest() {
        mLocationRequest = LocationRequest.create()
        //mLocationRequest!!.setInterval(1 * 1000);// 1s
        mLocationRequest!!.setInterval(OGoConstant.LOCATION_UPDATE_INTERVEL);//  sec
        //mLocationRequest!!.interval = 500// 0.5s (half sec.)

        //make it 7 seconds
        //mLocationRequest!!.interval = OGoConstant.LOCATION_UPDATE_PERIOD_MSEC
        //mLocationRequest!!.setFastestInterval(100);
        //mLocationRequest!!.interval = OGoConstant.LOCATION_UPDATE_PERIOD_MSEC
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }




    /**
     * Provides a simple way of getting a device's location and is well suited for
     * applications that do not require a fine-grained location and that do not need location
     * updates. Gets the best and most recent location currently available, which may be null
     * in rare cases when a location is not available.
     * <p>
     * Note: this method should be called after location permission has been granted.
     */
    @SuppressWarnings("MissingPermission")
    private fun getLastLocation() {


        //val mLastLocation = fusedLocationProviderApi.getLastLocation(mGoogleApiClient)
        //        if (mLastLocation != null) {
//            EventBus.getDefault().post(LocationChangedEvent(mLastLocation.latitude, mLastLocation.longitude))
//        }


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val addOnCompleteListener = fusedLocationProviderClient!!.lastLocation.addOnCompleteListener {

            if (it.isSuccessful() && it.getResult() != null) {
                val mLastLocation = it.getResult()
                EventBus.getDefault().post(LocationChangedEvent(mLastLocation!!.latitude, mLastLocation!!.longitude))

            } else {
                LogUtils.error(LogUtils.TAG, " LOCATION : no location detected")


            }
        }

    }


    private fun stopLocationUpdates() {
        if(fusedLocationProviderClient!=null)
        fusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
    }


    /**
     * Note: this method should be called after location permission has been granted.
     */
    @SuppressWarnings("MissingPermission")
    private fun startLocationUpdates() {
        //fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)

        // fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
        fusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, null)

    }

    private fun updateLocationHere(location: Location?) {

        preferenceHelper = PreferenceHelper(this)
        if (((preferenceHelper!!.lastUsername != null)) && (preferenceHelper!!.driverOnlineStatus.equals(OGoConstant.ONLINE, true))) {
            ////////////////////////////////////
            //// Call web service to update location
            //// Call web service of PACI
            //// Only When driver is online
            ////////////////////////////////////
            LocationServiceHelper.getInstance(this).driverLocationPost(preferenceHelper!!.lastUsername!!, location!!.latitude.toString(), location!!.longitude.toString())
            //Toast.makeText(getApplicationContext(), "Your Location is - \n Lat:"+ location!!.getLatitude() + "\n Long: " +location!!.getLongitude() , Toast.LENGTH_LONG).show();
            EventBus.getDefault().post(LocationChangedEvent(location!!.latitude, location!!.longitude))



            /// logic for testing  wih no delay
//            currentLocation = location
//            if(true)

            ////////////////////////////////////
            //logic to update location after some deplay
            ///////////////////////////////////

            var updateLocationandReport = false
            if (currentLocation == null) {
                currentLocation = location
                locationUpdatedAt = System.currentTimeMillis()
                updateLocationandReport = true

            } else {
                val secondsElapsed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - locationUpdatedAt)
                LogUtils.error(LogUtils.TAG, " secondsElapsed :  " + secondsElapsed)
                LogUtils.error(LogUtils.TAG, "locationUpdatedAt :  " + locationUpdatedAt)


                if (secondsElapsed >= TimeUnit.MILLISECONDS.toSeconds(FASTEST_INTERVAL!!)) {
                    LogUtils.error(LogUtils.TAG, "secondsElapsed IN SIDE IF  :  " + secondsElapsed)
                    // check location accuracy here
                    currentLocation = location
                    locationUpdatedAt = System.currentTimeMillis()
                    updateLocationandReport = true

                }



            }

            //////// send your location to firebase after delay
            if (updateLocationandReport) {


                LogUtils.error(LogUtils.TAG, "DriverLocation " + javaClass.simpleName + " #Location Changed")
                LogUtils.error(LogUtils.TAG, " DriverLocation currentLocation!!.getLatitude() => " + currentLocation!!.latitude)
                LogUtils.error(LogUtils.TAG, " DriverLocation currentLocation!!.getLongitude() => " + currentLocation!!.longitude)
                LogUtils.error(LogUtils.TAG, " DriverLocation currentLocation!!.speed => " + currentLocation!!.speed)


                val currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
                LogUtils.error(LogUtils.TAG, "currentDate => " + currentDate)


                //// update location in real time fire base database
                ////here if driver is offline then update only online status and onlineDateTime only
                /// after offline if driver is logout then taken necessary action

                ////
                if (preferenceHelper!!.lastUsername != null)
                    LocationServiceHelper.getInstance(this).updateRealTimeDriverLocation(currentLocation!!, this)
                //// sometimes other nodes are not updating in firebase or deleting is automatically
                //// so update all driver firebase node here once again
                //// also so update all driver firebase node when online status changes

                preferenceHelper!!.appStatus?.let {
                    UpdateRealTimeDriverApplicationStatus.updateRealTimeDriverApplicationStatus(this, it)
                }
                preferenceHelper?.let {
                    UpdateRealTimeDriverDetails.updateDriverDetails(this)
                }
                preferenceHelper!!.driverOnlineStatus.let {
                    UpdateRealTimeDriverOnlineStatus.updateDriverOnlineStatus(this, it)
                }
                preferenceHelper!!.orderStatus?.let {
                    UpdateRealTimeDriverOrderStatus.updateRealTimeDriverOrderStatus(this, it )
                }


            }



        }



    }


}