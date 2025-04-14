package webtech.com.courierdriver.communication

/*
Created by ​
Hannure Abdulrahim
WebTech Co.
Swissbell Plaza Kuwait Hotel,
9th Floor, Office No.- 915, Kuwait City, KUWAIT

on 1/28/2018.
 */

object ApiPostUtils {

    val apiPostService: ApiPostService
        get() = RetrofitClient.getClient()
     //   get() = RetrofitClient.getClient(ApiConstant.BASE_URL)!!.create(ApiPostService::class.java)
}