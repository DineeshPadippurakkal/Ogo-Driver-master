package webtech.com.courierdriver.events


import webtech.com.courierdriver.communication.NotificationResponseData.OrderSwitchFromRemoteData


/*
Created by ​
Hannure Abdulrahim


on 10/13/2019.
 */


public class OrderSwitchFromRemoteEvent(orderEntity: OrderSwitchFromRemoteData) {

    var orderSwitchFromRemoteEntity: OrderSwitchFromRemoteData? = null
        private set
 init {
        this.orderSwitchFromRemoteEntity = orderEntity
    }

    fun setOrderCancelledFromRemoteEntity(orderEntity: OrderSwitchFromRemoteData) {
        this.orderSwitchFromRemoteEntity = orderEntity
    }
}
