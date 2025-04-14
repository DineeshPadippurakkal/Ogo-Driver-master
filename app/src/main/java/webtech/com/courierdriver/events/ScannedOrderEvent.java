package webtech.com.courierdriver.events;

import webtech.com.courierdriver.communication.response.ReceiveCurrentOrderResponseData;

/*
Created by ​
Hannure Abdulrahim


on 6/1/2021.
 */


public class ScannedOrderEvent {
    private String scannedOrder;

    public ScannedOrderEvent(String scannedOrder) {
        this.scannedOrder = scannedOrder;
    }

    public String getScannedOrder() {
        return scannedOrder;
    }

    public void setScannedOrder(String scannedOrder) {
        this.scannedOrder = scannedOrder;
    }
}

