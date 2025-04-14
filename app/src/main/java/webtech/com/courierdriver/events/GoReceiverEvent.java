package webtech.com.courierdriver.events;

import webtech.com.courierdriver.communication.response.ReceiveCurrentOrderResponseData;


/**
 * Created by Abdulrahim Hannure on 10/9/16.
 */

public class GoReceiverEvent {
    private ReceiveCurrentOrderResponseData orderEntity;

    public GoReceiverEvent(ReceiveCurrentOrderResponseData orderEntity) {
        this.orderEntity = orderEntity;
    }

    public ReceiveCurrentOrderResponseData getOrderEntity() {
        return orderEntity;
    }

    public void setOrderEntity(ReceiveCurrentOrderResponseData orderEntity) {
        this.orderEntity = orderEntity;
    }
}
