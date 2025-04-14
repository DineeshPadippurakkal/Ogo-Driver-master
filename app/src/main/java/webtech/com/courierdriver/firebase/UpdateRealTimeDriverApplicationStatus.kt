package webtech.com.courierdriver.firebase

import android.content.Context
import webtech.com.courierdriver.communication.request.*

import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.utilities.LogUtils
import webtech.com.courierdriver.utilities.LogouUtils
import webtech.com.courierdriver.utilities.PreferenceHelper


/*
Created by ​
Hannure Abdulrahim


on 1/21/2019.
 */
object UpdateRealTimeDriverApplicationStatus {

    // private var loginRespData: LoginRespData? = null
    private var realTimeFireBaseLoginDataModel: RealTimeFireBaseLoginDataModel? = null


    var preferenceHelper: PreferenceHelper? = null
    var isSucesssfullFirebasePush :Boolean = false


    /*
  * this will update driver status in fire base real time database
  * ACTIVE = application is in foreground
    BACKGROUND = application is in backround
    Logout = application log out
  *
  * */

    fun updateRealTimeDriverApplicationStatus(context: Context, applicationStatus: String):Boolean {

         return createDriver(context, applicationStatus)

    }


/*
 * Creating new Driver node under 'driver' , if already exist it will update
 */
  private fun createDriver(context: Context, applicationStatus: String):Boolean {



        val  mFirebaseDatabase = FirebaseDatabaseUtils.firebaseDatabaseRefrence(context, javaClass.simpleName)

        mFirebaseDatabase.let {

            preferenceHelper = PreferenceHelper(context)

            if(applicationStatus.equals(OGoConstant.LOGOUT,true))
            {
                preferenceHelper!!.logout()
                preferenceHelper!!.logOutRealTimeFireBaseUser()
            }

            //login
            realTimeFireBaseLoginDataModel= RealTimeFireBaseLoginDataModel()

            realTimeFireBaseLoginDataModel!!.appStatus =  RealTimeFireBaseAppStatus()

            realTimeFireBaseLoginDataModel = preferenceHelper!!.realTimeFireBaseLoginDataModel

            preferenceHelper!!.appStatus = applicationStatus



            if (realTimeFireBaseLoginDataModel != null)
            {
                //LogUtils.error(LogUtils.TAG,"mpreferenceHelper!!.driverOnlineStatus!! in MainActivity =>" +mpreferenceHelper!!.driverOnlineStatus!!)
                //  LogUtils.error(LogUtils.TAG,"mpreferenceHelper!!.loggedInUser!!.onlineStatus in MainActivity =>" +mpreferenceHelper!!.loggedInUser!!.onlineStatus)


                if (realTimeFireBaseLoginDataModel != null && applicationStatus != null) {

                    // LogUtils.error(LogUtils.TAG , "realTimeFireBaseLoginDataModel!!.orderStatus!!= > " + realTimeFireBaseLoginDataModel!!.orderStatus!!)

                    realTimeFireBaseLoginDataModel!!.appStatus!!.status = applicationStatus

                    // by implementing firebase auth

                    // In real apps this DriverID should be fetched
                    // by implementing firebase auth
                    if (realTimeFireBaseLoginDataModel != null) {
                        mFirebaseDatabase!!.child(realTimeFireBaseLoginDataModel!!.driverDetails!!.id!!).child(OGoConstant.APP_STATUS_NODE).setValue(realTimeFireBaseLoginDataModel!!.appStatus!!)
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

            } else {
                LogUtils.error(LogUtils.TAG, "Driver Logout successfully !!! ")
                ///even if user is not logged in then update its STATUS but not LAT and LONG


                ///initialise object here


                var realTimeLogoutDataModel = LogouUtils.getLogoutModel(context)

                if (realTimeLogoutDataModel!!.appSt!!.status == null || realTimeLogoutDataModel!!.appSt!!.status!!.equals(OGoConstant.LOGOUT, true) ||  preferenceHelper!!.appStatus!!.equals(OGoConstant.LOGOUT, true)) {

                    mFirebaseDatabase!!.child(realTimeLogoutDataModel!!.drD!!.id!!).setValue(realTimeLogoutDataModel)
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



}
