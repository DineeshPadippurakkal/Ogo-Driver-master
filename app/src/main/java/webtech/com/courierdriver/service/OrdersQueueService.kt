package webtech.com.courierdriver.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import webtech.com.courierdriver.Application.AppController
import webtech.com.courierdriver.OrderSingletonQueue.OrderSingletonQueue
import webtech.com.courierdriver.R
import webtech.com.courierdriver.activity.ArrivedOrderActivity
import webtech.com.courierdriver.activity.MainActivity
import webtech.com.courierdriver.communication.ApiPostService
import webtech.com.courierdriver.communication.ApiPostUtils
import webtech.com.courierdriver.communication.NotificationResponseData.NotificationOrdersResponseData
import webtech.com.courierdriver.communication.response.CheckOrderStatusResp
import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.utilities.*
import java.util.*


/*
Created by ​
Hannure Abdulrahim


on 7/26/2019.
 */



class OrdersQueueService : Service() {
    internal var tag = this.javaClass.name
    var ARRIVED_ORDER_QUEUE_DELAY = 5000L //5 Sec
    private lateinit var runnable: Runnable


    companion object {
        val CHANNEL_ID = this.javaClass.name
        val notificationChannelName = this.javaClass.name + " Channel"
        val ORDER_CHANNEL_ID = 1000
        val orderNotificationChannelName = "Order" + this.javaClass.name + " Channel"

    }


    override fun onBind(intent: Intent?): IBinder? {
        return null

    }

