package webtech.com.courierdriver.communication.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.apache.commons.lang.builder.ToStringBuilder
import java.io.Serializable

/*
Created by ​
Hannure Abdulrahim


on 8/26/2019.
 */


class OGoDriverUpdateInfo: Serializable {

    @SerializedName("isUpdateAvailableOgoDriver")
    @Expose
    var isUpdateAvailableOgoDriver: Boolean? = null
    @SerializedName("isForceUpdateOgoDriver")
    @Expose
    var isForceUpdateOgoDriver: Boolean? = null
    @SerializedName("versionCodeOgoDriver")
    @Expose
    var versionCodeOgoDriver: String? = null
    @SerializedName("newFeaturesOgoDriver")
    @Expose
    var newFeaturesOgoDriver: String? = null
    @SerializedName("appUpdateMessageAndroidEngOgoDriver")
    @Expose
    var appUpdateMessageAndroidEngOgoDriver: String? = null
    @SerializedName("appUpdateMessageAndroidArOgoDriver")
    @Expose
    var appUpdateMessageAndroidArOgoDriver: String? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("isUpdateAvailableOgoDriver", isUpdateAvailableOgoDriver).append("isForceUpdateOgoDriver", isForceUpdateOgoDriver).append("versionCodeOgoDriver", versionCodeOgoDriver).append("newFeaturesOgoDriver", newFeaturesOgoDriver).append("appUpdateMessageAndroidEngOgoDriver", appUpdateMessageAndroidEngOgoDriver).append("appUpdateMessageAndroidArOgoDriver", appUpdateMessageAndroidArOgoDriver).toString()
    }

}
