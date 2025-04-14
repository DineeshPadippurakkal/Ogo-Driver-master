package webtech.com.courierdriver.communication.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang.builder.ToStringBuilder

/*
Created by ​
Hannure Abdulrahim


on 7/27/2020.
 */   class PaciFeedResp {
    @SerializedName("ErrorMessage")
    @Expose
    var errorMessage: Any? = null

    @SerializedName("GUID")
    @Expose
    var gUID: Any? = null

    @SerializedName("IsSuccessful")
    @Expose
    var isSuccessful: Boolean? = null

    @SerializedName("Result")
    @Expose
    var result: Boolean? = null

    @SerializedName("SurroundingResults")
    @Expose
    var surroundingResults: Boolean? = null

    @SerializedName("TotalFound")
    @Expose
    var totalFound: Int? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("errorMessage", errorMessage).append("gUID", gUID).append("isSuccessful", isSuccessful).append("result", result).append("surroundingResults", surroundingResults).append("totalFound", totalFound).toString()
    }
}