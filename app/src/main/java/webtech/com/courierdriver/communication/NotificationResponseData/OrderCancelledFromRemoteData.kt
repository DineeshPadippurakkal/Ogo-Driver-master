package webtech.com.courierdriver.communication.NotificationResponseData


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.apache.commons.lang.builder.ToStringBuilder
import java.io.Serializable

/*
Created by ​
Hannure Abdulrahim


on 2/24/2019.
 */
class OrderCancelledFromRemoteData : Serializable {

    @SerializedName("order_id")
    @Expose
    var orderId: String? = null
    @SerializedName("order_status_int")
    @Expose
    var orderStatusInt: Int? = null
    @SerializedName("email_id")
    @Expose
    var emailId: String? = null

    @SerializedName("sound")
    @Expose
    var sound: String? = null

    @SerializedName("switch_order_status")
    @Expose
    var switch_order_status: String? = null


    override fun toString(): String {
        return ToStringBuilder(this).append("orderId", orderId).append("orderStatusInt", orderStatusInt).append("emailId", emailId).append("sound", sound).append("switch_order_status", switch_order_status).toString()
    }

}