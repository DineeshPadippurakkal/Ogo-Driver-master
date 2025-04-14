package webtech.com.courierdriver.communication.response



import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.apache.commons.lang.builder.ToStringBuilder
import java.io.Serializable

/*
Created by ​
Hannure Abdulrahim


on 9/5/2018.
 */

class LoginResp:Serializable {

    @SerializedName("status")
    @Expose
    internal var status: Boolean? = null
    @SerializedName("message")
    @Expose
    internal var message: String? = null
    @SerializedName("data")
    @Expose
    internal var data: LoginRespData? = null

    fun getStatus(): Boolean? {
        return status
    }

    fun setStatus(status: Boolean?) {
        this.status = status
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun getData(): LoginRespData? {
        return data
    }

    fun setData(data: LoginRespData) {
        this.data = data
    }

    override fun toString(): String {
        return ToStringBuilder(this).append("status", status).append("message", message).append("data", data).toString()
    }
}
