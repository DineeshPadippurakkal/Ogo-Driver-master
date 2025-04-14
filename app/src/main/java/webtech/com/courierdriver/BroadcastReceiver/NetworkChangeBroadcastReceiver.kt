package webtech.com.courierdriver.BroadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.utilities.LogUtils
import webtech.com.courierdriver.utilities.NetworkStateChangeUtil

/*
Created by ​
Hannure Abdulrahim


on 7/24/2019.
 */



// ...Network change Receiver for >= Nougat
///NetworkChangeBroadcastReceiver is registered in SyncingIntentService and get call from service but  ,
// not register in manifest

class NetworkChangeBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {


       LogUtils.error(LogUtils.TAG, "NetworkChangeBroadcastReceiver >> action:" + intent.getAction())
        //var connectionStatusString =  NetworkStateChangeUtil.getConnectivityStatusString(context)


        Looper.myLooper()?.let {
            Handler(it).postDelayed({
                //Your Code
                NetworkStateChangeUtil.checkInternetConnectivity(context,javaClass.simpleName)


            },OGoConstant.FIVE_SEC!!)
        }




    }
}