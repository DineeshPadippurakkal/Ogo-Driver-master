package webtech.com.courierdriver.events;

import webtech.com.courierdriver.communication.response.ReceiveCurrentOrderResponseData;

/*
Created by ​
Hannure Abdulrahim


on 11/5/2018.
 */


public class OrderDetailsEvent {
    private ReceiveCurrentOrderResponseData orderDetailsEntity;

    public OrderDetailsEvent(ReceiveCurrentOrderResponseData orderDetailsEntity) {
        this.orderDetailsEntity = orderDetailsEntity;
    }

    public ReceiveCurrentOrderResponseData getOrderDetailsEntity() {
        return orderDetailsEntity;
    }

    public void setOrderDetailsEntity(ReceiveCurrentOrderResponseData orderDetailsEntity) {
        this.orderDetailsEntity = orderDetailsEntity;
    }
}
