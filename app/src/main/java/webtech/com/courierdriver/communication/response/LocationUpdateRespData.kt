package webtech.com.courierdriver.communication.response

/*
Created by ​
Hannure Abdulrahim


on 9/19/2018.
 */

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.apache.commons.lang.builder.ToStringBuilder

class LocationUpdateRespData {

    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("cid")
    @Expose
    var cid: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("email_id")
    @Expose
    var emailId: String? = null
    @SerializedName("phone")
    @Expose
    var phone: String? = null
    @SerializedName("password")
    @Expose
    var password: String? = null
    @SerializedName("status")
    @Expose
    var status: String? = null
    @SerializedName("vehicle_type")
    @Expose
    var vehicleType: String? = null
    @SerializedName("e1")
    @Expose
    var e1: String? = null
    @SerializedName("e2")
    @Expose
    var e2: String? = null
    @SerializedName("e3")
    @Expose
    var e3: String? = null
    @SerializedName("e4")
    @Expose
    var e4: String? = null
    @SerializedName("e5")
    @Expose
    var e5: String? = null
    @SerializedName("token")
    @Expose
    var token: String? = null
    @SerializedName("fcm_id")
    @Expose
    var fcmId: String? = null
    @SerializedName("app_status")
    @Expose
    var appStatus: String? = null
    @SerializedName("driver_status")
    @Expose
    var driverStatus: String? = null
    @SerializedName("lat")
    @Expose
    var lat: String? = null
    @SerializedName("lon")
    @Expose
    var lon: String? = null
    @SerializedName("updated_date")
    @Expose
    var updatedDate: String? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("id", id).append("cid", cid).append("name", name).append("emailId", emailId).append("phone", phone).append("password", password).append("status", status).append("vehicleType", vehicleType).append("e1", e1).append("e2", e2).append("e3", e3).append("e4", e4).append("e5", e5).append("token", token).append("fcmId", fcmId).append("appStatus", appStatus).append("driverStatus", driverStatus).append("lat", lat).append("lon", lon).append("updatedDate", updatedDate).toString()
    }

}
