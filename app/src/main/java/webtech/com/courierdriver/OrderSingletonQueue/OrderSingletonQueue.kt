package webtech.com.courierdriver.OrderSingletonQueue

import java.util.ArrayList

import webtech.com.courierdriver.communication.NotificationResponseData.NotificationOrdersResponseData

/*
Created by ​
Hannure Abdulrahim


on 7/27/2019.
 */


class OrderSingletonQueue private constructor() {

    // retrieve array from anywhere
    @get:Synchronized  var  notificationOrdersResponseDataList: ArrayList<NotificationOrdersResponseData>? = null


    init {
        notificationOrdersResponseDataList = ArrayList()
    }





    //Add element to array

    @Synchronized   fun addOrderAt(notificationOrdersResponseDataParam: NotificationOrdersResponseData, index:Int) {


        notificationOrdersResponseDataList?.let {notificationOrdersResponseDataList->

            if (notificationOrdersResponseDataList!!.size > 0) {



                var isOrderExist=false
                for( ordersResponseData in notificationOrdersResponseDataList!!)
                {

                    if (ordersResponseData.orderId!!.equals(notificationOrdersResponseDataParam.orderId))
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
                    {
                        notificationOrdersResponseDataList!!.add(0,notificationOrdersResponseDataParam)
                    } else if( (index>0) && (notificationOrdersResponseDataList!!.size==index))
                    {
                        /// check it should add at correct place ( at end )
                        notificationOrdersResponseDataList!!.add(index,notificationOrdersResponseDataParam)

                    }
                }

            } else {
                //add at 0 position
                notificationOrdersResponseDataList!!.add(0,notificationOrdersResponseDataParam)


            }

        }



    }



    //Add element to array
    @Synchronized    fun deleteOrder(index:Int) {
        if (notificationOrdersResponseDataList!!.size > 0) {
            if((index < notificationOrdersResponseDataList!!.size ))
            {
                notificationOrdersResponseDataList!!.removeAt(index)

            }



//            for (individualOrder in notificationOrdersResponseDataList!!) {
//
//
//                if (individualOrder.orderId.equals(notificationOrdersResponseDataParam.orderId) ) {
//                    //delete order here
//                    notificationOrdersResponseDataList!!.removeAt(0)
//
//
//                }
//
//
//            }

        }




    }


    companion object {

        private var mInstance: OrderSingletonQueue? = null

        val instance: OrderSingletonQueue
            @Synchronized get() {
                if (mInstance == null)
                    mInstance = OrderSingletonQueue()

                return mInstance as OrderSingletonQueue
            }
    }
}