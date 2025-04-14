package webtech.com.courierdriver.communication.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.apache.commons.lang.builder.ToStringBuilder


/*
Created by ​
Hannure Abdulrahim


on 7/18/2019.
 */

class RealTimeFireBaseAppStatus {

    @SerializedName("status")
    @Expose
    var status: String? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("status", status).toString()
    }

}