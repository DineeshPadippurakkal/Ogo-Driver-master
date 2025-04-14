package webtech.com.courierdriver.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.location.GpsStatus
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.NotificationCompat
import webtech.com.courierdriver.LocationUtils.LocationServiceHelper
import webtech.com.courierdriver.R
import webtech.com.courierdriver.activity.MainActivity
import webtech.com.courierdriver.constants.ApiConstant
import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.utilities.Language
import webtech.com.courierdriver.utilities.LogUtils
import webtech.com.courierdriver.utilities.PreferenceHelper

/*
Created by ​
Hannure Abdulrahim


on 8/13/2020.
 */
////This service is written only to feed data to PACI web service
class LocationForegroundServicePACI : Service(), LocationListener {
    var isGPSEnabled = false // flag for GPS satellite status
    var isNetworkEnabled = false // flag for cellular network status
    var canGetLocation = false // flag for either cellular or satellite status
    private var mGpsStatus: GpsStatus? = null
    protected var locationManager: LocationManager? = null
    protected var gpsListener = GpsListener()
    var location :Location?= null    // location
    var lastLocation:Location?=null
    var dLatitude = 0.0
    var dAltitude = 0.0
    var dLongitude = 0.0
    var dAccuracy = 0.0
    var dSpeed = 0.0
    var dSats = 0.0
    var fAccuracy = 0f
    var fSpeed = 0f
    var lSatTime // satellite time
            : Long = 0
    var szSignalSource: String? = null
    var szAltitude: String? = null
    var szAccuracy: String? = null
    var szSpeed: String? = null
    var szSatellitesInUse: String? = null
    var szSatellitesInView: String? = null




    companion object {
        val CHANNEL_ID = javaClass.simpleName
        val notificationChannelName = javaClass.simpleName+" Channel"

        var szSatTime: String? = null
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 0 // 0 meters
        private const val MIN_TIME_BW_UPDATES: Long = 5*1000 /// 5 seconds ///1000 //1 second

        val MAIN_ACTION = "webtech.com.courierdriver.action.main"
        val STARTFOREGROUND_ACTION = "webtech.com.courierdriver.action.startforeground"
        val STOPFOREGROUND_ACTION = "webtech.com.courierdriver.action.stopforeground"

        var isLocationForegroundServicePACIStarted = false

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

        isLocationForegroundServicePACIStarted = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        ////check intent is null or not its crashing becouse of this
        // use Intent? instead of Intent  with handle this issue
//        if (null == intent || null == intent.action) {
//            val source = if (null == intent) "intent" else "action"
//            LogUtils.error(LogUtils.TAG, source + " was null, flags=" + flags + " bits=" + Integer.toBinaryString(flags))
//            LogUtils.error(LogUtils.TAG, "intent is null in "+ javaClass.simpleName )
//
//
//        }

        if (intent!!.action.equals(LocationForegroundService.Constants.ACTION.STARTFOREGROUND_ACTION))
        {
            LogUtils.error(LogUtils.TAG, javaClass.simpleName + " Received Start  Foreground Intent...")
            // your start service code
            setUpNotifcationChannelIfNeeded(intent)
            getUserLocation()


        }else if (intent.getAction().equals( LocationForegroundService.Constants.ACTION.STOPFOREGROUND_ACTION)){
            LogUtils.error(LogUtils.TAG, javaClass.simpleName + " Received Stop Foreground Intent...")

            ///first stop location update callback
            stopLocationUpdates()

            //your end servce code
            stopForeground(true);
           // stopSelfResult(startId);
            stopSelf()
        }



        return START_NOT_STICKY

       // return  START_STICKY
        //return super.onStartCommand(intent, flags, startId)

    }

    override fun onDestroy() {
        super.onDestroy()
        isLocationForegroundServicePACIStarted = false
        LogUtils.error(LogUtils.TAG, javaClass.simpleName + " stopped...")
    }

    override fun onLocationChanged(location: Location) {

        if(location!=null)
        {
            LogUtils.error(LogUtils.TAG, ": ${javaClass.simpleName} :location.latitude "+ location!!.latitude)
            LogUtils.error(LogUtils.TAG, " : ${javaClass.simpleName} :location.longitude  "+ location!!.longitude)

            getSatellitesInfo(location)

        }



    }
    override fun onStatusChanged(s: String, i: Int, bundle: Bundle?) {}
    override fun onProviderEnabled(s: String) {}
    override fun onProviderDisabled(s: String) {}

