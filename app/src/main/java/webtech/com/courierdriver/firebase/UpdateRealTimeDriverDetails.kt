package webtech.com.courierdriver.firebase

import android.content.Context
import webtech.com.courierdriver.communication.request.RealTimeFireBaseLoginDataModel
import webtech.com.courierdriver.communication.request.RealTimeFireBaseOnlineStatus
import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.utilities.LogUtils
import webtech.com.courierdriver.utilities.PreferenceHelper

/*
Created by ​
Hannure Abdulrahim


on 7/22/2019.
 */
object UpdateRealTimeDriverDetails
{

    var preferenceHelper: PreferenceHelper? = null
    //private var loginRespData: LoginRespData? = null
    var realTimeFireBaseLoginDataModel: RealTimeFireBaseLoginDataModel? = null
    var isSucesssfullFirebasePush = false


    /*
     * this will update driver online status in fire base real time database
     */
    fun updateDriverDetails(context: Context):Boolean {


        return  createDriver(context)


    }


    /*
     * Creating new Driver node under 'driver' , if already exist it will update
     *  This will update driver online status
     *  This will update driver order Status status
     *  This will update driver busy Status status (loginRespData!!.busyStatus)
     *  This will update preferenceHelper!!.fireBase_e5
     *
     *
     */
    private fun createDriver(context: Context):Boolean {

        val mFirebaseDatabase = FirebaseDatabaseUtils.firebaseDatabaseRefrence(context, javaClass.simpleName)

        mFirebaseDatabase.let {

            preferenceHelper = PreferenceHelper(context)

            realTimeFireBaseLoginDataModel = RealTimeFireBaseLoginDataModel()
            realTimeFireBaseLoginDataModel!!.onlineStatus =  RealTimeFireBaseOnlineStatus()
            realTimeFireBaseLoginDataModel = preferenceHelper!!.realTimeFireBaseLoginDataModel!!


                if (realTimeFireBaseLoginDataModel != null) {


                    // TODO
                    // In real apps this DriverID should be fetched
                    // by implementing fire base auth
                    mFirebaseDatabase!!.child(realTimeFireBaseLoginDataModel!!.driverDetails!!.id!!).child(OGoConstant.DRIVER_DETAILS_NODE).setValue(realTimeFireBaseLoginDataModel!!.driverDetails)
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



