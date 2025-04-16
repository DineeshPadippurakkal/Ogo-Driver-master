package webtech.com.courierdriver.LocationUtils

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.IBinder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import webtech.com.courierdriver.communication.APIGetService
import webtech.com.courierdriver.communication.ApiGetUtils
import webtech.com.courierdriver.communication.ApiPostUtils
import webtech.com.courierdriver.communication.response.LocationUpdateResp
import webtech.com.courierdriver.communication.ApiPostService
import webtech.com.courierdriver.communication.request.RealTimeFireBaseLocation
import webtech.com.courierdriver.communication.request.RealTimeFireBaseLoginDataModel
import webtech.com.courierdriver.communication.response.PaciFeedResp
import webtech.com.courierdriver.constants.ApiConstant
import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.firebase.FirebaseDatabaseUtils
import webtech.com.courierdriver.utilities.*


/*
Created by ​
Hannure Abdulrahim


on 7/31/2019.
 */
class LocationServiceHelper: Service()
{
    companion object {

        val instance = LocationServiceHelper()
        internal lateinit var context: Context
        var isSucesssfullFirebasePush = false

        fun getInstance(ctx: Context): LocationServiceHelper {
            context = ctx.applicationContext
            return instance
        }

    }


    /*
   * web service call via retrofit to post driver location
   * */



    fun driverLocationPost(userName: String, latitude: String, longitude: String) {
        var apiPostService: ApiPostService? = null
        apiPostService = ApiPostUtils.apiPostService



        apiPostService.postDriverLocation(userName, latitude, longitude).enqueue(object : Callback<LocationUpdateResp> {
            override fun onResponse(call: Call<LocationUpdateResp>, response: Response<LocationUpdateResp>) {

                //LogUtils.error("TvCloud>>>", "response.raw().toString() =>" + response.raw().toString())

                if (response.isSuccessful) {

                    //showLocationUpdateResponse(response.body()!!.toString())

                    if (response.body()!!.status.toString().equals("true", true)) {
                        LogUtils.error(LogUtils.TAG, "postDriverLocation response.body()!!.message.toString() =>" + response.body()!!.message.toString())


                    } else {
                        LogUtils.error(LogUtils.TAG, "postDriverLocation ERROR #### response.body()!!.message.toString()=> " + response.body()!!.message.toString())


                    }

                }
            }

            override fun onFailure(call: Call<LocationUpdateResp>, t: Throwable) {

                LogUtils.error(LogUtils.TAG, "Unable to submit driverLocationPost to API.")


            }
        })


    }

    fun showLocationUpdateResponse(response: String) {
        LogUtils.error("TAG>>>>RESPONSE>>>>", response)
    }



    ///PACI GET REQUEST TO FEED

