package webtech.com.courierdriver.service


import android.app.*
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import androidx.core.app.NotificationCompat
import webtech.com.courierdriver.BroadcastReceiver.NetworkChangeBroadcastReceiver
import webtech.com.courierdriver.R
import webtech.com.courierdriver.activity.MainActivity
import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.utilities.Language
import webtech.com.courierdriver.utilities.LogUtils
import webtech.com.courierdriver.utilities.PreferenceHelper

/*
Created by ​
Hannure Abdulrahim


on 7/24/2019.
 */

class SyncingIntentService : IntentService("SyncingIntentService") {

    companion object {
        val CHANNEL_ID = this.javaClass.name
        val notificationChannelName = this.javaClass.name+" Channel"

    }

    private  var networkChangeBroadcastReceiver :NetworkChangeBroadcastReceiver?  =null


    override fun onHandleIntent(intent: Intent?) {


        LogUtils.error(LogUtils.TAG, "onHandleIntent in SyncingIntentService ")

    }

    override fun onCreate() {
        super.onCreate()

        if (networkChangeBroadcastReceiver == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                val intentFilter = IntentFilter()
                intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
                networkChangeBroadcastReceiver = NetworkChangeBroadcastReceiver()
                registerReceiver(networkChangeBroadcastReceiver, intentFilter)
            }



        }


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//
//            registerReceiver(networkChangeBroadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
//        }


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        onHandleIntent(intent)

        setUpNotifcationChannelIfNeeded(intent)




        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        if(networkChangeBroadcastReceiver != null)
        {
            unregisterReceiver(networkChangeBroadcastReceiver)
            networkChangeBroadcastReceiver=null
        }





    }


    private fun setUpNotifcationChannelIfNeeded(intent: Intent?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            /// no need of notification handler
            // let service start as background


        } else {

            // from here service will start as Foreground Service ( user will see notification always)
            val input = intent?.getStringExtra("inputExtra")
            createNotificationChannel()
            showNotification(getString(R.string.sync_intent_note_title), getString(R.string.sync_intent_note))

            //do heavy work on a background thread
            //stopSelf();

        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                    CHANNEL_ID,
                    notificationChannelName,
                    NotificationManager.IMPORTANCE_LOW
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }


    private fun showNotification(contentTitleStr: String, contentMsgStr: String)
    {

        var preferenceHelper: PreferenceHelper? = null
        preferenceHelper = PreferenceHelper(this)
        var notification: Notification? = null


        if (preferenceHelper!!.language == Language.ENGLISH) {


            notification = buildNotification(contentTitleStr, contentMsgStr)

        } else {
            notification = buildNotification(contentTitleStr, contentMsgStr)
        }


        //we will use same ID , we don't need separate notification here
        startForeground(OGoConstant.NEARBY_ORDER_QUEUE_SERVICE_ID, notification)


    }

    private fun buildNotification(contentTitleStr: String, contentMsgStr: String): Notification? {

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent,  PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(contentTitleStr)//
                .setContentText(contentMsgStr)
                .setSmallIcon(R.mipmap.delivery_launcher)
                // You must set the priority to support Android 7.1 and lower
                .setPriority(NotificationCompat.PRIORITY_LOW) // Set priority to PRIORITY_LOW to mute notification sound
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build()
    }












}