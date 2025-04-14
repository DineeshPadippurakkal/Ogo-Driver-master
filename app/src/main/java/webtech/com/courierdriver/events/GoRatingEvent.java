package webtech.com.courierdriver.events;

/*
Created by ​
Hannure Abdulrahim


on 11/12/2018.
 */


import webtech.com.courierdriver.communication.response.ReceiveCurrentOrderResponseData;

public class GoRatingEvent {
    private ReceiveCurrentOrderResponseData orderEntity;

    public GoRatingEvent(ReceiveCurrentOrderResponseData orderEntity) {
        this.orderEntity = orderEntity;
    }

    public ReceiveCurrentOrderResponseData getOrderEntity() {
        return orderEntity;
    }

    public void setOrderEntity(ReceiveCurrentOrderResponseData orderEntity) {
        this.orderEntity = orderEntity;
    }
}
