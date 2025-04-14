package webtech.com.courierdriver.firebase

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import webtech.com.courierdriver.communication.request.RealTimeFireBaseLoginDataModel
import webtech.com.courierdriver.communication.request.RealTimeFireBaseOrderStatus

import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.utilities.LogUtils
import webtech.com.courierdriver.utilities.PreferenceHelper

/*
Created by ​
Hannure Abdulrahim


on 11/19/2018.
 */
object UpdateRealTimeDriverOrderStatus {

    //private var loginRespData: LoginRespData? = null
    private var realTimeFireBaseLoginDataModel: RealTimeFireBaseLoginDataModel? = null
    private var mFirebaseInstance: FirebaseDatabase? = null

    var preferenceHelper: PreferenceHelper? = null
    var isSucesssfullFirebasePush = false

    /*
     * this will update driver order status in fire base real time database
     */
    fun updateRealTimeDriverOrderStatus(context: Context, orderStatus: String):Boolean {



        return createDriver(context, orderStatus)

    }


    /*
     * Creating new Driver node under 'driver' , if already exist it will update
     * This will also update the driver busy status
     *
     *  This will update driver order Status status
     *  This will update driver online status
     *  This will update driver busy Status status (realTimeFireBaseLoginDataModel!!.busyStatus)
     *  This will update preferenceHelper!!.fireBase_e5
     *
     */
    private fun createDriver(context: Context, orderStatus: String):Boolean {

        val  mFirebaseDatabase = FirebaseDatabaseUtils.firebaseDatabaseRefrence(context, javaClass.simpleName)
        mFirebaseDatabase.let {
            preferenceHelper = PreferenceHelper(context)
            //loginRespData = preferenceHelper!!.loggedInUser
            realTimeFireBaseLoginDataModel= RealTimeFireBaseLoginDataModel()
            realTimeFireBaseLoginDataModel!!.orderStatus =  RealTimeFireBaseOrderStatus()
            realTimeFireBaseLoginDataModel = preferenceHelper!!.realTimeFireBaseLoginDataModel

            LogUtils.error(LogUtils.TAG, "preferenceHelper!!.loggedInUser in UpdateRealTimeDriverOrderStatus  =>" + preferenceHelper!!.loggedInUser)


            //LogUtils.error(LogUtils.TAG,"preferenceHelper!!.driverOnlineStatus!! in MainActivity =>" +preferenceHelper!!.driverOnlineStatus!!)

            if (realTimeFireBaseLoginDataModel != null) {

                //save in shared preferences
                preferenceHelper!!.orderStatus = orderStatus
                realTimeFireBaseLoginDataModel!!.orderStatus!!.orderStatus = preferenceHelper!!.orderStatus



                /////update driver status as  busy and free here....
                if (orderStatus.equals(OGoConstant.DRIVER_ACCEPTED, true) || orderStatus.equals(OGoConstant.DRIVER_AT_SOURCE, true) ||
                        orderStatus.equals(OGoConstant.ORDER_COLLECTED, true) || orderStatus.equals(OGoConstant.DRIVER_AT_DESTINATION, true) ||
                        orderStatus.equals(OGoConstant.DELIVERED, true)||orderStatus.equals(OGoConstant.CANCEL, true) ||
                        orderStatus.equals(OGoConstant.RETURN_ORDER_COLLECTED, true)||orderStatus.equals(OGoConstant.RETURN_ORDER_TO_SOURCE, true) ||
                        orderStatus.equals(OGoConstant.RETURN_ORDER_DELIVERED, true)
                ) {

                    preferenceHelper!!.busyStatus = OGoConstant.BUSY
                    realTimeFireBaseLoginDataModel!!.orderStatus!!.busyStatus = preferenceHelper!!.busyStatus!!

                    //////update fire base value here which comes as cid when order accepted
                    realTimeFireBaseLoginDataModel!!.orderStatus!!.e5 = preferenceHelper!!.fireBase_e5!!

                } else {

                    preferenceHelper!!.busyStatus = OGoConstant.FREE
                    realTimeFireBaseLoginDataModel!!.orderStatus!!.busyStatus = preferenceHelper!!.busyStatus!!


                    preferenceHelper!!.fireBase_e5 = "null"
                    realTimeFireBaseLoginDataModel!!.orderStatus!!.e5 = preferenceHelper!!.fireBase_e5!!



                }


                // TODO
                // In real apps this DriverID should be fetched
                // by implementing fire base auth
                mFirebaseDatabase!!.child(realTimeFireBaseLoginDataModel!!.driverDetails!!.id!!).child(OGoConstant.ORDER_STATUS_NODE).setValue(realTimeFireBaseLoginDataModel!!.orderStatus)
                        .addOnCompleteListener {
                            if(it.isComplete || it.isSuccessful )
                            {
                                LogUtils.error(LogUtils.TAG, javaClass.simpleName +" : isComplete  : "+it.isComplete)
                                LogUtils.error(LogUtils.TAG, javaClass.simpleName +" :isSuccessful : "+it.isSuccessful)
                                isSucesssfullFirebasePush = true

                            }else if (it.isCanceled)
                            {
                                LogUtils.error(LogUtils.TAG, javaClass.simpleName +" :isCanceled : "+it.isCanceled)
                                isSucesssfullFirebasePush = false
                            }


                        }


            }

        }


        return isSucesssfullFirebasePush

    }


}
