package webtech.com.courierdriver.communication

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*
Created by ​
Hannure Abdulrahim


on 1/14/2019.
 */

object DirectionRetrofitClient {

    private var retrofit: Retrofit? = null

    fun getClient(baseUrl: String): Retrofit? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
        return retrofit
    }
}
