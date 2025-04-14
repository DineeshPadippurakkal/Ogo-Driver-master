package webtech.com.courierdriver.events;

import webtech.com.courierdriver.communication.response.ReceiveCurrentOrderResponseData;

/*
Created by ​
Hannure Abdulrahim


on 8/21/2019.
 */

public class GoBackToSenderEvent {
    private ReceiveCurrentOrderResponseData orderEntity;

    public GoBackToSenderEvent(ReceiveCurrentOrderResponseData orderEntity) {
        this.orderEntity = orderEntity;
    }

    public ReceiveCurrentOrderResponseData getOrderEntity() {
        return orderEntity;
    }

    public void setOrderEntity(ReceiveCurrentOrderResponseData orderEntity) {
        this.orderEntity = orderEntity;
    }
}
