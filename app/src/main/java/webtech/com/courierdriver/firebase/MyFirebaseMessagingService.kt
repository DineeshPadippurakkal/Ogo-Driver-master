package webtech.com.courierdriver.firebase

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import webtech.com.courierdriver.OrderSingletonQueue.NearByOrderSingletonQueue
import webtech.com.courierdriver.OrderSingletonQueue.OrderSingletonQueue
import webtech.com.courierdriver.R
import webtech.com.courierdriver.activity.MainActivity
import webtech.com.courierdriver.communication.NotificationResponseData.NotificationNearByOrdersResponseData
import webtech.com.courierdriver.communication.NotificationResponseData.NotificationOrdersResponseData
import webtech.com.courierdriver.communication.NotificationResponseData.OrderCancelledFromRemoteData
import webtech.com.courierdriver.communication.NotificationResponseData.OrderSwitchFromRemoteData
import webtech.com.courierdriver.communication.NotificationResponseData.OrderSwitchedAndAssignedFromRemoteData
import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.events.OrderCancelledFromRemoteEvent
import webtech.com.courierdriver.events.OrderSwitchFromRemoteEvent
import webtech.com.courierdriver.events.OrderSwitchedAndAssignedFromRemoteEvent
import webtech.com.courierdriver.utilities.LogUtils
import webtech.com.courierdriver.utilities.PreferenceHelper


/*
Created by ​
Hannure Abdulrahim


on 9/30/2018.
 */


// data model
// {
//  "to": "eW_LIfHSSTs:APA91bEygIIOGJVZblFJsGKNglPGZOmMWqAc_4nlWm2DL4Td4l7dNoIoR6fYHbu2hvQ7G7naRa2NV6_O1JbZrW2X_0rWudPBZ12GR7aldol9fr1AI8t3ueym3UEgb9KAaoqFSLMv_e8z",
//"data" : {
//     "body" : "Body of Your Notification",
// "mutable_content" : true,
// "category": "rich-apns"
// }
//}


// notification model
// {
//  "to": "eW_LIfHSSTs:APA91bEygIIOGJVZblFJsGKNglPGZOmMWqAc_4nlWm2DL4Td4l7dNoIoR6fYHbu2hvQ7G7naRa2NV6_O1JbZrW2X_0rWudPBZ12GR7aldol9fr1AI8t3ueym3UEgb9KAaoqFSLMv_e8z",
//"notification" : {
//     "body" : "Body of Your Notification",
// "mutable_content" : true,
// "category": "rich-apns"
// }
//}


class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        val CANCEL_ORDER = "CANCEL_ORDER"
        val SWITCH_ORDER = "SWITCH_ORDER"
        val SWITCHED_ORDER_ASSIGNING = "SWITCHED_ORDER_ASSIGNING"
        val LOGOUT_FROM_SERVER = "LOGOUT_FROM_SERVER"
        val ORDER_STUCK = "ORDER_STUCK"

        private const val NEW_ORDER_CHANNEL_ID = "new_order_channel"
        private const val CANCEL_ORDER_CHANNEL_ID = "cancel_order_channel"

    }

    private val TAG = LogUtils.TAG_FCM

    private var preferenceHelper: PreferenceHelper? = null

    override fun onNewToken(mToken: String) {
        super.onNewToken(mToken)
        LogUtils.error(LogUtils.TAG, "TOKEN : $mToken")

        val refreshedToken = mToken

        preferenceHelper = PreferenceHelper(applicationContext)
        if (preferenceHelper!!.fcmToken === null) {
            // No need to notify the token changed broadcast for the first time
            // Token will be sent while registering the device
            preferenceHelper!!.fcmToken = refreshedToken
        } else {
            preferenceHelper!!.fcmToken = refreshedToken
            // Notify the token refreshed broadcast so that token can be updated on server
            val intent = Intent("FCM_TOKEN_REFRESHED")
            val broadcastManager = LocalBroadcastManager.getInstance(this)
            intent.putExtra("token", refreshedToken)
            broadcastManager.sendBroadcast(intent)
        }


    }


    ////Entry point for fire base Cloud Messaging  / notification which affects  following features
