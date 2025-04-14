package webtech.com.courierdriver.communication.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.apache.commons.lang.builder.ToStringBuilder

/*
Created by ​
Hannure Abdulrahim


on 7/18/2019.
 */


class RealTimeFireBaseLocation {

    @SerializedName("loctLatt")
    @Expose
    var loctLatt: String? = null
    @SerializedName("loctLong")
    @Expose
    var loctLong: String? = null
    @SerializedName("lastLocationDandT")
    @Expose
    var lastLocationDandT: String? = null
    @SerializedName("previous_latitude")
    @Expose
    var previousLatitude: String? = null
    @SerializedName("previous_longitude")
    @Expose
    var previousLongitude: String? = null
    @SerializedName("speed_in_kmph")
    @Expose
    var speedInKmph: String? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("loctLatt", loctLatt).append("loctLong", loctLong).append("lastLocationDandT", lastLocationDandT).append("previous_latitude", previousLatitude).append("previous_longitude", previousLongitude).append("speed_in_kmph", speedInKmph).toString()
    }

}