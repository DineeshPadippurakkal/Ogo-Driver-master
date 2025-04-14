package webtech.com.courierdriver.communication.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang.builder.ToStringBuilder
import java.io.Serializable

/*
Created by ​
Hannure Abdulrahim


on 11/27/2018.
 */

class LoginRespData : Serializable {

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
    @SerializedName("fcm_key")
    @Expose
    var fcmKey: String? = null

    @SerializedName("Loct_latt")
    @Expose
    var loctLatt: String? = null

    @SerializedName("Loct_long")
    @Expose
    var loctLong: String? = null

    ///// old lat and long added newly not present in response
    @SerializedName("previous_latitude")
    @Expose
    var previous_latitude: String? = null

    ///// driver speed not present in response
    @SerializedName("speed_in_kmph")
    @Expose
    var speed_in_kmph: String? = null



    @SerializedName("previous_longitude")
    @Expose
    var previous_longitude: String? = null

    @SerializedName("LastLocationDandT")
    @Expose
    var lastLocationDandT: String? = null
    @SerializedName("token")
    @Expose
    var token: String? = null
    @SerializedName("online_status")
    @Expose
    var onlineStatus: String? = null
    @SerializedName("online_datetime")
    @Expose
    var onlineDatetime: String? = null
    @SerializedName("busy_status")
    @Expose
    var busyStatus: String? = null
    @SerializedName("cid_type")
    @Expose
    var cidType: String? = null
    @SerializedName("driver_image")
    @Expose
    var driverImage: String? = null

    //////// added separately   not present in response
    @SerializedName("order_status_int")
    @Expose
    var orderStatus: String? = null

    ///newly added
    @SerializedName("vehicle_no")
    @Expose
    var vehicle_no: String? = null

    ///newly added
    @SerializedName("app_version")
    @Expose
    var app_version: String? = null

    @SerializedName("logged_in")
    @Expose
    var loggedIn: String? = null
    @SerializedName("logged_in_time")
    @Expose
    var loggedInTime: String? = null
    @SerializedName("logged_out_time")
    @Expose
    var loggedOutTime: String? = null
    @SerializedName("shift_from_time")
    @Expose
    var shiftFromTime: String? = null
    @SerializedName("shift_to_time")
    @Expose
    var shiftToTime: String? = null
    @SerializedName("current_day")
    @Expose
    var currentDay: String? = null




    override fun toString(): String {
        return ToStringBuilder(this).append("id", id).append("cid", cid).append("name", name).append("emailId", emailId).append("phone", phone).append("password", password).append("status", status).append("vehicleType", vehicleType).append("e1", e1).append("e2", e2).append("e3", e3).append("e4", e4).append("e5", e5).append("fcmKey", fcmKey).append("loctLatt", loctLatt).append("loctLong", loctLong).append("previous_latitude", previous_latitude).append("speed_in_kmph",speed_in_kmph).append("previous_longitude", previous_longitude).append("lastLocationDandT", lastLocationDandT).append("token", token).append("onlineStatus", onlineStatus).append("onlineDatetime", onlineDatetime).append("busyStatus", busyStatus).append("cidType", cidType).append("driverImage", driverImage).append("order_status_int", orderStatus).append("vehicle_no", vehicle_no).append("app_version", app_version).append("loggedIn", loggedIn).append("loggedInTime", loggedInTime).append("loggedOutTime", loggedOutTime).append("shiftFromTime", shiftFromTime).append("shiftToTime", shiftToTime).append("currentDay", currentDay).toString()
    }

}
