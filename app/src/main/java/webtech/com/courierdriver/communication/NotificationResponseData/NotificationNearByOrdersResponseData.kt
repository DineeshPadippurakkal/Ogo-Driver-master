package webtech.com.courierdriver.communication.NotificationResponseData

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.apache.commons.lang.builder.ToStringBuilder

/*
Created by ​
Hannure Abdulrahim


on 10/17/2019.
 */


class NotificationNearByOrdersResponseData {

        @SerializedName("id")
        @Expose
        var id: String? = null
        @SerializedName("pre_order_id")
        @Expose
        var preOrderId: String? = null
        @SerializedName("order_id")
        @Expose
        var orderId: String? = null
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
        @SerializedName("fare")
        @Expose
        var fare: String? = null
        @SerializedName("vehicle_rates_per_km")
        @Expose
        var vehicleRatesPerKm: String? = null
        @SerializedName("source")
        @Expose
        var source: String? = null
        @SerializedName("switch_order_status")
        @Expose
        var switchOrderStatus: String? = null
        @SerializedName("your_near_by_order")
        @Expose
        var yourNearByOrder: String? = null
        @SerializedName("go_to_area")
        @Expose
        var goToArea: String? = null
        @SerializedName("sound")
        @Expose
        var sound: String? = null

        override fun toString(): String {
            return ToStringBuilder(this).append("id", id).append("pre_order_id", preOrderId).append("order_id", orderId)
                    .append("type_of_order", typeOfOrder).append("sender_id", senderId).append("sender_json", senderJson)
                    .append("sender_name", senderName).append("sender_phone", senderPhone).append("sender_email", senderEmail)
                    .append("sender_location", senderLocation).append("sender_latitude", senderLatitude)
                    .append("sender_longitute", senderLongitute).append("receiver_json", receiverJson)
                    .append("receiver_name", receiverName).append("receiver_phone", receiverPhone)
                    .append("receiver_email", receiverEmail).append("receiver_location", receiverLocation)
                    .append("receiver_latitude", receiverLatitude).append("receiver_longitute", receiverLongitute)
                    .append("delivery_vehicle", deliveryVehicle).append("product_image_array_json", productImageArrayJson)
                    .append("pick_up_date", pickUpDate).append("pick_up_time", pickUpTime)
                    .append("estimated_distance_km", estimatedDistanceKm).append("estimated_time_min", estimatedTimeMin)
                    .append("additional_charges", additionalCharges).append("taxes", taxes)
                    .append("payment_gateway_fee", paymentGatewayFee).append("packaging_charge", packagingCharge)
                    .append("waiting_time_charges", waitingTimeCharges).append("total_fare", totalFare)
                    .append("discount", discount).append("additional_discount", additionalDiscount)
                    .append("isCOD", isCOD).append("status", status).append("date_of_order", dateOfOrder)
                    .append("updated_order_date", updatedOrderDate).append("gateway_type", gatewayType)
                    .append("tracking_info", trackingInfo).append("driver_info", driverInfo)
                    .append("last_updated_by", lastUpdatedBy).append("e1", e1).append("e2", e2).append("e3", e3)
                    .append("e4", e4).append("e5", e5).append("fare", fare)
                    .append("vehicle_rates_per_km", vehicleRatesPerKm).append("source", source)
                    .append("switch_order_status", switchOrderStatus).append("your_near_by_order", yourNearByOrder)
                    .append("go_to_area", goToArea).append("sound", sound).toString()
        }

    }