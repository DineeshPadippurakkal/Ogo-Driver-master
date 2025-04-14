package webtech.com.courierdriver.communication.request

import android.location.Location

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.apache.commons.lang.builder.ToStringBuilder

import webtech.com.courierdriver.utilities.AppStatus

/*
Created by ​
Hannure Abdulrahim


on 7/18/2019.
 */



class RealTimeFireBaseLogoutDataModel {

    @SerializedName("OnlineStatus")
    @Expose
    var onSt: RealTimeFireBaseLogoutOnlineStatus? = null
    @SerializedName("AppStatus")
    @Expose
    var appSt: RealTimeFireBaseLogoutAppStatus? = null
    @SerializedName("Location")
    @Expose
    var lo: RealTimeFireBaseLogoutLocation? = null
    @SerializedName("DriverDetails")
    @Expose
    var drD: RealTimeFireBaseLogoutDriverDetails? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("OnlineStatus", onSt).append("AppStatus", appSt).append("Location", lo).append("DriverDetails", drD).toString()
    }

}