    class GpsListener : GpsStatus.Listener {
        override fun onGpsStatusChanged(event: Int) {}
    }



    private fun stopLocationUpdates() {
        if(locationManager!=null)
        locationManager!!.removeUpdates(this)
    }

    /*///this method should call only if you have below permission granted
    Manifest.permission.ACCESS_FINE_LOCATION
    Manifest.permission.ACCESS_COARSE_LOCATION

     */
    @SuppressLint("MissingPermission")
    fun getUserLocation(): Location? {
        try {
            locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) // getting GPS satellite status
            isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER) // getting cellular network status
            if (!isGPSEnabled && !isNetworkEnabled) {
            } else {
                canGetLocation = true
                if (isNetworkEnabled) { //GPS is enabled, getting lat/long via cellular towers


//                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                        // TODO: Consider calling
//                        //    ActivityCompat#requestPermissions
//                        // here to request the missing permissions, and then overriding
//                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                        //                                          int[] grantResults)
//                        // to handle the case where the user grants the permission. See the documentation
//                        // for ActivityCompat#requestPermissions for more details.
//                        return TODO;
//                    }
                    locationManager!!.addGpsStatusListener(gpsListener) //inserted new
                    locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                    LogUtils.error(LogUtils.TAG,"Cell tower"+ "Cell tower")
                    if (locationManager != null) {
                        location = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location != null) {
                            szAltitude = " NA (using cell towers)"
                            szSatellitesInView = " NA (using cell towers)"
                            szSatellitesInUse = " NA (using cell towers)"
                        }
                    }
                }
                if (isGPSEnabled) {
                    //GPS is enabled, getting lat/long via satellite

                    //if (location == null) {
                        assert(locationManager != null)
                        locationManager!!.addGpsStatusListener(gpsListener) //inserted new
                        locationManager!!.getGpsStatus(null)
                        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                    LogUtils.error(LogUtils.TAG,"GPS Enabled"+"GPS Enabled")

                    if (locationManager != null) {
                        val location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                        getSatellitesInfo(location)

                        }
                   // }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return location
    }







    private fun setUpNotifcationChannelIfNeeded(intent: Intent?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            /// no need of notification handler
            // let service start as background


        } else {

            // from here service will start as Foreground Service ( user will see notification always)
            val input = intent?.getStringExtra("inputExtra")
            createNotificationChannel()

            showNotification(getString(R.string.location_note_title),getString(R.string.location_note))



            //do heavy work on a background thread
            //stopSelf();

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


    private fun showNotification( contentTitleStr:String ,contentMsgStr : String )
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


        return NotificationCompat.Builder(this, LocationForegroundService.CHANNEL_ID)
                .setContentTitle(contentTitleStr)//
                .setContentText(contentMsgStr)
                .setSmallIcon(R.mipmap.delivery_launcher)
                // You must set the priority to support Android 7.1 and lower
                .setPriority(NotificationCompat.PRIORITY_LOW) // Set priority to PRIORITY_LOW to mute notification sound
                .setContentIntent(pendingIntent)
                .build()
    }


    @SuppressLint("MissingPermission")
    private fun getSatellitesInfo(location: Location?)
    {


        if (location != null) {
            dAltitude = location!!.latitude
            szAltitude = dAltitude.toString()
            /**************************************************************
             * Provides a count of satellites in view, and satellites in use
             */
            mGpsStatus = locationManager!!.getGpsStatus(mGpsStatus)

            val satellites = mGpsStatus!!.getSatellites()
            var iTempCountInView = 0
            var iTempCountInUse = 0
            if (satellites != null) {
                for (gpsSatellite in satellites) {
                    iTempCountInView++
                    if (gpsSatellite.usedInFix()) {
                        iTempCountInUse++
                    }
                }
            }
            szSatellitesInView = iTempCountInView.toString()
            szSatellitesInUse = iTempCountInUse.toString()

            LogUtils.error(LogUtils.TAG, "szSatellitesInView : ${javaClass.simpleName} "+ szSatellitesInView)
            LogUtils.error(LogUtils.TAG, "szSatellitesInUse : ${javaClass.simpleName} "+ szSatellitesInUse)


            if(lastLocation == null)
                lastLocation= location

            /// send detail to PACI SERVER
            LocationServiceHelper.getInstance(this).paciFeedRequestGet(ApiConstant.FEED_PACI, location,szSatellitesInView!!, szSatellitesInUse!!, lastLocation!!)
            ///keep here location as old location and take new location in  next call
            lastLocation= location




        }
    }



}