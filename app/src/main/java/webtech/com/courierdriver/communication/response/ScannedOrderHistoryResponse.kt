package webtech.com.courierdriver.communication.response

/*
Created by ​
Hannure Abdulrahim


on 10/30/2018.
 */


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.apache.commons.lang.builder.ToStringBuilder

class ScannedOrderHistoryResponse {

    @SerializedName("status")
    @Expose
    var status: Boolean? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("data")
    @Expose
    var data: ArrayList<ScannedOrderHistoryResponseData>? = null


    override fun toString(): String {
        return ToStringBuilder(this).append("status", status).append("message", message).append("data", data).toString()
    }

}