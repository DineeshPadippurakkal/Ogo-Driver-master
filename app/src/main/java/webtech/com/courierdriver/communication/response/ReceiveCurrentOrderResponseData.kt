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



class ReceiveCurrentOrderResponseData  : Serializable {

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
    @SerializedName("password")
    @Expose
    var password: String? = null
    @SerializedName("phone")
    @Expose
    var phone: String? = null
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
    @SerializedName("loct_latt")
    @Expose
    var loctLatt: String? = null
    @SerializedName("loct_long")
    @Expose
    var loctLong: String? = null
    @SerializedName("last_location_d_and_t")
    @Expose
    var lastLocationDandT: String? = null
    @SerializedName("online_status")
    @Expose
    var onlineStatus: String? = null
    @SerializedName("online_datetime")
    @Expose
    var onlineDatetime: String? = null
    @SerializedName("busy_status")
    @Expose
    var busyStatus: String? = null
    @SerializedName("order_id")
    @Expose
    var orderId: String? = null
    @SerializedName("cid_type")
    @Expose
    var cidType: String? = null
    @SerializedName("insurance_date")
    @Expose
    var insuranceDate: String? = null
    @SerializedName("driver_image")
    @Expose
    var driverImage: String? = null
    @SerializedName("create_date")
    @Expose
    var createDate: String? = null
    @SerializedName("pre_order_id")
    @Expose
    var preOrderId: String? = null
    @SerializedName("type_of_order")
    @Expose
    var typeOfOrder: String? = null
    @SerializedName("sender_id")
    @Expose
    var senderId: String? = null
    @SerializedName("sender_json")
    @Expose
    var senderJson: String? = null
    @SerializedName("sender_name")
    @Expose
    var senderName: String? = null
    @SerializedName("sender_phone")
    @Expose
    var senderPhone: String? = null
    @SerializedName("sender_email")
    @Expose
    var senderEmail: String? = null
    @SerializedName("sender_location")
    @Expose
    var senderLocation: String? = null
    @SerializedName("sender_latitude")
    @Expose
    var senderLatitude: String? = null
    @SerializedName("sender_longitute")
    @Expose
    var senderLongitute: String? = null
    @SerializedName("receiver_json")
    @Expose
    var receiverJson: String? = null
    @SerializedName("receiver_name")
    @Expose
    var receiverName: String? = null
    @SerializedName("receiver_phone")
    @Expose
    var receiverPhone: String? = null
    @SerializedName("receiver_email")
    @Expose
    var receiverEmail: String? = null
    @SerializedName("receiver_location")
    @Expose
    var receiverLocation: String? = null
    @SerializedName("receiver_latitude")
    @Expose
    var receiverLatitude: String? = null
    @SerializedName("receiver_longitute")
    @Expose
    var receiverLongitute: String? = null
    @SerializedName("delivery_vehicle")
    @Expose
    var deliveryVehicle: String? = null
    @SerializedName("product_image_array_json")
    @Expose
    var productImageArrayJson: String? = null
    @SerializedName("pick_up_date")
    @Expose
    var pickUpDate: String? = null
    @SerializedName("pick_up_time")
    @Expose
    var pickUpTime: String? = null
    @SerializedName("estimated_distance_km")
    @Expose
    var estimatedDistanceKm: String? = null
    @SerializedName("estimated_time_min")
    @Expose
    var estimatedTimeMin: String? = null
    @SerializedName("additional_charges")
    @Expose
    var additionalCharges: String? = null
    @SerializedName("taxes")
    @Expose
    var taxes: String? = null
    @SerializedName("payment_gateway_fee")
    @Expose
    var paymentGatewayFee: String? = null
    @SerializedName("packaging_charge")
    @Expose
    var packagingCharge: String? = null
    @SerializedName("waiting_time_charges")
    @Expose
    var waitingTimeCharges: String? = null
    @SerializedName("total_fare")
    @Expose
    var totalFare: String? = null
    @SerializedName("discount")
    @Expose
    var discount: String? = null
    @SerializedName("additional_discount")
    @Expose
    var additionalDiscount: String? = null
    @SerializedName("isCOD")
    @Expose
    var isCOD: String? = null
    @SerializedName("date_of_order")
    @Expose
    var dateOfOrder: String? = null
    @SerializedName("updated_order_date")
    @Expose
    var updatedOrderDate: String? = null
    @SerializedName("gateway_type")
    @Expose
    var gatewayType: String? = null
    @SerializedName("tracking_info")
    @Expose
    var trackingInfo: String? = null
    @SerializedName("driver_info")
    @Expose
    var driverInfo: String? = null
    @SerializedName("last_updated_by")
    @Expose
    var lastUpdatedBy: String? = null
    @SerializedName("fare")
    @Expose
    var fare: String? = null
    @SerializedName("vehicle_rates_per_km")
    @Expose
    var vehicleRatesPerKm: String? = null
    @SerializedName("order_status_int")
    @Expose
    var orderStatusInt: String? = null
    @SerializedName("non_cid")
    @Expose
    var nonCid: String? = null
    @SerializedName("source")
    @Expose
    var source: String? = null
    @SerializedName("total_invoice_amount")
    @Expose
    var totalInvoiceAmount: String? = null
    @SerializedName("switch_order_status")
    @Expose
    var switchOrderStatus: String? = null
    @SerializedName("previous_driver_email_id")
    @Expose
    var previousDriverEmailId: String? = null
    @SerializedName("previous_driver_name")
    @Expose
    var previousDriverName: String? = null
    @SerializedName("previous_driver_phone")
    @Expose
    var previousDriverPhone: String? = null
    @SerializedName("previous_driver_loct_latt")
    @Expose
    var previousDriverLoctLatt: String? = null
    @SerializedName("previous_driver_loct_long")
    @Expose
    var previousDriverLoctLong: String? = null
    @SerializedName("previous_driver_vehicle_type")
    @Expose
    var previousDriverVehicleType: String? = null
    @SerializedName("previous_driver_vehicle_no")
    @Expose
    var previousDriverVehicleNo: String? = null
    @SerializedName("previous_driver_cid")
    @Expose
    var previousDriverCid: String? = null

