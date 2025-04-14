package webtech.com.courierdriver.communication.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.apache.commons.lang.builder.ToStringBuilder

/*
Created by ​
Hannure Abdulrahim


on 7/18/2019.
 */


class RealTimeFireBaseOrderStatus {

    @SerializedName("order_status_int")
    @Expose
    var orderStatus: String? = null
    @SerializedName("busyStatus")
    @Expose
    var busyStatus: String? = null
    @SerializedName("e5")
    @Expose
    var e5: String? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("orderStatus", orderStatus).append("busyStatus", busyStatus).append("e5", e5).toString()
    }

}