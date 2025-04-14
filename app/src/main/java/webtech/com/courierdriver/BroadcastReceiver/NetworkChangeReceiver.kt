package webtech.com.courierdriver.BroadcastReceiver

/*
Created by ​
Hannure Abdulrahim


on 24/07/2019.
 */


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import org.greenrobot.eventbus.EventBus
import webtech.com.courierdriver.R
import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.events.InternetEvent
import webtech.com.courierdriver.service.SyncingIntentService
import webtech.com.courierdriver.utilities.LogUtils
import webtech.com.courierdriver.utilities.NetworkUtil
import webtech.com.courierdriver.utilities.PreferenceHelper


//// this will not work for > Nougat please check NetworkBroadcast also for higher version

///NetworkChangeReceiver is registered in manifest and get call when network status changed  automatically but  ,
// not called from SyncingIntentService since not register in SyncingIntentService

class NetworkChangeReceiver : BroadcastReceiver() {

    private var preferenceHelper: PreferenceHelper? = null

    private var isUpdated = false
    override fun onReceive(context: Context, intent: Intent) {


        preferenceHelper = PreferenceHelper(context)
        LogUtils.error(LogUtils.TAG, "NetworkChangeReceiver >> action:" + intent.getAction())


        //No need to register here its trigrring in MainActivity
//        if (!EventBus.getDefault().isRegistered(context))
//            EventBus.getDefault().register(context)


        // calling CONNECTIVITY_CHANGE action twice here hence
        //If you only want to receive it once, you can simply control it through variables.
        /// remember WIFI_STATE_CHANGED is not require here in manifest since we just want to calculate only network status not the wifi


        Looper.myLooper()?.let {
            Handler(it).postDelayed({
                // YOUR CODE after duration finished


//            var connectionStatusString =  NetworkStateChangeUtil.getConnectivityStatusString(context)
//            LogUtils.error(LogUtils.TAG, "NetworkChangeReceiver >> connectionStatusString:" + connectionStatusString)
//            Toast.makeText(context, connectionStatusString, Toast.LENGTH_SHORT).show()


                /// here calling CONNECTIVITY_CHANGE action twice hence controlling  with variables
                if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {


                    /// this will handle twice if its online
                    if (NetworkUtil.getInstance(context).isOnline && !isUpdated) {

                        isUpdated = true
                        LogUtils.error(LogUtils.TAG, "NetworkChangeReceiver >> CONNECTED TO INTERNET")

                        context.startService(Intent(context, SyncingIntentService::class.java))
                        //+"from"+javaClass.simpleName
                        Toast.makeText(context, context.getString(R.string.connected_to_internet), Toast.LENGTH_SHORT).show()

                        EventBus.getDefault().post(InternetEvent(OGoConstant.INTERNET_ON))


                    } else {
                        isUpdated = false
                        LogUtils.error(LogUtils.TAG, "NetworkChangeReceiver >> NO INTERNET CONNECTION :")
                        Toast.makeText(context, context.getString(R.string.not_connected_to_internet), Toast.LENGTH_SHORT).show()

                        EventBus.getDefault().post(InternetEvent(OGoConstant.INTERNET_OFF))


                    }


                }

            }, OGoConstant.FIVE_SEC)
        }


    }


}



