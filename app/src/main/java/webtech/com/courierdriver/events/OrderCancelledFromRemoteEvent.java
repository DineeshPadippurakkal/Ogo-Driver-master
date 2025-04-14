package webtech.com.courierdriver.events;

import webtech.com.courierdriver.communication.NotificationResponseData.OrderCancelledFromRemoteData;

/*
Created by ​
Hannure Abdulrahim


on 2/24/2019.
 */
public class OrderCancelledFromRemoteEvent {

    private OrderCancelledFromRemoteData orderEntity;

    public OrderCancelledFromRemoteEvent(OrderCancelledFromRemoteData orderEntity) {
        this.orderEntity = orderEntity;
    }

    public OrderCancelledFromRemoteData getOrderCancelledFromRemoteEntity() {
        return orderEntity;
    }

    public void setOrderCancelledFromRemoteEntity(OrderCancelledFromRemoteData orderEntity) {
        this.orderEntity = orderEntity;
    }
}
