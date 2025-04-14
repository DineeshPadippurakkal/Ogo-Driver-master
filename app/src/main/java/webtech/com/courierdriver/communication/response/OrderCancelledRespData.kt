package webtech.com.courierdriver.communication.response

/*
Created by ​
Hannure Abdulrahim


on 2/27/2019.
 */


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang.builder.ToStringBuilder

class OrderCancelledRespData {

    @SerializedName("order_id")
    @Expose
    var orderId: String? = null
    @SerializedName("e5")
    @Expose
    var e5: String? = null
    @SerializedName("driver_info")
    @Expose
    var driverInfo: String? = null
    @SerializedName("order_status_int")
    @Expose
    var orderStatusInt: String? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("orderId", orderId).append("e5", e5).append("driverInfo", driverInfo).append("orderStatusInt", orderStatusInt).toString()
    }

}