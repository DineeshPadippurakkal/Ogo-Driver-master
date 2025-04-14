package webtech.com.courierdriver.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import org.greenrobot.eventbus.EventBus
import webtech.com.courierdriver.LocationUtils.LocationServiceHelper
import webtech.com.courierdriver.communication.request.RealTimeFireBaseLocation
import webtech.com.courierdriver.communication.request.RealTimeFireBaseLoginDataModel
import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.events.LocationChangedEvent
import webtech.com.courierdriver.events.PermissionFailedEvent
import webtech.com.courierdriver.firebase.FirebaseDatabaseUtils
import webtech.com.courierdriver.utilities.*
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit

//


/* This class will perform two task
* 1) Update driver location in  real time fire base database
* 2) Update driver location by calling web service on server side
* 3) //location for < Android 8 (Oreo)
* */



///// not in use anymore please check LocationForegroundService which will act as background as well as Foreground Service
class LocationService : Service(), LocationListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{

    private var mLocationRequest: LocationRequest? = null

    private val fusedLocationProviderApi = LocationServices.FusedLocationApi
    private var mGoogleApiClient: GoogleApiClient? = null
    private var realTimeFireBaseLoginDataModel: RealTimeFireBaseLoginDataModel? = null
    private var status: String? = null
    private var preferenceHelper: PreferenceHelper? = null


    //////Speed
    private var lastLocation: Location? = null
    private var calculatedSpeed = 0.0


    var FASTEST_INTERVAL = OGoConstant.LOCATION_UPDATE_PERIOD_MSEC // use whatever suits you
    var currentLocation: Location? = null
    var locationUpdatedAt = java.lang.Long.MIN_VALUE


