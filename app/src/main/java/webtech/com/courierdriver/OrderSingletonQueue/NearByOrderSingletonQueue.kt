package webtech.com.courierdriver.OrderSingletonQueue


import webtech.com.courierdriver.communication.NotificationResponseData.NotificationNearByOrdersResponseData

import java.util.ArrayList

/*
Created by ​
Hannure Abdulrahim


on 10/17/2019.
 */



/* This class will perform two task
 * 1) Maintain list of orders which is near by drivers ( add , delete)
*/

class NearByOrderSingletonQueue private constructor() {

    // retrieve array from anywhere
    @get:Synchronized  var  notificationNearByOrdersResponseDataList: ArrayList<NotificationNearByOrdersResponseData>? = null


    init {
        notificationNearByOrdersResponseDataList = ArrayList()
    }





    //Add element to array
    @Synchronized   fun addOrderAt(NotificationNearByOrdersResponseDataParam: NotificationNearByOrdersResponseData, index:Int) {

        if (notificationNearByOrdersResponseDataList!!.size > 0) {



            var isOrderExist=false
            for( ordersResponseData in notificationNearByOrdersResponseDataList!!)
            {

                if (ordersResponseData.orderId!!.equals(NotificationNearByOrdersResponseDataParam.orderId))
                {
                    isOrderExist=true
                    break

                }else
                {
                    isOrderExist=false

                }

            }

            if(!isOrderExist)
            {
                if(index== 0)
                    notificationNearByOrdersResponseDataList!!.add(index,NotificationNearByOrdersResponseDataParam)
                else if( (index>0) && (notificationNearByOrdersResponseDataList!!.size==index))
                {
                    /// check it should add at correct place ( at end )
                    notificationNearByOrdersResponseDataList!!.add(index,NotificationNearByOrdersResponseDataParam)

                }
            }

        } else {
            //add at 0 position
            notificationNearByOrdersResponseDataList!!.add(0,NotificationNearByOrdersResponseDataParam)


        }

    }



    //Add element to array
    @Synchronized    fun deleteOrder(index:Int) {
        if (notificationNearByOrdersResponseDataList!!.size > 0) {
            if((index < notificationNearByOrdersResponseDataList!!.size ))
            {
                notificationNearByOrdersResponseDataList!!.removeAt(index)

            }



        }




    }


    companion object {

        private var mInstance: NearByOrderSingletonQueue? = null

        val instance: NearByOrderSingletonQueue
            @Synchronized get() {
                if (mInstance == null)
                    mInstance = NearByOrderSingletonQueue()

                return mInstance as NearByOrderSingletonQueue
            }
    }
}