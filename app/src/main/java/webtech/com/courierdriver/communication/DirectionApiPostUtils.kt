package webtech.com.courierdriver.communication

import webtech.com.courierdriver.constants.ApiConstant

/*
Created by ​
Hannure Abdulrahim


on 1/13/2019.
 */
object DirectionApiPostUtils {

    val directionApiPostService: DirectionApiPostService
        get() = DirectionRetrofitClient.getClient(ApiConstant.BASE_URL_DIRECTION_API)!!.create(DirectionApiPostService::class.java)
}