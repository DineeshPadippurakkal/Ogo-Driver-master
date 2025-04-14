package webtech.com.courierdriver.communication.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.apache.commons.lang.builder.ToStringBuilder

/*
Created by ​
Hannure Abdulrahim


on 7/18/2019.
 */


class RealTimeFireBaseLogoutLocation {

    @SerializedName("loctLatt")
    @Expose
    var loctLatt: String? = null
    @SerializedName("loctLong")
    @Expose
    var loctLong: String? = null
    @SerializedName("lastLocationDandT")
    @Expose
    var lastLocationDandT: String? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("loctLatt", loctLatt).append("loctLong", loctLong).append("lastLocationDandT", lastLocationDandT).toString()
    }

}