package webtech.com.courierdriver.communication.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.apache.commons.lang.builder.ToStringBuilder

/*
Created by ​
Hannure Abdulrahim


on 7/18/2019.
 */


class RealTimeFireBaseOnlineStatus {

    @SerializedName("onlineStatus")
    @Expose
    var onlineStatus: String? = null
    @SerializedName("onlineDatetime")
    @Expose
    var onlineDatetime: String? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("onlineStatus", onlineStatus).append("onlineDatetime", onlineDatetime).toString()
    }

}