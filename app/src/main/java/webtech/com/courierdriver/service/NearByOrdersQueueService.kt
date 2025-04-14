package webtech.com.courierdriver.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import webtech.com.courierdriver.OrderSingletonQueue.NearByOrderSingletonQueue
import webtech.com.courierdriver.R
import webtech.com.courierdriver.activity.MainActivity
import webtech.com.courierdriver.communication.ApiPostService
import webtech.com.courierdriver.communication.ApiPostUtils
import webtech.com.courierdriver.communication.NotificationResponseData.NotificationNearByOrdersResponseData
import webtech.com.courierdriver.communication.response.CheckOrderStatusResp
import webtech.com.courierdriver.constants.OGoConstant

import webtech.com.courierdriver.utilities.Language
import webtech.com.courierdriver.utilities.LogUtils
import webtech.com.courierdriver.utilities.PreferenceHelper
import java.util.*

/*
Created by ​
Hannure Abdulrahim


on 10/17/2019.
 */



/* This class will perform two task
 * 1) it will check where recived near by order is still in pending ( available ) if yes show in notification else delete it
 * 2) notification channel >= Android 8 (Oreo) and < =  Android 8 both managed here check while starting this service
 * */

class NearByOrdersQueueService : Service() {


    companion object {
        val CHANNEL_ID = javaClass.simpleName
        val notificationChannelName = javaClass.simpleName+" Channel"


    }


    internal var tag = javaClass.simpleName
    var NEARBY_ORDER_QUEUE_DELAY = 30*1000L // 30 Sec
    private lateinit var runnable: Runnable
    var contentType="text";


    override fun onBind(intent: Intent?): IBinder? {
        return null

    }


    override fun onCreate() {
        super.onCreate()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        //LogUtils.error(LogUtils.TAG, "NearByOrdersQueueService => ")
        showNotificationHandler()

        val handler = Handler(Looper.myLooper()!!)
        runnable = Runnable {
            // do your work

            val mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
            LogUtils.error(LogUtils.TAG, "NearByOrdersQueueService mydate => "+mydate)
            LogUtils.error(LogUtils.TAG, "NearByOrdersQueueService NearByOrderSingletonQueue.instance.notificationNearByOrdersResponseDataList.size  => "+ NearByOrderSingletonQueue.instance.notificationNearByOrdersResponseDataList!!.size)


            if(NearByOrderSingletonQueue.instance.notificationNearByOrdersResponseDataList!!.size>0)
            {
                ////Take order alway at zeroth  position
                checkOrderStatusPost(NearByOrderSingletonQueue.instance.notificationNearByOrdersResponseDataList!!.get(0),this)


            }else
            {
               // showNotificationHandler()
                showNotification(getString(R.string.checking_near_by_order),getString(R.string.no_orders))

            }




            handler.postDelayed(runnable, NEARBY_ORDER_QUEUE_DELAY)
        }

        handler.postDelayed(runnable, NEARBY_ORDER_QUEUE_DELAY)




        return START_STICKY
    }