    override fun onCreate() {
        super.onCreate()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        //LogUtils.error(LogUtils.TAG, "OrdersQueueService => ")

        setUpNotifcationChannelIfNeeded(intent)


        val handler = Handler()
        runnable = Runnable {
            // do your work

            val mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
            LogUtils.error(LogUtils.TAG, "OrdersQueueService mydate => " + mydate)
            LogUtils.error(LogUtils.TAG, "OrdersQueueService OrderSingletonQueue.instance.notificationOrdersResponseData.size  => " + OrderSingletonQueue.instance.notificationOrdersResponseDataList!!.size)

            OrderSingletonQueue.instance.notificationOrdersResponseDataList?.let { notificationOrdersResponseDataList ->

                ///must have at least one order
                if (notificationOrdersResponseDataList.size > 0) {
                    ////Take order always at zeroth  position
                    checkOrderStatusPost(notificationOrdersResponseDataList.get(0), this)
                }


            }



            handler.postDelayed(runnable, ARRIVED_ORDER_QUEUE_DELAY)
        }

        handler.postDelayed(runnable, ARRIVED_ORDER_QUEUE_DELAY)


        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    private fun setUpNotifcationChannelIfNeeded(intent: Intent?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            /// no need of notification handler
            // let service start as background


        } else {

            // from here service will start as Foreground Service ( user will see notification always)
            val input = intent?.getStringExtra("inputExtra")
            createNotificationChannel()
            showNotification(getString(R.string.order_queue_note_title), getString(R.string.order_queue_note))

            //do heavy work on a background thread
            //stopSelf();

        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                    CHANNEL_ID,
                    notificationChannelName,
                    NotificationManager.IMPORTANCE_HIGH
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }

    private fun showNotification(contentTitleStr: String, contentMsgStr: String) {

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


    /*
  * web service call via retrofit
  * To check order status
  * */
    private fun checkOrderStatusPost(notificationOrdersResponseData: NotificationOrdersResponseData, context: Context) {
        LogUtils.error(LogUtils.TAG, "OrdersQueueService => notificationOrdersResponseData!!.orderId =>" + notificationOrdersResponseData!!.orderId)


        var apiPostService: ApiPostService? = null

        apiPostService = ApiPostUtils.apiPostService
        apiPostService!!.postCheckOrderStatus(notificationOrdersResponseData!!.orderId!!).enqueue(object : Callback<CheckOrderStatusResp> {
            override fun onResponse(call: Call<CheckOrderStatusResp>, response: Response<CheckOrderStatusResp>) {

                //LogUtils.error(TAG, "response.raw().toString() =>" + response.raw().toString())

                if (response.isSuccessful) {

                    showCheckOrderStatusResp(response.body()!!.toString())



                    if (response.body()!!.status.toString().equals("true", true)) {

                        ///make sure at least one order is present
                        if (response.body()!!.data!! != null) {

                            var orderStatus = response.body()!!.data!!.orderStatusInt


                            LogUtils.error(LogUtils.TAG, " OrdersQueueService => postCheckOrderStatus => response.body()!!.data!!.orderStatusInt => " + orderStatus)
                            // if order status is zero then only display it to user and push order  to at the end of queue to process next time
                            /// if order status is other than zero then just remove its from the list

                            if (orderStatus.equals(OGoConstant.PENDING)) {
                                /// here order is in  pending status hence show to user
                                // and delete it from top and add this order to pending queue at the end
                                ///// show order here if driver is online and free
                                showOrderToDriver(notificationOrdersResponseData)
                            } else {
                                LogUtils.error(LogUtils.TAG, " OrdersQueueService => DELETE#2 ")
                                ////// here remove order , not in accepting situation
                                OrderSingletonQueue.instance.deleteOrder(OGoConstant.AT_TOP)
                            }


                        } else {
                            LogUtils.error(LogUtils.TAG, " OrdersQueueService => DELETE#3 ")
                            /// here also remove order because order data is not exist
                            OrderSingletonQueue.instance.deleteOrder(OGoConstant.AT_TOP)


                        }


                    } else {
                        LogUtils.error(LogUtils.TAG, " OrdersQueueService => DELETE#4 ")
                        /// here also remove order because order does not exist
                        OrderSingletonQueue.instance.deleteOrder(OGoConstant.AT_TOP)

                    }
                } else {
                    LogUtils.error(LogUtils.TAG, " OrdersQueueService => DELETE#5 ")
                    OrderSingletonQueue.instance.deleteOrder(OGoConstant.AT_TOP)
                }
            }

            override fun onFailure(call: Call<CheckOrderStatusResp>, t: Throwable) {

                LogUtils.error(LogUtils.TAG, "Unable to submit postCheckOrderStatus to API.")
                //Toast.makeText(context, " Unable to submit postCheckOrderStatus to API.!", Toast.LENGTH_LONG).show();

            }
        })


    }


    fun showCheckOrderStatusResp(response: String) {
        LogUtils.error(LogUtils.TAG + ">>>>RESPONSE>>>>", response)
    }


    private fun showOrderToDriver(notificationOrdersResponseDataParam: NotificationOrdersResponseData?) {
        var preferenceHelper: PreferenceHelper? = null

        preferenceHelper = PreferenceHelper(this)

        if (preferenceHelper!!.driverOnlineStatus!!.equals(OGoConstant.ONLINE, true)) {


            if (preferenceHelper!!.busyStatus!!.equals(OGoConstant.FREE, true)) {
                LogUtils.error(LogUtils.TAG, "OrdersQueueService : ### DRIVER IS FREE ###")

                LogUtils.error(LogUtils.TAG, "driverOnlineStatus=> " + preferenceHelper!!.driverOnlineStatus!!.equals(OGoConstant.ONLINE, true))

                val applicationControl = applicationContext as AppController

                var PACKAGE_NAME = getApplicationContext().getPackageName()
                LogUtils.error(LogUtils.TAG, "PACKAGE_NAME : " + PACKAGE_NAME)

                val isAlive = AppStatus.AppVisibilityHelper.isForeground(this, PACKAGE_NAME)

                if (isAlive) {
                    // App is running
                    LogUtils.error(LogUtils.TAG, "App is running")

                    //// check app is in background if yes then show notification
                    //// if app is in foreground then check ArrivedOrderActivity is visible or not
                    //// if app is in foreground and ArrivedOrderActivity is visible don't do anything
                    ///  if app is in foreground and ArrivedOrderActivity is no visible just launch ArrivedOrderActivity

                    if (applicationControl.isArrivedActivityInForeground()) {

                        /////Activity already visible don't show order....
                        LogUtils.error(LogUtils.TAG, "ArrivedOrderActivity Activity is in foreground")

                    } else {

                        LogUtils.error(LogUtils.TAG, "ArrivedOrderActivity Activity is in background or not visible")

                        showNewOrderActivity(notificationOrdersResponseDataParam)

                    }


                } else {
                    // App is not running
                    LogUtils.error(LogUtils.TAG, "App is not running or killed or  app in in background ")


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        showOrderNotification(notificationOrdersResponseDataParam)

                    } else {
                        showNewOrderActivity(notificationOrdersResponseDataParam)

                    }

                }


            }


        }


    }


    fun showNewOrderActivity(notificationOrdersResponseDataParam: NotificationOrdersResponseData?) {

        var preferenceHelper: PreferenceHelper? = null
        preferenceHelper = PreferenceHelper(this)


        var intentNewOrder: Intent? = null
        ///this sections represents new order
        ///converting map to jason
        val gson = Gson()

        ////converting  MAP to POJO model class
        var notificationOrdersResponseData: NotificationOrdersResponseData? = null
        val jsonElement = gson.toJsonTree(notificationOrdersResponseDataParam)
        notificationOrdersResponseData = gson.fromJson(jsonElement, NotificationOrdersResponseData::class.java)


        ///no order found show order here
        LogUtils.error(LogUtils.TAG, "preferenceHelper!!.busyStatus =>" + preferenceHelper!!.busyStatus)
        intentNewOrder = Intent(this, ArrivedOrderActivity::class.java)
        /////Send POJO model class , makes sure pojo class implements Serializable
        intentNewOrder!!.putExtra("ARRIVED_ORDER", notificationOrdersResponseData)

        intentNewOrder!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intentNewOrder!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intentNewOrder!!)

    }