//    CANCEL_ORDER (CANCEL)  => order_status_int = 7
//    SWITCH_ORDER  (SWITCHING_ORDER  ) => switch_order_status = 1
//    SWITCHED_ORDER_ASSIGNING ( ASSIGNING_ORDER ) = >  switch_order_status = 2
//    LOGOUT_FROM_SERVER => logout_status
//    ORDER_STUCK (STUCK_ORDER )=>  order_stuck
//    NEARBY_ORDERS => your_near_by_order = show_sms
//    NEW_ORDER ( default is new order )


    /// if you test through  firebase console and send test "title" and "description" then onMessageReceived will call when your app is in  foreground
    //  if you test through  firebase console and send test "title" and "description" then onMessageReceived will not  call when your app is in  background  instead it will receive in notification tray you can see in notification bar
    /// if you are sending  data model ( data payload ) only then also   onMessageReceived will call

    private fun showNewOrderNotification(orderId: String, message: String) {
        val builder = NotificationCompat.Builder(this, NEW_ORDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("New Order: #$orderId")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        val notificationManager = NotificationManagerCompat.from(this)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(orderId.hashCode(), builder.build())
        } else {
            Log.w("Notification", "POST_NOTIFICATIONS permission not granted")
            // Optional: ask for permission if you're inside an Activity
        }
    }

    private fun showCancelOrderNotification(orderId: String, message: String) {
        val builder = NotificationCompat.Builder(this, CANCEL_ORDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Order Cancelled: #$orderId")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(this)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(orderId.hashCode(), builder.build())
        } else {
            Log.w("Notification", "POST_NOTIFICATIONS permission not granted")
            // Optional: Ask permission from the user here if you're in an Activity
        }
    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        //so if you are sending  notification model ( notification payload ) instead of data model ( data payload ) and app is in foreground below block will execute

//        remoteMessage?.let {
//            remoteMessage -> remoteMessage.notification?.let { notification ->
//            notification.title?.let {
//                LogUtils.error(TAG, "remoteMessage!!.notification!!.title!!: " + remoteMessage!!.notification!!.title!!)
//            }
//
//        }
//        }
        val data = remoteMessage.data
        val type = remoteMessage.notification?.title ?: remoteMessage.data["title"]
        val orderId = data["order_id"]
        val message = data["body"]

        when (type) {
            "New Order" -> showNewOrderNotification(orderId.toString(), message!!)
            "Cancel Order" -> showCancelOrderNotification(orderId!!, message!!)
            else -> Log.d("FCM", "Unknown notification type")
        }

        ///// This block is purely for  data model ( data payload ) only
        if (remoteMessage!!.data!!.isNotEmpty()) {


            LogUtils.error(TAG, "Data: " + remoteMessage.data)
            LogUtils.error(TAG, "order_id: " + remoteMessage.data.get("order_id"))
            LogUtils.error(TAG, "order_status_int: " + remoteMessage.data.get("order_status_int"))
            LogUtils.error(
                TAG,
                "switch_order_status: " + remoteMessage.data.get("switch_order_status")
            )
            LogUtils.error(
                TAG,
                "your_near_by_order: " + remoteMessage.data.get("your_near_by_order")
            )

            if ((remoteMessage.data != null) && (remoteMessage.data!!.get("order_id") != null)) {

                if ((remoteMessage.data!!.get("switch_order_status") != null) && (remoteMessage.data!!.get(
                        "switch_order_status"
                    )!!.toString().equals(OGoConstant.ASSIGNING_ORDER, true))
                ) {
                    //// this section is for switched order assigning (Assigned order from remote which is already switched  )


                    ///converting map to jason
                    val gson = Gson()
                    //val jsonOrder = gson.toJson(remoteMessage.data)

                    ////converting  MAP to POJO model class
                    var orderSwitchedAndAssignedFromRemoteData: OrderSwitchedAndAssignedFromRemoteData? =
                        null
                    val jsonElement = gson.toJsonTree(remoteMessage.data)
                    orderSwitchedAndAssignedFromRemoteData = gson.fromJson(
                        jsonElement,
                        OrderSwitchedAndAssignedFromRemoteData::class.java
                    )



                    LogUtils.error(
                        TAG,
                        "MainActivity  isActive :  " + PreferenceManager.getDefaultSharedPreferences(
                            this
                        ).getBoolean("isActive", false)
                    )

                    // saved status in MainActivity,  Check MainActivity onPause , onDestroy ,onResume

                    if (PreferenceManager.getDefaultSharedPreferences(this)
                            .getBoolean("isActive", false)
                    ) {

                        /////MainActivity already visible ,trigger event directly
                        LogUtils.error(TAG, "MainActivity  is in foreground or visible to user")
                        EventBus.getDefault().post(
                            OrderSwitchedAndAssignedFromRemoteEvent(
                                orderSwitchedAndAssignedFromRemoteData
                            )
                        )


                    } else {

                        var intentSwitchOrder: Intent? = null
                        LogUtils.error(
                            TAG,
                            "MainActivity  is in background or not visible or destroyed"
                        )
                        intentSwitchOrder = Intent(this, MainActivity::class.java)

                        /////Send POJO model class , makes sure pojo class implements Serializable
                        intentSwitchOrder.putExtra(
                            SWITCHED_ORDER_ASSIGNING,
                            orderSwitchedAndAssignedFromRemoteData
                        )

                        intentSwitchOrder.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intentSwitchOrder!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intentSwitchOrder)

                    }


                } else if ((remoteMessage.data!!.get("switch_order_status") != null) && (remoteMessage.data!!.get(
                        "switch_order_status"
                    )!!.toString().equals(OGoConstant.SWITCHING_ORDER, true))
                ) {
                    //// this section is for switching order

                    ///converting map to jason
                    val gson = Gson()
                    //val jsonOrder = gson.toJson(remoteMessage.data)

                    ////converting  MAP to POJO model class
                    var orderSwitchRespData: OrderSwitchFromRemoteData? = null
                    val jsonElement = gson.toJsonTree(remoteMessage.data)
                    orderSwitchRespData =
                        gson.fromJson(jsonElement, OrderSwitchFromRemoteData::class.java)



                    LogUtils.error(
                        TAG,
                        "MainActivity  isActive :  " + PreferenceManager.getDefaultSharedPreferences(
                            this
                        ).getBoolean("isActive", false)
                    )

                    // saved status in MainActivity,  Check MainActivity onPause , onDestroy ,onResume
                    if (PreferenceManager.getDefaultSharedPreferences(this)
                            .getBoolean("isActive", false)
                    ) {

                        /////Activity already visible ,trigger event directly
                        LogUtils.error(TAG, "MainActivity  is in foreground or visible to user")
                        EventBus.getDefault().post(OrderSwitchFromRemoteEvent(orderSwitchRespData))

                    } else {

                        var intentSwitchOrder: Intent? = null
                        LogUtils.error(
                            TAG,
                            "MainActivity  is in background or not visible or destroyed"
                        )
                        intentSwitchOrder = Intent(this, MainActivity::class.java)

                        /////Send POJO model class , makes sure pojo class implements Serializable
                        intentSwitchOrder.putExtra(SWITCH_ORDER, orderSwitchRespData)

                        intentSwitchOrder.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intentSwitchOrder!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intentSwitchOrder)

                    }


                } else if ((remoteMessage.data!!.get("order_status_int") != null) && (remoteMessage.data!!.get(
                        "order_status_int"
                    )!!.toString().equals(OGoConstant.CANCEL, true))
                ) {
                    //if order status received  means order cancelled from remote / backend
                    /// cancel order and update in fire base also alog with e5 has to be null in fire base

                    ///converting map to jason
                    val gson = Gson()
                    //val jsonOrder = gson.toJson(remoteMessage.data)

                    ////converting  MAP to POJO model class
                    var orderCancelledFromRemoteData: OrderCancelledFromRemoteData? = null
                    val jsonElement = gson.toJsonTree(remoteMessage.data)
                    orderCancelledFromRemoteData =
                        gson.fromJson(jsonElement, OrderCancelledFromRemoteData::class.java)


                    // EventBus.getDefault().post(OrderCancelledFromRemoteEvent(orderCancelledFromRemoteData))


                    LogUtils.error(
                        TAG,
                        "MainActivity  isActive :  " + PreferenceManager.getDefaultSharedPreferences(
                            this
                        ).getBoolean("isActive", false)
                    )

                    // saved status in MainActivity,  Check MainActivity onPause , onDestroy ,onResume
                    if (PreferenceManager.getDefaultSharedPreferences(this)
                            .getBoolean("isActive", false)
                    ) {

                        /////Activity already visible ,trigger event directly
                        LogUtils.error(TAG, "MainActivity  is in foreground or visible to user")
                        EventBus.getDefault()
                            .post(OrderCancelledFromRemoteEvent(orderCancelledFromRemoteData))


                    } else {

                        var intentCancelOrder: Intent? = null
                        LogUtils.error(
                            TAG,
                            "MainActivity  is in background or not visible or destroyed"
                        )
//                          EventBus.getDefault().post(OrderCancelledFromRemoteEvent(orderCancelledFromRemoteData))
                        intentCancelOrder = Intent(this, MainActivity::class.java)

                        /////Send POJO model class , makes sure pojo class implements Serializable
                        intentCancelOrder.putExtra(CANCEL_ORDER, orderCancelledFromRemoteData)

                        intentCancelOrder.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intentCancelOrder!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intentCancelOrder)

                    }


                } else if ((remoteMessage.data!!.get("your_near_by_order") != null) && (remoteMessage.data!!.get(
                        "your_near_by_order"
                    )!!.toString().equals(OGoConstant.YOUR_NEAR_BY_ORDER, true))
                ) {


                    ///// take orders and add to list
                    //// if next order is same as in list then  ignore it else  add new order and save to shared preferences

                    ///converting map to jason
                    val gson = Gson()
                    //val jsonOrder = gson.toJson(remoteMessage.data)

                    ////converting  MAP to POJO model class
                    var notificationNearByOrdersResponseData: NotificationNearByOrdersResponseData? =
                        null
                    val jsonElement = gson.toJsonTree(remoteMessage!!.data)
                    notificationNearByOrdersResponseData =
                        gson.fromJson(jsonElement, NotificationNearByOrdersResponseData::class.java)

                    NearByOrderSingletonQueue.instance.addOrderAt(
                        notificationNearByOrdersResponseData,
                        NearByOrderSingletonQueue.instance.notificationNearByOrdersResponseDataList!!.size
                    )

                    LogUtils.error(
                        TAG,
                        "notificationNearByOrdersResponseData.orderId => " + notificationNearByOrdersResponseData.orderId
                    )


                } else {


                    ///// take orders and add to list
                    //// if next order is same as in list then  ignore it else  add new order and save to shared preferences

                    ///converting map to jason
                    val gson = Gson()
                    //val jsonOrder = gson.toJson(remoteMessage.data)

                    ////converting  MAP to POJO model class
                    var notificationOrdersResponseData: NotificationOrdersResponseData? = null
                    val jsonElement = gson.toJsonTree(remoteMessage!!.data)
                    notificationOrdersResponseData =
                        gson.fromJson(jsonElement, NotificationOrdersResponseData::class.java)

                    OrderSingletonQueue.instance.addOrderAt(
                        notificationOrdersResponseData,
                        OrderSingletonQueue.instance.notificationOrdersResponseDataList!!.size
                    )

                    LogUtils.error(
                        TAG,
                        "notificationOrdersResponseData.orderId => " + notificationOrdersResponseData.orderId
                    )


                }

            } else if ((remoteMessage.data != null) && (remoteMessage.data!!.get("logout_status") != null)) {
                ///Logout here from server
                var intentLogout: Intent? = null
                intentLogout = Intent(this, MainActivity::class.java)

                /////Send POJO model class , makes sure pojo class implements Serializable
                intentLogout.putExtra(
                    LOGOUT_FROM_SERVER,
                    (remoteMessage.data!!.get("logout_status").toString())
                )

                intentLogout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intentLogout!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intentLogout)


            } else if ((remoteMessage.data != null) && (remoteMessage.data!!.get("order_stuck") != null)) {
                LogUtils.error(
                    TAG,
                    "remoteMessage.data!!.get(\"order_stuck\") => " + remoteMessage.data!!.get("order_stuck")
                )
                ///Logout here from server
                var intentStuck: Intent? = null
                intentStuck = Intent(this, MainActivity::class.java)
                /////Send POJO model class , makes sure pojo class implements Serializable
                intentStuck.putExtra(
                    ORDER_STUCK,
                    (remoteMessage.data!!.get("order_stuck").toString())
                )
                intentStuck.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intentStuck!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intentStuck)


            }

        } else {
            remoteMessage.notification?.let {
                showNotification(it.title, it.body)
            }     }

    }

    private fun showNotification(title: String?, message: String?) {
        Log.d("FCM", "Message received: ${title}")
    }
}