    override fun toString(): String {
        return ToStringBuilder(this).append("id", id).append("cid", cid).append("name", name).append("email_id", emailId)
                .append("password", password).append("phone", phone).append("status", status).append("vehicle_type", vehicleType)
                .append("e1", e1).append("e2", e2).append("e3", e3).append("e4", e4).append("e5", e5)
                .append("fcm_Key", fcmKey).append("loct_latt", loctLatt).append("loct_long", loctLong)
                .append("last_location_d_and_t", lastLocationDandT).append("online_status", onlineStatus).append("online_datetime", onlineDatetime)
                .append("busy_status", busyStatus).append("order_id", orderId).append("cid_type", cidType)
                .append("insurance_date", insuranceDate)
                .append("driver_image", driverImage).append("create_date", createDate).append("pre_order_id", preOrderId)
                .append("type_of_order", typeOfOrder).append("sender_id", senderId).append("sender_json", senderJson)
                .append("sender_name", senderName).append("sender_phone", senderPhone).append("sender_email", senderEmail)
                .append("sender_location", senderLocation).append("sender_latitude", senderLatitude).append("sender_longitute", senderLongitute)
                .append("receiver_json", receiverJson).append("receiver_name", receiverName).append("receiver_phone", receiverPhone)
                .append("receiver_email", receiverEmail).append("receiver_location", receiverLocation).append("receiver_latitude", receiverLatitude)
                .append("receiver_longitute", receiverLongitute).append("delivery_vehicle", deliveryVehicle)
                .append("product_image_array_json", productImageArrayJson).append("pick_up_date", pickUpDate).append("pick_up_time", pickUpTime)
                .append("estimated_distance_km", estimatedDistanceKm).append("estimated_time_min", estimatedTimeMin)
                .append("additional_charges", additionalCharges).append("taxes", taxes).append("payment_gateway_fee", paymentGatewayFee)
                .append("packaging_charge", packagingCharge).append("waiting_time_charges", waitingTimeCharges).append("total_fare", totalFare)
                .append("discount", discount).append("additional_discount", additionalDiscount).append("isCOD", isCOD)
                .append("date_of_order", dateOfOrder).append("updated_order_date", updatedOrderDate).append("gateway_type", gatewayType)
                .append("tracking_info", trackingInfo).append("driver_info", driverInfo).append("last_updated_by", lastUpdatedBy)
                .append("fare", fare).append("vehicle_rates_per_km", vehicleRatesPerKm).append("order_status_int", orderStatusInt)
                .append("non_cid", nonCid).append("source", source).append("total_invoice_amount", totalInvoiceAmount)
                .append("switch_order_status", switchOrderStatus).append("previous_driver_email_id", previousDriverEmailId)
                .append("previous_driver_name", previousDriverName).append("previous_driver_phone", previousDriverPhone)
                .append("previous_driver_loct_latt", previousDriverLoctLatt).append("previous_driver_loct_long", previousDriverLoctLong)
                .append("previous_driver_vehicle_type", previousDriverVehicleType).append("previous_driver_vehicle_no", previousDriverVehicleNo)
                .append("previous_driver_cid", previousDriverCid).toString()
    }

}