    internal var mLocationCallback: LocationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult?) {


        }

    }
    // private double tempLat= 29.3663148;
    //private double tempLon= 47.9679723;


    override fun onConnected(connectionHint: Bundle?) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            EventBus.getDefault().post(PermissionFailedEvent())
            return
        }


        //////////////////////////////////////////S

        val mLastLocation = fusedLocationProviderApi.getLastLocation(mGoogleApiClient)

        if (mLastLocation != null) {
            EventBus.getDefault().post(LocationChangedEvent(mLastLocation.latitude, mLastLocation.longitude))
        }
        //mLocationRequest!!.setInterval(1 * 1000);// 1s
        mLocationRequest!!.interval = 500// 0.5s (half sec.)

        //make it 7 seconds
        //mLocationRequest!!.interval = OGoConstant.LOCATION_UPDATE_PERIOD_MSEC

        // mLocationRequest.setFastestInterval(100);
        fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
        //////////////////////////////////////////E

    }


    override fun onLocationChanged(location: Location?) {
        if (location != null) {

            preferenceHelper = PreferenceHelper(this)

            if (((preferenceHelper!!.lastUsername != null)) && (preferenceHelper!!.driverOnlineStatus.equals(OGoConstant.ONLINE, true))) {
                ////////////////////////////////////
                //// Call web service to update location
                //// Only When driver is online
                ////////////////////////////////////
                LocationServiceHelper.getInstance(this).driverLocationPost(preferenceHelper!!.lastUsername!!, location!!.latitude.toString(), location!!.longitude.toString())

                //Toast.makeText(getApplicationContext(), "Your Location is - \n Lat:"+ location!!.getLatitude() + "\n Long: " +location!!.getLongitude() , Toast.LENGTH_LONG).show();
                EventBus.getDefault().post(LocationChangedEvent(location!!.latitude, location!!.longitude))


            }





                /// logic for testing  wih no delay
//            currentLocation = location
//            if(true)

            ////////////////////////////////////
            //logic to update location after some deplay
            ///////////////////////////////////

            var updateLocationandReport = false
            if (currentLocation == null)
            {
                currentLocation = location
                locationUpdatedAt = System.currentTimeMillis()
                updateLocationandReport = true
            }
            else
            {
                val secondsElapsed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - locationUpdatedAt)
                LogUtils.error(LogUtils.TAG, " secondsElapsed :  " + secondsElapsed )
                LogUtils.error(LogUtils.TAG, "locationUpdatedAt :  " + locationUpdatedAt )


                if (secondsElapsed >= TimeUnit.MILLISECONDS.toSeconds(FASTEST_INTERVAL!!))
                {
                    LogUtils.error(LogUtils.TAG, "secondsElapsed IN SIDE IF  :  " + secondsElapsed )
                    // check location accuracy here
                    currentLocation = location
                    locationUpdatedAt = System.currentTimeMillis()
                    updateLocationandReport = true
                }


            }

            //////// send your location to firebase after delay
           if (updateLocationandReport)
            {


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
                    updateRealTimeDriverLocation(currentLocation!!)


            }


        }
    }


    override fun onCreate() {
        super.onCreate()

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build()


    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.setInterval(1000 * 1)//1 sec
        //mLocationRequest!!.interval = OGoConstant.LOCATION_UPDATE_PERIOD_MSEC
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        if (mGoogleApiClient != null) {
            if (!mGoogleApiClient!!.isConnected) {
                mGoogleApiClient!!.connect()
            }
        }


        ////check intent is null or not its crashing becouse of this
        // use Intent? instead of Intent  with handle this issue
        if (null == intent || null == intent.action) {
            val source = if (null == intent) "intent" else "action"
            LogUtils.error(LogUtils.TAG, source + " was null, flags=" + flags + " bits=" + Integer.toBinaryString(flags))
            LogUtils.error(LogUtils.TAG, "intent is null in LocationService")


        }


        return START_STICKY
        //return super.onStartCommand(intent, flags, startId)


    }

    override fun onConnectionSuspended(i: Int) {

        LogUtils.info(LogUtils.TAG, "Suspended$i")
        LogUtils.info(LogUtils.TAG, "DriverLocation " + javaClass.simpleName + "Location Suspended")

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

        LogUtils.info(LogUtils.TAG, "Failed" + connectionResult.errorCode)
        LogUtils.info(LogUtils.TAG, "DriverLocation " + javaClass.simpleName + "Location Failed")

    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    /*
    *
    * if you call stopService(), then the method onDestroy() in the service is called
    *
    *
    * */

    override fun onDestroy() {
        LogUtils.error(LogUtils.TAG, "LocationService service stopped...")

    }


    /*
    * this will update driver location and its status on fire base real time database
    * if you want to update status only check MainActivity - > updateRealTimeDriverApplicationStatus()
    *
    * */
    private fun updateRealTimeDriverLocation(driverLocation: Location) {


        // loginRespData = preferenceHelper!!.loggedInUser
        if (preferenceHelper!!.realTimeFireBaseLoginDataModel != null) {

            /// send lat and lng and this is getting updated in Location Service.java
            ///      Here Send Driver Location

            createDriver(driverLocation)
        }

    }

    /*
     * Creating new Driver node under 'driver'
     */
    private fun createDriver(driverLocation: Location) {

        val mFirebaseDatabase = FirebaseDatabaseUtils.firebaseDatabaseRefrence(this, javaClass.simpleName)

        mFirebaseDatabase.let {

            //  preferenceHelper = PreferenceHelper(this)

            // login
            realTimeFireBaseLoginDataModel = RealTimeFireBaseLoginDataModel()
            realTimeFireBaseLoginDataModel!!.location = RealTimeFireBaseLocation()

            realTimeFireBaseLoginDataModel = preferenceHelper!!.realTimeFireBaseLoginDataModel

            status = preferenceHelper!!.appStatus


            //val mydate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
            //LogUtils.error(LogUtils.TAG, "mydate => "+mydate)


            val noWDateAndTime = TimeUtils.getFormattedDateAndTime()
            LogUtils.error(LogUtils.TAG, "noWDateAndTime => "+noWDateAndTime)

            //////if application is logout then update its logout details
            if (status!!.equals(OGoConstant.LOGOUT, true)) {

                LogUtils.error(LogUtils.TAG, "Driver Logout successfully !!! ")


                var realTimeLogoutDataModel = LogouUtils.getLogoutModel(this)

                if (realTimeLogoutDataModel!!.appSt!!.status == null || realTimeLogoutDataModel!!.appSt!!.status!!.equals(OGoConstant.LOGOUT, true)) {

                    mFirebaseDatabase!!.child(realTimeLogoutDataModel!!.drD!!.id!!).setValue(realTimeLogoutDataModel)

                }


            } else if (preferenceHelper!!.driverOnlineStatus.equals(OGoConstant.OFFLINE, true) && (realTimeFireBaseLoginDataModel != null)) {
                ///user is logged in but  gone to offline


            } else {

                /////here  user is logged  in and online
                if (realTimeFireBaseLoginDataModel != null) {


                    //this below section is when driver is online
                    if ((driverLocation.latitude.toString() != null || driverLocation.longitude.toString() != null) && realTimeFireBaseLoginDataModel != null) {


                        /////////////////////////////////////////////////////////
                        ///  driver Speed
                        /////////////////////////////////////////////////////////

                        if (lastLocation != null) {
                            val elapsedTime = (driverLocation.getTime() - lastLocation!!.getTime()) / 1000 // Convert milliseconds to seconds
                            calculatedSpeed = (lastLocation!!.distanceTo(driverLocation) / elapsedTime).toDouble()
                        }
                        this.lastLocation = driverLocation





                        //The speed calculation should be done only if location.hasSpeed()==false. Some people also reported that location.getSpeed() always return 0 even if location.hasSpeed()==true. Thus, I would use location.hasSpeed() && location.getSpeed()>0 as a condition
                        //driverLocation.hasSpeed() && driverLocation.getSpeed()>0//condition
                        //var  driverSpeedInMPS= if (driverLocation.hasSpeed()) driverLocation.getSpeed() else calculatedSpeed
                        //var  driverSpeedInMPS= if (driverLocation.hasSpeed() && driverLocation.getSpeed()>0) driverLocation.getSpeed() else calculatedSpeed
                        var driverSpeedInMPS = calculatedSpeed

                        // There you have it, a speed value in m/s
                        LogUtils.error(LogUtils.TAG, " Driver Speed in M/Sec>> " + driverSpeedInMPS)

                        /// convert Meter/Sec to KM/hr
                        var driverSpeedInKPH: Double? = 0.0
                        if (!driverSpeedInMPS.toString().equals("Infinity", true))
                            driverSpeedInKPH = DistanceUtils.convertFromMpsToKmph(driverSpeedInMPS.toString().toFloat())
                        //var  driverSpeedInKPH =( driverSpeedInMPS.toString().toFloat() * 3600)/1000

                        LogUtils.error(LogUtils.TAG, " Driver Speed in KM/hr >> " + driverSpeedInKPH)
                        var driverIntSpeedKMPH = driverSpeedInKPH!!.toInt()
                        // send speed ,LAT,LONG and previous LAT, LONG , last location date and time  only  if driver speed is more than 10 KM/hr only


                        realTimeFireBaseLoginDataModel!!.location!!.speedInKmph = driverIntSpeedKMPH.toString()

                        ///delay taken care already no need of speed here
                        //if(driverIntSpeedKMPH>10 )
                        if (true) {


                            realTimeFireBaseLoginDataModel!!.location!!.lastLocationDandT = noWDateAndTime

                            realTimeFireBaseLoginDataModel!!.location!!.loctLatt = driverLocation.latitude.toString()
                            realTimeFireBaseLoginDataModel!!.location!!.loctLong = driverLocation.longitude.toString()

                            ///add here previous LAT and previous LONG and push it in fire base
                            realTimeFireBaseLoginDataModel!!.location!!.previousLatitude = preferenceHelper!!.lastLat
                            realTimeFireBaseLoginDataModel!!.location!!.previousLongitude = preferenceHelper!!.lastLong

                            /////////////////////////////////////////////////////////
                            ///  previous LAT and LONG
                            /////////////////////////////////////////////////////////
                            ////put last LAT LONG in share preferences
                            preferenceHelper!!.lastLat = driverLocation.latitude.toString()
                            preferenceHelper!!.lastLong = driverLocation.longitude.toString()
                            preferenceHelper!!.lastLocationDandT = noWDateAndTime


                        } else {
                            ///keep old one

                            realTimeFireBaseLoginDataModel!!.location!!.speedInKmph = OGoConstant.DRIVER_STOPPED
                            realTimeFireBaseLoginDataModel!!.location!!.loctLatt = preferenceHelper!!.lastLat
                            realTimeFireBaseLoginDataModel!!.location!!.loctLong = preferenceHelper!!.lastLong

                            ///add here previous LAT and previous LONG and push it in fire base
                            realTimeFireBaseLoginDataModel!!.location!!.previousLatitude = preferenceHelper!!.lastLat
                            realTimeFireBaseLoginDataModel!!.location!!.previousLongitude = preferenceHelper!!.lastLong


                        }


                        LogUtils.error(LogUtils.TAG, "##### realTimeFireBaseLoginDataModel!!.loctLatt > " + realTimeFireBaseLoginDataModel!!.location!!.loctLatt)
                        LogUtils.error(LogUtils.TAG, "##### realTimeFireBaseLoginDataModel!!.loctLong > " + realTimeFireBaseLoginDataModel!!.location!!.loctLong)
                        LogUtils.error(LogUtils.TAG, "##### realTimeFireBaseLoginDataModel!!.location!!.previousLatitude > " + realTimeFireBaseLoginDataModel!!.location!!.previousLatitude)
                        LogUtils.error(LogUtils.TAG, "##### realTimeFireBaseLoginDataModel!!.location!!.previousLongitude > " + realTimeFireBaseLoginDataModel!!.location!!.previousLongitude)


                        // by implementing fire base auth


                        // TODO
                        // In real apps this DriverID should be fetched
                        // by implementing firebase auth
                        if (realTimeFireBaseLoginDataModel != null) {
                            mFirebaseDatabase!!.child(realTimeFireBaseLoginDataModel!!.driverDetails!!.id!!).child(OGoConstant.LOCATION_NODE).setValue(realTimeFireBaseLoginDataModel!!.location)

                        }

                    }

                }

            }


        }



    }




    /**
     * Converts the given string to title case, where the first
     * letter is capitalized and the rest of the string is in
     * lower case.
     *
     * @param s a string with unknown capitalization
     * @return a title-case version of the string
     */
    fun toTitleCase(s: String): String {
        return if (s.isEmpty()) {
            s
        } else s.substring(0, 1).uppercase() + s.substring(1).lowercase()
    }





}