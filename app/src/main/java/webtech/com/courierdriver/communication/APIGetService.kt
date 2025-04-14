package webtech.com.courierdriver.communication


import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import webtech.com.courierdriver.communication.response.ContactUsResp
import webtech.com.courierdriver.communication.response.PaciFeedResp


/*
Created by ​
Hannure Abdulrahim
WebTech Co.
Swissbell Plaza Kuwait Hotel,
9th Floor, Office No.- 915, Kuwait City, KUWAIT

on 7/23/2019.
 */
interface APIGetService {

    // option 1: a resource relative to your base URL

    @Streaming
    @GET
    fun getContactUsDetails(@Url contactUsUrl: String): Call<ContactUsResp>


    /// this is way if you are using GET method and  header is added and you have any param then  @url + @HeaderMap is sufficient and make sure you make url with param
    @Streaming
    @GET
    fun getPACIFeedRequest(@Url pacifeedFullUrl: String): Call<PaciFeedResp>


}