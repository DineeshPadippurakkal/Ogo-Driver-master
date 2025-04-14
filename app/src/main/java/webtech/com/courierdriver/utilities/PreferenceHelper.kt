package webtech.com.courierdriver.utilities

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import webtech.com.courierdriver.communication.request.RealTimeFireBaseLoginDataModel
import webtech.com.courierdriver.communication.request.RealTimeFireBaseLogoutDataModel
import webtech.com.courierdriver.communication.response.LoginRespData
import webtech.com.courierdriver.communication.NotificationResponseData.NotificationOrdersResponseData



/**
 * Created by Abulrahim Hannure on 1/2/17.
 */

class PreferenceHelper(context: Context) {


    private val sharedPref: SharedPreferences
    private val editor: SharedPreferences.Editor



    init {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context)

        editor = sharedPref.edit()
    }


    companion object {
        private val FCM_TOKEN = "fcm_token"
        private val HAS_ORDER_EXIST = "has_order_exist"
        private val LOGGED_IN_USER = "logged_in_user"
        private val LOGGED_IN_USER_REAL_TIME_FIREBASE_DB = "logged_in_user_real_time_db"
        private val LOGGED_OUT_USER_REAL_TIME_FIREBASE_DB = "logged_out_user_real_time_db"
        private val APP_STATUS = "app_status"
        private val DRIVER_ONLINE_STATUS = "driver_online_status"
        private val ORDER_STATUS = "order_status"
        private val DRIVER_ID = "driver_id"
        private val LAST_LAT = "last_lat"
        private val LAST_LONG = "last_long"
        private val LAST_LOCATION_DATE_AND_TIME = "last_location_date_and_time"
        private val LANGUAGE = "language"
        private val LAST_USERNAME = "last_username"
        private val LAST_PASSWORD = "last_password"
        //vehicle
        private val VEHICLE_TYPE = "vehicle_type"
        private val APP_VERSION_NAME = "app_version_name"
        private val LAST_ARRIVED_ORDER = "last_arrived_order"
        private val BUSY_STATUS = "busy_status"
        private val FIRE_BASE_E5 = "fire_base_e5"

        private val OGO_UUID = "ogo_uuid"
        private val LOCATION_SERVICE_FLAG = "location_service_flag"








    }

    //get fcm token
    var fcmToken: String?
        get() = sharedPref.getString(FCM_TOKEN, null)
    //set fcm token
        set(fcmToken) {
            editor.putString(FCM_TOKEN, fcmToken).commit()
        }



    ///get langauge
    ////set language
    var language: Int
        get() = sharedPref.getInt(LANGUAGE, Language.NONE)
        set(language) {
            editor.putInt(LANGUAGE, language).commit()
        }




    //this is to know order present or not
//    var orderEntry: Int
//        get() = sharedPref.getInt(HAS_ORDER_EXIST, OGoConstant.ORDER_FINISHED)
//        set(orderEntry) {
//            editor.putInt(HAS_ORDER_EXIST, orderEntry).commit()
//        }


    /**
     * set user logged in
     */
    var loggedInUser: LoginRespData?
        get() {
            val loggedINUserData = sharedPref.getString(LOGGED_IN_USER, null)
            return if (loggedINUserData != null) {
                Gson().fromJson(loggedINUserData, LoginRespData::class.java)
            } else {
                null
            }
        }
        set(info) {
            editor.putString(LOGGED_IN_USER, Gson().toJson(info)).commit()
        }

    /**
     * Logout user
     */
    fun logout() {
        editor.remove(LOGGED_IN_USER).commit()

    }



    ////// this data will help to save in fire base real time database
    /// this will be set when user logged in and removed when logout

    var realTimeFireBaseLoginDataModel: RealTimeFireBaseLoginDataModel?
        get() {
            val realTimeFireBaseLoginDataModel = sharedPref.getString(LOGGED_IN_USER_REAL_TIME_FIREBASE_DB, null)
            return if (realTimeFireBaseLoginDataModel != null) {
                Gson().fromJson(realTimeFireBaseLoginDataModel, RealTimeFireBaseLoginDataModel::class.java)
            } else {
                null
            }
        }

        set(info) {
            editor.putString(LOGGED_IN_USER_REAL_TIME_FIREBASE_DB, Gson().toJson(info)).commit()
        }






    var realTimeFireBaseLogOutDataModel: RealTimeFireBaseLogoutDataModel?
        get() {
            val realTimeFireBaseLogOutDataModel = sharedPref.getString(LOGGED_OUT_USER_REAL_TIME_FIREBASE_DB, null)
            return if (realTimeFireBaseLogOutDataModel != null) {
                Gson().fromJson(realTimeFireBaseLogOutDataModel, RealTimeFireBaseLogoutDataModel::class.java)
            } else {
                null
            }
        }

        set(info) {
            editor.putString(LOGGED_OUT_USER_REAL_TIME_FIREBASE_DB, Gson().toJson(info)).commit()
        }



    /**
     * remove user
     */
    fun logOutRealTimeFireBaseUser() {
        editor.remove(LOGGED_IN_USER_REAL_TIME_FIREBASE_DB).commit()

    }






    //  Save last arrived order here
