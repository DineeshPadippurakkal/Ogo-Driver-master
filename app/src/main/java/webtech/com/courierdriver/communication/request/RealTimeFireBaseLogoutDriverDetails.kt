package webtech.com.courierdriver.communication.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.apache.commons.lang.builder.ToStringBuilder

/*
Created by ​
Hannure Abdulrahim


on 7/18/2019.
 */


class RealTimeFireBaseLogoutDriverDetails {

    @SerializedName("emailId")
    @Expose
    var emailId: String? = null
    @SerializedName("id")
    @Expose
    var id: String? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("emailId", emailId).append("id", id).toString()
    }

}