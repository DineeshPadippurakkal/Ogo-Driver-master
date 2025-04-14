package webtech.com.courierdriver.communication.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.ToStringBuilder;
import java.util.ArrayList;



/*
Created by ​
Hannure Abdulrahim


on 4/20/2020.
 */

public class BulkOrderHistoryResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private ArrayList<BulkOrderHistoryResponseData> data = null;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<BulkOrderHistoryResponseData> getData() {
        return data;
    }

    public void setData(ArrayList<BulkOrderHistoryResponseData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("status", status).append("message", message).append("data", data).toString();
    }

}