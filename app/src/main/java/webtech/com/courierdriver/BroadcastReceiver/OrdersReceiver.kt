package webtech.com.courierdriver.BroadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

import webtech.com.courierdriver.utilities.LogUtils
import java.util.*

/*
Created by ​
Hannure Abdulrahim


on 7/26/2019.
 */



class OrdersReceiver : BroadcastReceiver() {

    //private static final String TAG = "OrdersReceiver";

    override fun onReceive(context: Context, intent: Intent) {
        // Log.d(TAG, "onReceive");

        val mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)

        LogUtils.error(LogUtils.TAG, "OrdersReceiver => onReceive :  "+ mydate)

    }
}