    //if you call stopService(), then the method onDestroy() in the service is called
    override fun onDestroy() {
        super.onDestroy()
        LogUtils.error(LogUtils.TAG, javaClass.simpleName + " stopped...")

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {


            // NotificationManager.IMPORTANCE_DEFAULT
            //// Set importance to IMPORTANCE_LOW to mute notification sound on Android 8.0 and above
            val serviceChannel = NotificationChannel(
                    CHANNEL_ID,
                    notificationChannelName,
                    NotificationManager.IMPORTANCE_LOW
            )

            serviceChannel.enableVibration(false)
            serviceChannel.setSound(null,null); //<---- ignore sound
            serviceChannel.enableLights(false)
            serviceChannel.setLightColor(Color.BLUE)


            val manager = getSystemService(NotificationManager::class.java)

            manager!!.createNotificationChannel(serviceChannel)

    }



    private fun  showNotificationHandler()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            /// no need of notification handler
            // let service start as background


        }else{

            // from here service will start as Foreground Service ( user will see notification always
            //val input = intent!!.getStringExtra("inputExtra")
            //contentType= input
            createNotificationChannel()

            showNotification(getString(R.string.checking_near_by_order),getString(R.string.no_orders))


            //do heavy work on a background thread
            //stopSelf();

        }

    }

    private fun showNotification( contentTitleStr:String ,contentMsgStr : String )
    {

        var preferenceHelper: PreferenceHelper? = null
            preferenceHelper = PreferenceHelper(this)
        var notification : Notification? = null

        if(preferenceHelper!!.language== Language.ENGLISH)
        {
            notification = buildNotification(contentTitleStr,contentMsgStr)
        }else{

            notification = buildNotification(contentTitleStr,contentMsgStr)
        }


        startForeground(OGoConstant.NEARBY_ORDER_QUEUE_SERVICE_ID, notification)

    }


    private fun buildNotification(contentTitleStr:String ,contentMsgStr : String): Notification? {

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
                .build()
    }



    /*
  * web service call via retrofit
  * To check order status
  * */
    private fun checkOrderStatusPost(notificationNearByOrdersResponseData:  NotificationNearByOrdersResponseData, context: Context) {
        LogUtils.error(LogUtils.TAG, "NearByOrdersQueueService => notificationNearByOrdersResponseData!!.orderId =>" +notificationNearByOrdersResponseData!!.orderId)


        var apiPostService: ApiPostService? = null

        apiPostService = ApiPostUtils.apiPostService
        apiPostService!!.postCheckOrderStatus(notificationNearByOrdersResponseData!!.orderId!!).enqueue(object : Callback<CheckOrderStatusResp> {
            override fun onResponse(call: Call<CheckOrderStatusResp>, response: Response<CheckOrderStatusResp>) {

                //LogUtils.error(TAG, "response.raw().toString() =>" + response.raw().toString())

                if (response.isSuccessful) {

                    showCheckOrderStatusResp(response.body()!!.toString())



                    if (response.body()!!.status.toString().equals("true", true)) {

                        ///make sure at least one order is present
                        if (response.body()!!.data!! != null  ) {

                            var  orderStatus = response.body()!!.data!!.orderStatusInt


                            LogUtils.error(LogUtils.TAG, " NearByOrdersQueueService => postCheckOrderStatus => response.body()!!.data!!.orderStatusInt => "+ orderStatus )
                            // if order status is zero then only display it to user and push order  to at the end of queue to process next time
                            /// if order status is other than zero then just remove its from the list

                            if(orderStatus.equals(OGoConstant.PENDING))
                            {
                                /// here order is in  pending status hence show to user
                                // and delete it from top and add this order to pending queue at the end



                                ///// show order here if driver is online and free
                                showNotificationNearByOrder(notificationNearByOrdersResponseData)

                                var topQueueOrder = NearByOrderSingletonQueue.instance.notificationNearByOrdersResponseDataList!!.get(0)

                                //// just delete order from top and add it to at end of queue
                                NearByOrderSingletonQueue.instance.deleteOrder(OGoConstant.AT_TOP)
                                NearByOrderSingletonQueue.instance.addOrderAt(topQueueOrder!!,NearByOrderSingletonQueue.instance.notificationNearByOrdersResponseDataList!!.size)



                            }else{
                                LogUtils.error(LogUtils.TAG, " NearByOrdersQueueService => DELETE#2 " )
                                ////// here remove order , not in accepting situation
                                NearByOrderSingletonQueue.instance.deleteOrder(OGoConstant.AT_TOP)

                            }



                        } else {
                            LogUtils.error(LogUtils.TAG, " NearByOrdersQueueService => DELETE#3 " )
                            /// here also remove order because order data is not exist
                            NearByOrderSingletonQueue.instance.deleteOrder(OGoConstant.AT_TOP)





                        }


                    }else
                    {
                        LogUtils.error(LogUtils.TAG, " NearByOrdersQueueService => DELETE#4 " )
                        /// here also remove order because order does not exist
                        NearByOrderSingletonQueue.instance.deleteOrder(OGoConstant.AT_TOP)

                    }
                }else
                {
                    LogUtils.error(LogUtils.TAG, " NearByOrdersQueueService => DELETE#5 " )
                    NearByOrderSingletonQueue.instance.deleteOrder(OGoConstant.AT_TOP)
                }
            }

            override fun onFailure(call: Call<CheckOrderStatusResp>, t: Throwable) {

                LogUtils.error(LogUtils.TAG, "Unable to submit postCheckOrderStatus to API.")
                //Toast.makeText(context, " Unable to submit postCheckOrderStatus to API.!", Toast.LENGTH_LONG).show();

            }
        })



    }



    fun showCheckOrderStatusResp(response: String) {
        LogUtils.error(LogUtils.TAG+">>>>RESPONSE>>>>", response)
    }

    fun showNotificationNearByOrder(notificationNearByOrdersResponseDataParam: NotificationNearByOrdersResponseData?) {

        var preferenceHelper: PreferenceHelper? = null
        preferenceHelper = PreferenceHelper(this)
        ///no order found show order here
        LogUtils.error(LogUtils.TAG, "preferenceHelper!!.busyStatus =>" + preferenceHelper!!.busyStatus)

                showNotification(getString(R.string.more_orders),getString(R.string.go_to_area ) +" "+ notificationNearByOrdersResponseDataParam!!.goToArea)


    }




}
