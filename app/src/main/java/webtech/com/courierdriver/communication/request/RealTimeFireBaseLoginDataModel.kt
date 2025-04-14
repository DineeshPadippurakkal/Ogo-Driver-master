package webtech.com.courierdriver.communication.request

import android.location.Location

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.apache.commons.lang.builder.ToStringBuilder

import webtech.com.courierdriver.utilities.AppStatus
import java.io.Serializable

/*
Created by ​
Hannure Abdulrahim


on 7/18/2019.
 */

class RealTimeFireBaseLoginDataModel: Serializable {

    @SerializedName("OnlineStatus")
    @Expose
    var onlineStatus: RealTimeFireBaseOnlineStatus? = null
    @SerializedName("AppStatus")
    @Expose
    var appStatus: RealTimeFireBaseAppStatus? = null
    @SerializedName("OrderStatus")
    @Expose
    var orderStatus: RealTimeFireBaseOrderStatus? = null
    @SerializedName("Location")
    @Expose
    var location: RealTimeFireBaseLocation? = null
    @SerializedName("DriverDetails")
    @Expose
    var driverDetails: RealTimeFireBaseDriverDetails? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("OnlineStatus", onlineStatus).append("AppStatus", appStatus).append("OrderStatus", orderStatus).append("Location", location).append("DriverDetails", driverDetails).toString()
    }

}