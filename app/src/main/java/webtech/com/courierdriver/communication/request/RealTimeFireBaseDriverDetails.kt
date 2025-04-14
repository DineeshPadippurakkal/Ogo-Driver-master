package webtech.com.courierdriver.communication.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.apache.commons.lang.builder.ToStringBuilder

/*
Created by ​
Hannure Abdulrahim


on 7/18/2019.
 */

class RealTimeFireBaseDriverDetails {

    @SerializedName("app_version")
    @Expose
    var appVersion: String? = null
    @SerializedName("cid")
    @Expose
    var cid: String? = null
    @SerializedName("cidType")
    @Expose
    var cidType: String? = null
    @SerializedName("driverImage")
    @Expose
    var driverImage: String? = null
    @SerializedName("e1")
    @Expose
    var e1: String? = null


    @SerializedName("emailId")
    @Expose
    var emailId: String? = null
    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("phone")
    @Expose
    var phone: String? = null
    @SerializedName("vehicleType")
    @Expose
    var vehicleType: String? = null

    @SerializedName("vehicle_no")
    @Expose
    var vehicleNo: String? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("app_version", appVersion).append("cid", cid).append("cidType", cidType).append("driverImage", driverImage).append("e1", e1).append("emailId", emailId).append("id", id).append("name", name).append("phone", phone).append("vehicleType", vehicleType).append("vehicle_no", vehicleNo).toString()
    }

}
