package webtech.com.courierdriver.communication.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.apache.commons.lang.builder.ToStringBuilder

/*
Created by ​
Hannure Abdulrahim


on 7/23/2019.
 */


class ContactUsRespData {

    @SerializedName("contents_en")
    @Expose
    var contentsEn: String? = null
    @SerializedName("phone_en")
    @Expose
    var phoneEn: String? = null
    @SerializedName("email_en")
    @Expose
    var emailEn: String? = null
    @SerializedName("website_en")
    @Expose
    var websiteEn: String? = null
    @SerializedName("address_en")
    @Expose
    var addressEn: String? = null
    @SerializedName("Name")
    @Expose
    var name: String? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("contentsEn", contentsEn).append("phoneEn", phoneEn).append("emailEn", emailEn).append("websiteEn", websiteEn).append("addressEn", addressEn).append("name", name).toString()
    }

}