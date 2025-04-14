package webtech.com.courierdriver.communication.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.ToStringBuilder;

/*
Created by ​
Hannure Abdulrahim


on 4/20/2020.
 */


public class BulkOrderHistoryResponseData {

    @SerializedName("Id")
    @Expose
    private Integer id;
    @SerializedName("bulk_order_id")
    @Expose
    private String bulkOrderId;
    @SerializedName("sender_name")
    @Expose
    private String senderName;
    @SerializedName("sender_phone")
    @Expose
    private String senderPhone;
    @SerializedName("sender_email")
    @Expose
    private String senderEmail;
    @SerializedName("sender_location")
    @Expose
    private String senderLocation;
    @SerializedName("pick_up_date")
    @Expose
    private String pickUpDate;
    @SerializedName("pick_up_time")
    @Expose
    private String pickUpTime;
    @SerializedName("date_of_order")
    @Expose
    private String dateOfOrder;
    @SerializedName("no_of_parcel")
    @Expose
    private String noOfParcel;
    @SerializedName("total_fare")
    @Expose
    private String totalFare;
    @SerializedName("driver_info")
    @Expose
    private String driverInfo;
    @SerializedName("order_status_int")
    @Expose
    private String orderStatusInt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBulkOrderId() {
        return bulkOrderId;
    }

    public void setBulkOrderId(String bulkOrderId) {
        this.bulkOrderId = bulkOrderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSenderLocation() {
        return senderLocation;
    }

    public void setSenderLocation(String senderLocation) {
        this.senderLocation = senderLocation;
    }

    public String getPickUpDate() {
        return pickUpDate;
    }

    public void setPickUpDate(String pickUpDate) {
        this.pickUpDate = pickUpDate;
    }

    public String getPickUpTime() {
        return pickUpTime;
    }

    public void setPickUpTime(String pickUpTime) {
        this.pickUpTime = pickUpTime;
    }

    public String getDateOfOrder() {
        return dateOfOrder;
    }

    public void setDateOfOrder(String dateOfOrder) {
        this.dateOfOrder = dateOfOrder;
    }

    public String getNoOfParcel() {
        return noOfParcel;
    }

    public void setNoOfParcel(String noOfParcel) {
        this.noOfParcel = noOfParcel;
    }

    public String getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(String totalFare) {
        this.totalFare = totalFare;
    }

    public String getDriverInfo() {
        return driverInfo;
    }

    public void setDriverInfo(String driverInfo) {
        this.driverInfo = driverInfo;
    }

    public String getOrderStatusInt() {
        return orderStatusInt;
    }

    public void setOrderStatusInt(String orderStatusInt) {
        this.orderStatusInt = orderStatusInt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("bulkOrderId", bulkOrderId).append("senderName", senderName).append("senderPhone", senderPhone).append("senderEmail", senderEmail).append("senderLocation", senderLocation).append("pickUpDate", pickUpDate).append("pickUpTime", pickUpTime).append("dateOfOrder", dateOfOrder).append("noOfParcel", noOfParcel).append("totalFare", totalFare).append("driverInfo", driverInfo).append("orderStatusInt", orderStatusInt).toString();
    }

}
