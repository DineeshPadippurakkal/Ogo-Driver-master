package webtech.com.courierdriver.constants

import retrofit2.Retrofit
import webtech.com.courierdriver.BuildConfig


/*
 * Created by Abdulrahim Hannure
 */

object ApiConstant {

  // DEBUGGING or TEST
  //val BASE_URL_DEBUGGING = "https://ogo.delivery:1000/"
  ////// LIVE
  //val BASE_URL_LIVE = "http://ogo.delivery:9999/"

  val BASE_URL = BuildConfig.BASE_URL

  var retrofit: Retrofit? = null

  ///// multiple database in fire base console

  //below is LIVE REAL TIME FIRE BASE DATABASE
  var REAL_TIME_FIREBASE_DB_DEFAULT_ONE = 1

//below is DEBUGGING REAL TIME FIRE BASE DATABASE
  //var  REAL_TIME_FIREBASE_DB_TWO = 2

  var REAL_TIME_FIRBASE_CONNECT = REAL_TIME_FIREBASE_DB_DEFAULT_ONE


    ////https://maps.googleapis.com/maps/api/directions/json?origin=29.3663204,47.9679552&destination=29.2001991,48.0467224&sensor=false&units=metric&mode=driving&key=AIzaSyC695nD8aNVqejZ-r7IrYqJXNCWt4ykB4U

    val BASE_URL_DIRECTION_API = "https://maps.googleapis.com/"

    val DRIVER_IMAGE_BASE_URL = "http://ogo.delivery/Images/"



    const val LOGIN = "api/driver/login"
    const val LOGOUT = "api/driver/driver_logout"
    const val UPDATE_LOCATION =  "api/driver/update_loc"
    const val GO_ONLINE = "api/driver/go_online"
    const val GO_OFFLINE ="api/driver/go_offline"
    const val CHANGE_DRIVER_ONLINE_STATUS = "api/driver/change_status"
    const val ACCEPT_ORDER =  "api/driver/accept_order"
    const val CHANGE_ORDER_STATUS ="api/driver/change_order_status"
    const val CHANGE_ORDER_CANCEL ="api/driver/order_cancel"
    const val CANCEL_ORDER = "api/driver/cancel_order_web"
    const val INVOICE_ORDER = "api/driver/driver_total_invoice_list"
    const val ORDER_HISTORY = "api/driver/driver_completed_orderlist"
    const val BULK_ORDER_HISTORY = "api/driver/driver_bulk_order_completed_orderlist"
    const val CURRENT_ASSIGN_ORDER = "api/driver/driver_current_running_orders"
    const val STUCK_ORDER = "api/driver/driver_stuck_released_orders"
    const val CHECK_ORDER_STATUS = "api/driver/order_status_back"
    const val DRIVER_FEEDBACK = "api/driver/driver_submit_feedback"
    const val SWITCHING_ORDER = "api/driver/switch_order_free"
    const val SWITCHED_ORDER_ASSIGNED = "api/driver/switch_order_assigned"



    ///PACI API Constant
    const val FEED_PACI = "https://exgisapps.paci.gov.kw/LogAppServer/Log/LogServer.svc/json/FeedGPS?"

    const val PRIVACY_POLICY="https://ogo.delivery/Privacy/PrivacyPolicy"


    const val PARAM_CURRENT_LOCATION_X = "CurrentLocationX"
    const val PARAM_CURRENT_LOCATION_Y = "CurrentLocationY"
    const val PARAM_HEADING = "Heading"
    const val PARAM_SPEED = "Speed"
    const val PARAM_GPS_TIME = "GPSTime"
    const val PARAM_CALLER_ID = "CallerID"

    const val PARAM_NUM_OF_SATELLITES = "NumOfSatellites"
    const val PARAM_CALLER_TYPE = "CallerType"
    const val PARAM_CALLER_VERSION = "CallerVersion"
    const val PARAM_CALLER_OS = "CallerOS"
    const val PARAM_APPLICATION_NAME = "ApplicationName"
    const val PARAM_APPLICATION_ID = "ApplicationID"

 // const val GPS_TIME_TEMP = "2020-07-27 08:50:16"
 // const val CALLER_ID_TEMP = "STAT-2E105DA8-E8D1-45C4-94FD-8BC1AE27A1A8"

    const val NUM_OF_SATELLITES = "5"
    const val CALLER_TYPE = "android"
    const val CALLER_VERSION = "1.1.1"
    const val CALLER_OS = "android"
    const val APPLICATION_NAME = "OGO"
    const val APPLICATION_ID = "com.ogo.driverapp"









  ///only in test available
  //http://ogo.delivery:9999/api/cms/GetCmsByIDLang/2/en
  //http://ogo.delivery:9999/api/cms/GetCmsByIDLang/2/ar
    const val CONTACT_US_EN = "api/cms/GetCmsByIDLang/2/en"
    const val CONTACT_US_AR = "api/cms/GetCmsByIDLang/2/ar"


    ///DIRECTION GOOGLE API KEY with old makan gmail account
    //const val API_KEY = "AIzaSyC695nD8aNVqejZ-r7IrYqJXNCWt4ykB4U"


  ///DIRECTION GOOGLE API KEY with new  ogo gmail account
  const val API_KEY = "AIzaSyDYrnhGCEyfxmm5Mb8UHSQlRiKfti4uPiA"




}