    private fun showOrderNotification(notificationOrdersResponseDataParam: NotificationOrdersResponseData?) {


        /// play sound here
        HelperUtils.playNotificationSound(R.raw.alert_sound, true,this)

        val gson = Gson()
        ////converting  MAP to POJO model class
        var notificationOrdersResponseData: NotificationOrdersResponseData? = null
        val jsonElement = gson.toJsonTree(notificationOrdersResponseDataParam)
        notificationOrdersResponseData = gson.fromJson(jsonElement, NotificationOrdersResponseData::class.java)

        val i = Intent(this, ArrivedOrderActivity::class.java)
        /////Send POJO model class , makes sure pojo class implements Serializable
        i!!.putExtra("ARRIVED_ORDER", notificationOrdersResponseData)

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // This is at least android 10...
            val mgr = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (mgr.getNotificationChannel(orderNotificationChannelName) == null) {
                mgr.createNotificationChannel(NotificationChannel(orderNotificationChannelName,
                        orderNotificationChannelName, NotificationManager.IMPORTANCE_HIGH))
            }
            mgr.notify(ORDER_CHANNEL_ID, buildNormal(this, i, notificationOrdersResponseDataParam).build())
        }


    }

    private fun buildNormal(context: Context, intent: Intent, notificationOrdersResponseDataParam: NotificationOrdersResponseData?): NotificationCompat.Builder {

        //I got null for vector images but png image resources works fine.
        // val imageBitMap = BitmapFactory.decodeResource(resources, R.drawable.ic_add_order)
        val imageBitMap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_notification)
        val bigImageBitMap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_notification_big_picture_final)


        val b = NotificationCompat.Builder(context, orderNotificationChannelName)
        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.delivery_launcher)
                .setContentText("You have a new order#${notificationOrdersResponseDataParam?.orderId!!}")
                .setSubText("Click to view order.")
                //.setColor(resources.getColor(R.color.color_them))
                .setLargeIcon(imageBitMap!!)
//                .setStyle(NotificationCompat.BigTextStyle()
//                        .bigText("You have an order,Click to view order")
//                       .setBigContentTitle("OGo-Driver Order")
//                        .setSummaryText(notificationOrdersResponseDataParam?.orderId!!))
                .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bigImageBitMap!!))
                .setFullScreenIntent(buildPendingIntent(context, intent), true)

        return (b)
    }

    private fun buildPendingIntent(context: Context, intent: Intent): PendingIntent {
        return (PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
    }


}
