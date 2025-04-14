package webtech.com.courierdriver.communication.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.apache.commons.lang.builder.ToStringBuilder

/*
Created by ​
Hannure Abdulrahim


on 10/13/2019.
 */

class OrderSwitchingRespData {

    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("cid")
    @Expose
    var cid: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("email_id")
    @Expose
    var emailId: String? = null
    @SerializedName("online_status")
    @Expose
    var onlineStatus: String? = null
    @SerializedName("busy_status")
    @Expose
    var busyStatus: String? = null
    @SerializedName("order_id")
    @Expose
    var orderId: String? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("id", id).append("cid", cid).append("name", name).append("email_id", emailId).append("online_status", onlineStatus).append("busy_status", busyStatus).append("order_id", orderId).toString()
    }

}