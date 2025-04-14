package webtech.com.courierdriver.communication.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang.builder.ToStringBuilder

class ShiftResponse {
    @SerializedName("status")
    @Expose
    var status: Boolean? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("data")
    @Expose
    var data: ShiftResponseData? = null

    class ShiftResponseData {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("email_id")
        @Expose
        var email_id: String? = null

        @SerializedName("shift_from_time")
        @Expose
        var shift_from_time: String? = null

        @SerializedName("shift_to_time")
        @Expose
        var shift_to_time: String? = null

        @SerializedName("current_day")
        @Expose
        var current_day: String? = null
    }


    override fun toString(): String {
        return ToStringBuilder(this)
            .append("status", status)
            .append("message", message)
            .append("data", data).toString()
    }
}