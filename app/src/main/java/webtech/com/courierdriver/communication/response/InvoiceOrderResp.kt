package webtech.com.courierdriver.communication.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.apache.commons.lang.builder.ToStringBuilder

/*
Created by ​
Hannure Abdulrahim


on 12/3/2019.
 */


class InvoiceOrderResp {

    @SerializedName("status")
    @Expose
    var status: Boolean? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("data")
    @Expose
    var data: List<InvoiceOrderRespData>? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("status", status).append("message", message).append("data", data).toString()
    }

}