    fun paciFeedRequestGet(paciBaseUrl: String, location: Location?, szSatellitesInView: String, szSatellitesInUse: String, lastLocation: Location) {


//        LogUtils.error(LogUtils.TAG,">>>>paciFeedRequestGet>>>>lastLocation.latitude "+lastLocation.latitude )
//        LogUtils.error(LogUtils.TAG,">>>>paciFeedRequestGet>>>>lastLocation.longitude "+lastLocation.longitude )
//        LogUtils.error(LogUtils.TAG,"----" )



        ///https://exgisapps.paci.gov.kw/LogAppServer/Log/LogServer.svc/json/FeedGPS?
        // CurrentLocationX=48.014189&CurrentLocationY=29.267963&Heading=0.000000&Speed=-1.000000&GPSTime=2016-11-10%2008:50:16
        // &CallerID=Kuwait%20STAT-2E105DA8-E8D1-45C4-94FD-8BC1AE27A1A8&NumOfSatellites=5&CallerType=android&CallerVersion=1.0.1&CallerOS=android
        // &ApplicationName=ogoapp&ApplicationID=ogoapp
        /// make here full url based on PACI base url



        var paciFullUrl = paciBaseUrl +
                ApiConstant.PARAM_CURRENT_LOCATION_X +"="+location?.latitude + "&"+
        ApiConstant.PARAM_CURRENT_LOCATION_Y +"="+location?.longitude + "&"+
        ApiConstant.PARAM_HEADING +"="+location?.bearing + "&"+
        ApiConstant.PARAM_SPEED +"="+getSpeedInKMPH(location,lastLocation) + "&"+
        ApiConstant.PARAM_GPS_TIME +"="+ TimeUtils.getPACIFormattedDateAndTime() + "&"+
        ApiConstant.PARAM_CALLER_ID +"="+ DriverUtilities.getDeviceId(context) + "&"+
        ApiConstant.PARAM_NUM_OF_SATELLITES +"="+ szSatellitesInUse + "&"+
        ApiConstant.PARAM_CALLER_TYPE +"="+ ApiConstant.CALLER_TYPE + "&"+
        ApiConstant.PARAM_CALLER_VERSION +"="+ ApiConstant.CALLER_VERSION + "&"+
        ApiConstant.PARAM_CALLER_OS +"="+ ApiConstant.CALLER_OS + "&"+
        ApiConstant.PARAM_APPLICATION_NAME +"="+ ApiConstant.APPLICATION_NAME + "&"+
        ApiConstant.PARAM_APPLICATION_ID +"="+ ApiConstant.APPLICATION_ID


        //cd227e10-3828-4c00-bb9e-7249a58901e4
        //LogUtils.error(LogUtils.TAG, " DriverUtilities.getDeviceId(context) => "+DriverUtilities.getDeviceId(context))
        //2020-07-27 17:42:13
        //LogUtils.error(LogUtils.TAG, "PACI GPSTime => "+TimeUtils.getPACIFormattedDateAndTime())
        //https://exgisapps.paci.gov.kw/LogAppServer/Log/LogServer.svc/json/FeedGPS?CurrentLocationX=29.3660195&CurrentLocationY=47.968212
        // &Heading=118.290665&Speed=0.019922743&GPSTime=2020-07-27 17:42:13&CallerID=97d10af8-73ff-477b-a0b4-02ccf667d7e4&NumOfSatellites=5
        // &CallerType=android&CallerVersion=1.1.1&CallerOS=android&ApplicationName=com.ogo.driverapp&ApplicationID=com.ogo.driverapp
        //LogUtils.error(LogUtils.TAG, "paciFullUrl => "+paciFullUrl)

        var mAPIGetService: APIGetService? = null
        mAPIGetService = ApiGetUtils.apiGetService
        mAPIGetService.getPACIFeedRequest(paciFullUrl).enqueue(object : Callback<PaciFeedResp> {


            override fun onResponse(call: Call<PaciFeedResp>, response: Response<PaciFeedResp>) {

                if (response.isSuccessful) {
                    showPaciFeedResp(response.body()!!.toString())



                }
            }

            override fun onFailure(call: Call<PaciFeedResp>, t: Throwable) {
                LogUtils.error(LogUtils.TAG, "server contact failed")


            }
        })
    }

    fun showPaciFeedResp(response: String) {
        LogUtils.error(LogUtils.TAG, ">>>>RESPONSE_PACI_FEED>>>>$response")

    }




    /*
* this will update driver location and its status on fire base real time database
* if you want to update status only check MainActivity - > updateRealTimeDriverApplicationStatus()
*
* */
    fun updateRealTimeDriverLocation(driverLocation: Location,context: Context):Boolean {
         var preferenceHelper: PreferenceHelper? = null
        preferenceHelper = PreferenceHelper(context)

        // loginRespData = preferenceHelper!!.loggedInUser
        if (preferenceHelper.realTimeFireBaseLoginDataModel != null) {

            /// send lat and lng and this is getting updated in Location Service.java
            ///      Here Send Driver Location

             return createDriver(driverLocation,context)
        } else return false

    }


