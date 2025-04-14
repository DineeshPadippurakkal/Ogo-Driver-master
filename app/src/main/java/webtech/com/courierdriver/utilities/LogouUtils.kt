package webtech.com.courierdriver.utilities

import android.content.Context
import webtech.com.courierdriver.communication.request.*


/*
Created by ​
Hannure Abdulrahim


on 7/24/2019.
 */
object LogouUtils
{
    fun getLogoutModel(context:Context):RealTimeFireBaseLogoutDataModel
    {
        var preferenceHelper: PreferenceHelper? = null
        preferenceHelper = PreferenceHelper(context)

        var realTimeLogoutDataModel = RealTimeFireBaseLogoutDataModel()
        //logout
        realTimeLogoutDataModel.appSt =  RealTimeFireBaseLogoutAppStatus()
        realTimeLogoutDataModel.onSt =  RealTimeFireBaseLogoutOnlineStatus()
        realTimeLogoutDataModel.lo = RealTimeFireBaseLogoutLocation()
        realTimeLogoutDataModel.drD = RealTimeFireBaseLogoutDriverDetails()


        realTimeLogoutDataModel = preferenceHelper!!.realTimeFireBaseLogOutDataModel!!

       // val mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
       // LogUtils.error("MakanDriver:", "### Current time = > " + mydate!!)


        realTimeLogoutDataModel.lo!!.loctLatt = preferenceHelper!!.lastLat
        realTimeLogoutDataModel.lo!!.loctLong = preferenceHelper!!.lastLong
        realTimeLogoutDataModel.lo!!.lastLocationDandT= preferenceHelper!!.lastLocationDandT


        return  realTimeLogoutDataModel

    }
}
