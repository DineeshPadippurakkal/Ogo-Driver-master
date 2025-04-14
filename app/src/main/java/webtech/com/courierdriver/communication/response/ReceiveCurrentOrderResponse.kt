package webtech.com.courierdriver.communication.response

/*
Created by ​
Hannure Abdulrahim


on 11/7/2018.
 */
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang.builder.ToStringBuilder
import java.io.Serializable

class ReceiveCurrentOrderResponse: Serializable {

    @SerializedName("status")
    @Expose
    var status: Boolean? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("data")
    @Expose
    var data: List<ReceiveCurrentOrderResponseData>? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("status", status).append("message", message).append("data", data).toString()
    }

}