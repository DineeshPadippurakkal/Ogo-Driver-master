package webtech.com.courierdriver.communication.response

/*
Created by ​
Hannure Abdulrahim


on 10/30/2018.
 */

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.apache.commons.lang.builder.ToStringBuilder

class OrderHistoryResponseData {

    @SerializedName("Id")
    @Expose
    var id: Int? = null
    @SerializedName("driver_email")
    @Expose
    var driverEmail: String? = null
    @SerializedName("order_id")
    @Expose
    var orderId: String? = null
    @SerializedName("dandt")
    @Expose
    var dandt: String? = null
    @SerializedName("pre_order_id")
    @Expose
    var preOrderId: String? = null
    @SerializedName("type_of_order")
    @Expose
    var typeOfOrder: String? = null
    @SerializedName("from")
    @Expose
    var from: String? = null
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
    @SerializedName("to")
    @Expose
    var to: String? = null
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
    var estimatedDistanceKm: Double? = null
    @SerializedName("estimated_time_min")
    @Expose
    var estimatedTimeMin: Double? = null
    @SerializedName("additional_charges")
    @Expose
    var additionalCharges: Int? = null
    @SerializedName("taxes")
    @Expose
    var taxes: Int? = null
    @SerializedName("payment_gateway_fee")
    @Expose
    var paymentGatewayFee: Int? = null
    @SerializedName("packaging_charge")
    @Expose
    var packagingCharge: Int? = null
    @SerializedName("waiting_time_charges")
    @Expose
    var waitingTimeCharges: Int? = null
    @SerializedName("fare")
    @Expose
    var fare: String? = null
    @SerializedName("vehicle_rates_per_km")
    @Expose
    var vehicleRatesPerKm: Int? = null
    @SerializedName("total_fare")
    @Expose
    var totalFare: String? = null
    @SerializedName("discount")
    @Expose
    var discount: Int? = null
    @SerializedName("additional_discount")
    @Expose
    var additionalDiscount: Int? = null
    @SerializedName("isCOD")
    @Expose
    var isCOD: String? = null
    @SerializedName("status")
    @Expose
    var status: String? = null
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

    //added later
    @SerializedName("order_status_int")
    @Expose
    var order_status_int: String? = null

    @SerializedName("source")
    @Expose
    var source: String? = null
    @SerializedName("total_invoice_amount")
    @Expose
    var total_invoice_amount: String? = null



    override fun toString(): String {
        return ToStringBuilder(this).append("id", id).append("driverEmail", driverEmail).append("orderId", orderId).append("dandt", dandt).append("preOrderId", preOrderId).append("typeOfOrder", typeOfOrder).append("from", from).append("senderName", senderName).append("senderPhone", senderPhone).append("senderEmail", senderEmail).append("senderLocation", senderLocation).append("senderLatitude", senderLatitude).append("senderLongitute", senderLongitute).append("to", to).append("receiverName", receiverName).append("receiverPhone", receiverPhone).append("receiverEmail", receiverEmail).append("receiverLocation", receiverLocation).append("receiverLatitude", receiverLatitude).append("receiverLongitute", receiverLongitute).append("deliveryVehicle", deliveryVehicle).append("productImageArrayJson", productImageArrayJson).append("pickUpDate", pickUpDate).append("pickUpTime", pickUpTime).append("estimatedDistanceKm", estimatedDistanceKm).append("estimatedTimeMin", estimatedTimeMin).append("additionalCharges", additionalCharges).append("taxes", taxes).append("paymentGatewayFee", paymentGatewayFee).append("packagingCharge", packagingCharge).append("waitingTimeCharges", waitingTimeCharges).append("fare", fare).append("vehicleRatesPerKm", vehicleRatesPerKm).append("totalFare", totalFare).append("discount", discount).append("additionalDiscount", additionalDiscount).append("isCOD", isCOD).append("status", status).append("dateOfOrder", dateOfOrder).append("updatedOrderDate", updatedOrderDate).append("gatewayType", gatewayType).append("trackingInfo", trackingInfo).append("driverInfo", driverInfo).append("lastUpdatedBy", lastUpdatedBy).append("order_status_int", order_status_int).append("source", source).append("total_invoice_amount", total_invoice_amount).toString().toString()
    }

}