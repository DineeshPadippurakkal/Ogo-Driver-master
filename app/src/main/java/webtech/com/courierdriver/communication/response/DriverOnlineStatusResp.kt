package webtech.com.courierdriver.communication.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.apache.commons.lang.builder.ToStringBuilder
import java.io.Serializable

/*
Created by ​
Hannure Abdulrahim


on 9/20/2018.
 */

class DriverOnlineStatusResp:Serializable {

    @SerializedName("status")
    @Expose
    var status: Boolean? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("data")
    @Expose
    var data: Int? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("status", status).append("message", message).append("data", data).toString()
    }

}