    /*
 * Creating new Driver node under 'driver'
 */
    private fun createDriver(driverLocation: Location,context: Context):Boolean {


        var preferenceHelper: PreferenceHelper? = null
        preferenceHelper = PreferenceHelper(context)
         var status: String? = null


        //////Speed
         var lastLocation: Location? = null
         var calculatedSpeed = 0.0

        var realTimeFireBaseLoginDataModel: RealTimeFireBaseLoginDataModel? = null


        val mFirebaseDatabase = FirebaseDatabaseUtils.firebaseDatabaseRefrence(context,javaClass.simpleName)

        mFirebaseDatabase.let {

            // login
            realTimeFireBaseLoginDataModel = RealTimeFireBaseLoginDataModel()
            realTimeFireBaseLoginDataModel!!.location = RealTimeFireBaseLocation()

            realTimeFireBaseLoginDataModel = preferenceHelper.realTimeFireBaseLoginDataModel

            status = preferenceHelper.appStatus



            //val mydate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
            //LogUtils.error(LogUtils.TAG, "mydate => "+mydate)


            val noWDateAndTime = TimeUtils.getFormattedDateAndTime()
            LogUtils.error(LogUtils.TAG, "noWDateAndTime => "+noWDateAndTime)

            //////if application is logout then update its logout details
            if (status!!.equals(OGoConstant.LOGOUT, true)) {

                LogUtils.error(LogUtils.TAG, "Driver Logout successfully !!! ")


                var realTimeLogoutDataModel = LogouUtils.getLogoutModel(context)

                if (realTimeLogoutDataModel.appSt!!.status == null || realTimeLogoutDataModel.appSt!!.status!!.equals(OGoConstant.LOGOUT, true)) {

                    mFirebaseDatabase!!.child(realTimeLogoutDataModel.drD!!.id!!).setValue(realTimeLogoutDataModel)
                            .addOnCompleteListener {
                                if(it.isComplete || it.isSuccessful )
                                {
                                    LogUtils.error(LogUtils.TAG, javaClass.simpleName +" : isComplete  : "+it.isComplete)
                                    LogUtils.error(LogUtils.TAG, javaClass.simpleName +" :isSuccessful : "+it.isSuccessful)
                                    isSucesssfullFirebasePush = true

                                }else if (it.isCanceled)
                                {
                                    LogUtils.error(LogUtils.TAG, javaClass.simpleName +" :isCanceled : "+it.isCanceled)
                                    isSucesssfullFirebasePush = false
                                }


                            }


                }


            } else if (preferenceHelper.driverOnlineStatus.equals(OGoConstant.OFFLINE, true) && (realTimeFireBaseLoginDataModel != null)) {
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
                        lastLocation = driverLocation


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
                            realTimeFireBaseLoginDataModel!!.location!!.previousLatitude = preferenceHelper.lastLat
                            realTimeFireBaseLoginDataModel!!.location!!.previousLongitude = preferenceHelper.lastLong

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
                        // In real apps this DriverID should be fetched
                        // by implementing firebase auth
                        if (realTimeFireBaseLoginDataModel != null) {
                            mFirebaseDatabase!!.child(realTimeFireBaseLoginDataModel!!.driverDetails!!.id!!).child(OGoConstant.LOCATION_NODE).setValue(realTimeFireBaseLoginDataModel!!.location)
                                    .addOnCompleteListener {
                                        if(it.isComplete || it.isSuccessful )
                                        {
                                            LogUtils.error(LogUtils.TAG, javaClass.simpleName +" : isComplete  : "+it.isComplete)
                                            LogUtils.error(LogUtils.TAG, javaClass.simpleName +" :isSuccessful : "+it.isSuccessful)
                                            isSucesssfullFirebasePush = true

                                        }else if (it.isCanceled)
                                        {
                                            LogUtils.error(LogUtils.TAG, javaClass.simpleName +" :isCanceled : "+it.isCanceled)
                                            isSucesssfullFirebasePush = false
                                        }


                                    }


                        }

                    }

                }

            }
        }


        return isSucesssfullFirebasePush

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



    private fun getSpeedInKMPH(currentLocation: Location?,lastLocation: Location?): Int {

        //////Speed
        var calculatedSpeed = 0.0
        if (lastLocation != null) {
            val elapsedTime = (currentLocation!!.getTime() - lastLocation!!.getTime()) / 1000 // Convert milliseconds to seconds
            calculatedSpeed = (lastLocation!!.distanceTo(currentLocation) / elapsedTime).toDouble()
        }

        //The speed calculation should be done only if location.hasSpeed()==false. Some people also reported that location.getSpeed() always return 0 even if location.hasSpeed()==true. Thus, I would use location.hasSpeed() && location.getSpeed()>0 as a condition
        //driverLocation.hasSpeed() && driverLocation.getSpeed()>0//codition
        //var  driverSpeedInMPS= if (driverLocation.hasSpeed()) driverLocation.getSpeed() else calculatedSpeed

        var  driverSpeedInMPS= if (currentLocation!!.hasSpeed() && currentLocation.getSpeed()>0) currentLocation.getSpeed() else calculatedSpeed
        //var driverSpeedInMPS = calculatedSpeed

        // There you have it, a speed value in m/s
        LogUtils.error(LogUtils.TAG, " getSpeedInKMPH >> Driver Speed in M/Sec>> " + driverSpeedInMPS)

        /// convert Meter/Sec to KM/hr
        var driverSpeedInKPH: Double? = 0.0
        if (!driverSpeedInMPS.toString().equals("Infinity", true))
            driverSpeedInKPH = DistanceUtils.convertFromMpsToKmph(driverSpeedInMPS.toString().toFloat())
        //var  driverSpeedInKPH =( driverSpeedInMPS.toString().toFloat() * 3600)/1000

        LogUtils.error(LogUtils.TAG, " getSpeedInKMPH >> Driver Speed in KM/hr >> " + driverSpeedInKPH)
        var driverIntSpeedKMPH = driverSpeedInKPH!!.toInt()
        // send speed ,LAT,LONG and previous LAT, LONG , last location date and time  only  if driver speed is more than 10 KM/hr only

        return driverIntSpeedKMPH


    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }


}
