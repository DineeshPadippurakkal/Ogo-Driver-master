package webtech.com.courierdriver.communication


import webtech.com.courierdriver.communication.RetrofitClient
import webtech.com.courierdriver.communication.RetrofitClient.getClient
import webtech.com.courierdriver.constants.ApiConstant

/*
Created by ​
Hannure Abdulrahim
WebTech Co.
Swissbell Plaza Kuwait Hotel,
9th Floor, Office No.- 915, Kuwait City, KUWAIT


on 7/23/2019.
 */
object ApiGetUtils {

    val apiGetService: ApiPostService
        get() = RetrofitClient.getClient()
       // get() = RetrofitClient_Android.getClient(ApiConstant.BASE_URL)!!.create(APIGetService::class.java)
}