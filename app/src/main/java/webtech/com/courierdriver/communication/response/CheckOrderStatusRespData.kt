package webtech.com.courierdriver.communication.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.apache.commons.lang.builder.ToStringBuilder

/*
Created by ​
Hannure Abdulrahim


on 7/28/2019.
 */


class CheckOrderStatusRespData {

    @SerializedName("order_status_int")
    @Expose
    var orderStatusInt: String? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("orderStatusInt", orderStatusInt).toString()
    }

}