package webtech.com.courierdriver.utilities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import webtech.com.courierdriver.R
import webtech.com.courierdriver.activity.MainActivity
import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.events.InternetEvent
import webtech.com.courierdriver.service.SyncingIntentService

/*
Created by ​
Hannure Abdulrahim


on 3/12/2019.
 */



object NetworkStateChangeUtil {

    var TYPE_WIFI = 1
    var TYPE_MOBILE = 2
    var TYPE_NOT_CONNECTED = 0

    var WIFI_ENABLE = "Wifi enabled"
    var MOBILE_DATA_ENABLE = "Mobile data enabled"
    var NOT_CONNECTED = "Not connected to Internet"


    fun getConnectivityStatus(context: Context): Int {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = cm.activeNetworkInfo

        if (null != activeNetwork) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI

            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE
        }
        return TYPE_NOT_CONNECTED
    }

    fun getConnectivityStatusString(context: Context): String {
        val conn = getConnectivityStatus(context)
        // String status = null;
        var status = NOT_CONNECTED
        if (conn == TYPE_WIFI) {
            //status = "Wifi enabled";
            status = WIFI_ENABLE
        } else if (conn == TYPE_MOBILE) {
            //status = "Mobile data enabled";
            status = MOBILE_DATA_ENABLE
        } else if (conn == TYPE_NOT_CONNECTED) {
            //status = "Not connected to Internet";
            status = NOT_CONNECTED
        }
        return status
    }




 public fun checkInternetConnectivity(context: Context,displayMsg:String)
    {

        if(context is MainActivity )
        {
            /////registering here will call subscribe method  when respective event gets trigger or posted
            if (!EventBus.getDefault().isRegistered(context))
                EventBus.getDefault().register(context)

        }else
        {
            /// No need to register for service Context
        }


        if (NetworkUtil.getInstance(context).isOnline)
        {
            LogUtils.error(LogUtils.TAG, displayMsg+">> CONNECTED TO INTERNET" )
            context.startService(Intent(context, SyncingIntentService::class.java))

            //+"from"+ displayMsg

            //Toast.makeText(context, context.getString(R.string.connected_to_internet), Toast.LENGTH_SHORT).show()


            EventBus.getDefault().post(InternetEvent(OGoConstant.INTERNET_ON))

        }else
        {
            LogUtils.error(LogUtils.TAG, displayMsg+" >> NO INTERNET CONNECTION :" )
            //Toast.makeText(context, context.getString(R.string.not_connected_to_internet), Toast.LENGTH_SHORT).show()

            EventBus.getDefault().post(InternetEvent(OGoConstant.INTERNET_OFF))
        }



    }





}