//
//
var lastOrderResponseData: NotificationOrdersResponseData?
    get() {
        val OrdersResponseData = sharedPref.getString(LAST_ARRIVED_ORDER, null)
        return if (OrdersResponseData != null) {
            Gson().fromJson(OrdersResponseData, NotificationOrdersResponseData::class.java)
        } else {
            null
        }
    }






    set(notificationOrdersResponseData) {
        editor.putString(LAST_ARRIVED_ORDER, Gson().toJson(notificationOrdersResponseData)).commit()
    }






    var lastPassword: String?
        get() = sharedPref.getString(LAST_PASSWORD, null)
        set(lastPassword) {
            editor.putString(LAST_PASSWORD, lastPassword).commit()
        }

    var vehicleType: String?
        get() = sharedPref.getString(VEHICLE_TYPE, null)
        set(vehicleType) {
            editor.putString(VEHICLE_TYPE, vehicleType).commit()
        }

//    var appVersionName: String?
//        get() = sharedPref.getString(APP_VERSION_NAME, null)
//        set(appVersionName) {
//            editor.putString(APP_VERSION_NAME, appVersionName).commit()
//        }

    var lastUsername: String?
        get() = sharedPref.getString(LAST_USERNAME, null)
        set(lastUsername) {
            editor.putString(LAST_USERNAME, lastUsername).commit()
        }

    ///this beliw is to get driver online status
    ////default value it will return false

    var driverOnlineStatus: String
        get() = sharedPref.getString(DRIVER_ONLINE_STATUS,"false")!!
        set(driverOnlineStatus) {
            editor.putString(DRIVER_ONLINE_STATUS, driverOnlineStatus).commit()
        }






    /////set user ID (Driver ID )
    /*var driverId: String?
        get() = sharedPref.getString(DRIVER_ID, null)
        set(driverId) {
            editor.putString(DRIVER_ID, driverId).commit()
        }*/

    //// set  and get user  status : login , background or close ( logout )
    var appStatus: String?
        get() = sharedPref.getString(APP_STATUS, null)
        set(appStatus) {
            editor.putString(APP_STATUS, appStatus).commit()
        }

    ////get Lat  and  set  LAT
    var lastLat: String?
        get() = sharedPref.getString(LAST_LAT, null)
        set(lastLAT) {
        editor.putString(LAST_LAT, lastLAT).commit()
    }




    //// get and set  long
    var lastLong: String?
        get() = sharedPref.getString(LAST_LONG, null)
        set(lastLong) {
            editor.putString(LAST_LONG, lastLong).commit()
        }

    /// last lat and log time
    var lastLocationDandT: String?
        get() = sharedPref.getString(LAST_LOCATION_DATE_AND_TIME, null)
        set(lastLocationDandT) {
            editor.putString(LAST_LOCATION_DATE_AND_TIME, lastLocationDandT).commit()
        }

    //// get and set  order status
    //1 DRIVER ACCEPTED
    //2 DRIVER_AT_SOURCE
   // 3 ORDER_COLLECTED
  //  4 DRIVER_AT_DESTINATION
   // 5 DELIVERED
  //  6 COMPLETED


    var orderStatus: String?
        get() = sharedPref.getString(ORDER_STATUS, "false")
        set(orderStatus) {
            editor.putString(ORDER_STATUS, orderStatus).commit()
        }


    var busyStatus: String?
        get() = sharedPref.getString(BUSY_STATUS, "false")
        set(busyStatus) {
            editor.putString(BUSY_STATUS, busyStatus).commit()
        }


    //////this value is updateing in fire based
    ///// when  order accepted value is store here as cid and when order finished this value is null
    var fireBase_e5: String?
        get() = sharedPref.getString(FIRE_BASE_E5, "null")
        set(fireBase_e5) {
            editor.putString(FIRE_BASE_E5, fireBase_e5).commit()
        }



    //get ogo app uuid unique id
    // be careful it will vanish once share preference deleted
    var appUUID: String?
        get() = sharedPref.getString(OGO_UUID, null)
        //set fcm token
        set(fcmToken) {
            editor.putString(OGO_UUID, fcmToken).commit()
        }












}
