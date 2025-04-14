package webtech.com.courierdriver.constants

import webtech.com.courierdriver.service.LocationForegroundServicePACI

/*
Created by ​
Hannure Abdulrahim


on 9/12/2018.
 */

object OGoConstant {


    //////Application constant
    var ACTIVE = "1"
    var BACKGROUND = "0"
    var LOGOUT = "-1"

    val LOGOUT_FROM_SERVER = "1"

    val ORDER_STUCK = "STUCK"



    val DUTRY_ON = "DUTY_ON"
    val DUTRY_OFF = "DUTY_OFF"

    //driver ONLINE/OFFLINE status
    var ONLINE = "true"
    var OFFLINE = "false"

    /// INTERNET ON/OFF
    var INTERNET_ON = 1
    var INTERNET_OFF = 0

    var BUSY = "true"
    var FREE = "false"

    ////long and short and permanent snackbar status
    var INDEFINITE = 1
    var LONG = 2
    var SHORT = 3



    // types of order
    val REGULAR_ORDER ="0"
    val SWITCHING_ORDER ="1"
    val ASSIGNING_ORDER ="2"
    val YOUR_NEAR_BY_ORDER = "show_sms"





    ////driver order status
    /// 0 PENDING
    ////1 DRIVER_ACCEPTED
    //2 DRIVER_AT_SOURCE
    //3 ORDER_COLLECTED
    //4 DRIVER_AT_DESTINATION
    //5 DELIVERED
    //6 COMPLETED
    //7 CANCEL
    //8 REFUNDED


    var NO_ORDER = "-1"
    var PENDING = "0"
    var DRIVER_ACCEPTED = "1"
    var DRIVER_AT_SOURCE = "2"
    var ORDER_COLLECTED = "3"
    var DRIVER_AT_DESTINATION = "4"
    // skipp 5 and 6 in return trip
    var DELIVERED = "5"
    var COMPLETED = "6"
    var CANCEL = "7"

    //// return trip status continue here
    var RETURN_ORDER_COLLECTED = "8"
    var RETURN_ORDER_TO_SOURCE = "9"
    var RETURN_ORDER_DELIVERED ="10"
    var RETURN_ORDER_COMPLETED = "11"

    //RETURN_ORDER_COLLECTED  = 8 ,RETURN_ORDER_TO_DESTINATION =9,RETURN_ORDER_COMPLETED=10


    ////// Server side string
    var PENDING_STRING = "PENDING"
    var DRIVER_ACCEPTED_STRING = "DRIVER_ACCEPTED"
    var DRIVER_AT_SOURCE_STRING = "DRIVER_AT_SOURCE"
    var ORDER_COLLECTED_STRING = "ORDER_COLLECTED"
    var DRIVER_AT_DESTINATION_STRING = "DRIVER_AT_DESTINATION"
    var DELIVERED_STRING = "DELIVERED"
    var COMPLETED_STRING = "COMPLETED"
    var CANCEL_STRING = "CANCEL"

    ////

    var RETURN_ORDER_COLLECTED_STRING = "RETURN_ORDER_COLLECTED"
    var RETURN_ORDER_TO_SOURCE_STRING = "RETURN_ORDER_TO_SOURCE"
    var RETURN_ORDER_DELIVERED_STRING ="RETURN_ORDER_DELIVERED"
    var RETURN_ORDER_COMPLETED_STRING = "RETURN_ORDER_COMPLETED"







    //Order Queue position
    val AT_TOP = 0


    ///order status in string  or text
    var ALL_STRING = "ALL"
    var COMPLETED_ORDER_STRING = "COMPLETED"
    var CANCEL_ORDER_STRING = "CANCELLED"
    var ONGOING_ORDER_STRING = "ONGOING"


    var MOBILE = "MOBILE"
    var WEB = "WEB"
    var COD = "COD"

    /// order type
    var SINGLE_TRIP_COMP_TO_CLIENT = "Delivery"
    var SINGLE_TRIP_CLIENT_TO_COMP = "Return"
    var RETURN_TRIP_COMP_TO_CLIENT = "Delivery and Return"
    var RETURN_TRIP_CLIENT_TO_COMP = "Return and Delivery"




    /////map Activity delay
    var MAP_ACTIVITY_DELAY: Long? = 1000L

    var SPLASH_TIME_OUT: Long? = 2000L //2 Sec

    var ONE_SEC: Long = 1000L //1 Sec
    var TWO_SEC: Long = 2000L //2 Sec
    var THREE_SEC: Long = 3000L //3 Sec
    var FOUR_SEC: Long = 4000L //4 Sec
    var FIVE_SEC: Long = 5000L //5 Sec


    /////// driver comments constant
    var TYPES_OF_DRIVER = "DRIVER"
    var DRIVER_STOPPED = "0"

    var ARRIVED_ORDER_DELAY: Long? = 1000L //1 Sec
    var FCM_TOKEN_DELAY: Long? = 3000L //3 Sec

    var DELAY: Long = 1000L //1 Sec
    var DELAY_TWO_SEC: Long = 2000L //1 Sec

    var DELAY_HALF_SECOND: Long = 500L //0.5 Sec

    var LOCATION_UPDATE_PERIOD_MSEC: Long? =  60 * 1000L //60 Sec // 5 * 1000L// 5 sec
    var LOCATION_UPDATE_INTERVEL: Long =  5 * 1000L // 5 sec


    ///FIRE BASE NODE :

    var APP_STATUS_NODE = "appSt"
    var DRIVER_DETAILS_NODE = "drD"
    var LOCATION_NODE = "lo"
    var ONLINE_STATUS_NODE = "onSt"
    var ORDER_STATUS_NODE = "orSt"

//    var APP_STATUS_NODE = "appStatus"
//    var DRIVER_DETAILS_NODE = "driverDetails"
//    var LOCATION_NODE = "location"
//    var ONLINE_STATUS_NODE = "onlineStatus"
//    var ORDER_STATUS_NODE = "orderStatus"

    //// IndividualOrderHistoryFragment
    var PAYMENT_TYPE_ALL = "ALL"
    var PAYMENT_TYPE_COD = "COD"
    var PAYMENT_TYPE_KNET = "KNET"
    var PAYMENT_TYPE_MASTER_CARD = "MASTER_CARD"
    var PAYMENT_TYPE_VISA_CARD = "VISA_CARD"
    var PAYMENT_TYPE_CREDIT_CARD = "CREDIT_CARD"


    /// Bottom Navigation Consstant


    val YOUR_ORDER_TAB = 0
    val NEARBY_ORDER_TAB = 1


    val NOTIFICATION_FORGROUND_SERVICE_ID = 1
    val NOTIFICATION_FORGROUND_PACI_SERVICE_ID = 2
    val NEARBY_ORDER_QUEUE_SERVICE_ID=3
    val ORDERS_QUEUE_SERVICE_ID=4
    val SYNCING_INTENT_SERVICE_ID=5








}
