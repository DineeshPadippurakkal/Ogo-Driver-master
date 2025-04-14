package webtech.com.courierdriver.events;

import webtech.com.courierdriver.communication.response.OrderHistoryResponseData;

/*
Created by ​
Hannure Abdulrahim


on 11/4/2018.
 */


public class OrderHistoryDetailEvent {
    private OrderHistoryResponseData orderHistoryEntity;

    public OrderHistoryDetailEvent(OrderHistoryResponseData orderHistoryEntity) {
        this.orderHistoryEntity = orderHistoryEntity;
    }

    public OrderHistoryResponseData getOrderHistoryEntity() {
        return orderHistoryEntity;
    }

    public void setOrderHistoryEntity(OrderHistoryResponseData orderHistoryEntity) {
        this.orderHistoryEntity = orderHistoryEntity;
    }
}
