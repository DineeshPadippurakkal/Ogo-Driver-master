package webtech.com.courierdriver.communication.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.apache.commons.lang.builder.ToStringBuilder

/*
Created by ​
Hannure Abdulrahim


on 12/3/2019.
 */
class InvoiceOrderRespData {


    @SerializedName("order_id")
    @Expose
    var orderId: String? = null
    @SerializedName("sender_name")
    @Expose
    var senderName: String? = null
    @SerializedName("sender_phone")
    @Expose
    var senderPhone: String? = null
    @SerializedName("sender_location")
    @Expose
    var senderLocation: String? = null
    @SerializedName("receiver_name")
    @Expose
    var receiverName: String? = null
    @SerializedName("receiver_phone")
    @Expose
    var receiverPhone: String? = null
    @SerializedName("receiver_location")
    @Expose
    var receiverLocation: String? = null
    @SerializedName("pick_up_date")
    @Expose
    var pickUpDate: String? = null
    @SerializedName("pick_up_time")
    @Expose
    var pickUpTime: String? = null
    @SerializedName("driver_info")
    @Expose
    var driverInfo: String? = null
    @SerializedName("cid")
    @Expose
    var cid: String? = null
    @SerializedName("total_invoice_amount")
    @Expose
    var totalInvoiceAmount: String? = null
    @SerializedName("driver_name")
    @Expose
    var driverName: String? = null
    @SerializedName("driver_phone")
    @Expose
    var driverPhone: String? = null
    @SerializedName("driver_email")
    @Expose
    var driverEmail: String? = null
    @SerializedName("driver_vehicle_type")
    @Expose
    var driverVehicleType: String? = null
    @SerializedName("driver_cid")
    @Expose
    var driverCid: String? = null
    @SerializedName("driver_credits")
    @Expose
    var driverCredits: String? = null
    @SerializedName("created_date")
    @Expose
    var createdDate: String? = null
    @SerializedName("is_paid")
    @Expose
    var isPaid: String? = null
    @SerializedName("is_deleted")
    @Expose
    var isDeleted: String? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("orderId", orderId).append("senderName", senderName).append("senderPhone", senderPhone).append("senderLocation", senderLocation).append("receiverName", receiverName).append("receiverPhone", receiverPhone).append("receiverLocation", receiverLocation).append("pickUpDate", pickUpDate).append("pickUpTime", pickUpTime).append("driverInfo", driverInfo).append("cid", cid).append("totalInvoiceAmount", totalInvoiceAmount).append("driverName", driverName).append("driverPhone", driverPhone).append("driverEmail", driverEmail).append("driverVehicleType", driverVehicleType).append("driverCid", driverCid).append("driverCredits", driverCredits).append("createdDate", createdDate).append("isPaid", isPaid).append("isDeleted", isDeleted).toString()
    }

}
