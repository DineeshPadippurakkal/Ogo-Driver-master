package webtech.com.courierdriver.communication

/*
Created by ​
Hannure Abdulrahim


on 1/13/2019.
 */


import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.Field

import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query
import webtech.com.courierdriver.communication.response.direction.DirectionApiResponse

interface DirectionApiPostService {

    //https://maps.googleapis.com/maps/api/directions/json?origin=29.3663204,47.9679552&destination=29.2001991,48.0467224&sensor=false&units=metric&mode=driving&key=AIzaSyC695nD8aNVqejZ-r7IrYqJXNCWt4ykB4U
//



    @POST("/maps/api/directions/json")
     fun getDirectionsJson(@Query("origin") origin: String,
                           @Query("destination") destination: String,
                           @Query("sensor") sensor: String,
                           @Query("units") unites: String,
                           @Query("mode") mode: String,
                           @Query("key") key: String): Call<DirectionApiResponse>

}
