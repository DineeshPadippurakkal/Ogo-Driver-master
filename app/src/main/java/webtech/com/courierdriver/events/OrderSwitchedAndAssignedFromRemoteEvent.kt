package webtech.com.courierdriver.events

import webtech.com.courierdriver.communication.NotificationResponseData.OrderSwitchedAndAssignedFromRemoteData

/*
Created by ​
Hannure Abdulrahim


on 10/14/2019.
 */


 public class OrderSwitchedAndAssignedFromRemoteEvent(orderEntity: OrderSwitchedAndAssignedFromRemoteData) {

    var orderSwitchedAndAssignedFromRemoteEntity: OrderSwitchedAndAssignedFromRemoteData? = null
        private set

    init {
        this.orderSwitchedAndAssignedFromRemoteEntity = orderEntity
    }

    fun setOrderCancelledFromRemoteEntity(orderEntity: OrderSwitchedAndAssignedFromRemoteData) {
        this.orderSwitchedAndAssignedFromRemoteEntity = orderEntity
    }
}