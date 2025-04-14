package webtech.com.courierdriver.ApiCallUtils

import android.content.Context
import android.widget.Toast
import webtech.com.courierdriver.communication.ApiPostService
import webtech.com.courierdriver.communication.ApiPostUtils
import webtech.com.courierdriver.utilities.LogUtils
import webtech.com.courierdriver.utilities.PreferenceHelper


/*
Created by ​
Hannure Abdulrahim


on 12/23/2019.
 */
class ApiCall {

    companion object {
        val instance = ApiCall()

        internal lateinit var context: Context

        fun getInstance(ctx: Context): ApiCall {
            context = ctx.applicationContext
            return instance
        }
    }




    /*
  * web service call via retrofit
  * Call provides two methods for making HTTP requests:

execute() – Synchronously send the request and return its response.
enqueue(retrofit2.Callback) – Asynchronously send the request and notify callback of its response or if an error occurred talking to the server, creating the request, or processing the response.
  * */


    ///enqueue(retrofit2.Callback)
    /* fun loginPostToGetNewShiftTime(context: Context,userNameStr: String, passwordStr: String,fcmToken:String,versionName:String)  {

        var apiPostService: ApiPostService? = null
        apiPostService = ApiPostUtils.apiPostService
         var preferenceHelper: PreferenceHelper?=null
         var isApiCallSucess = false
        apiPostService!!.postLogin(userNameStr, passwordStr,fcmToken,versionName).enqueue(object : Callback<LoginResp> {
            override fun onResponse(call: Call<LoginResp>, response: Response<LoginResp>) {

                // LogUtils.error(LogUtils.TAG, "response.raw().toString() =>" + response.raw().toString())

                if (response.isSuccessful)
                {

                    showLoginResponse(response.body()!!.toString())

                    if (response.body()!!.status.toString().equals("true", true)) {
                        LogUtils.error(LogUtils.TAG, "postLogin response.body()!!.message.toString() =>" + response.body()!!.message.toString())
                        // showProgress(false)
                        preferenceHelper = PreferenceHelper(context)
                        preferenceHelper!!.loggedInUser = response.body()!!.data!!





                    }else
                    {



                        Toast.makeText(context, response.body()!!.message, Toast.LENGTH_LONG).show();

                    }


                }
            }

            override fun onFailure(call: Call<LoginResp>, t: Throwable) {

                LogUtils.error("TAG", "Unable to submit loginPostToGetNewShiftTime to API.")
                Toast.makeText(context, " Unable to submit loginPostToGetNewShiftTime to API.!", Toast.LENGTH_LONG).show()



            }
        })



    }*/




    ///execute() – Synchronously send the request and return its response.
    //'android.os.NetworkOnMainThreadException'? if you use directly since  Synchronously retrofit call gives you return value directly so you have to handle separately in separate thread

    fun loginPostToGetNewShiftTime(context: Context,userNameStr: String, passwordStr: String,fcmToken:String,versionName:String)   {

        var isWebServiceCallSucess = false

        val thread = Thread(object:Runnable {
            public override fun run() {
                try
                {
                    //Your code goes here
                    isWebServiceCallSucess=     callSyncApi(context,userNameStr, passwordStr,fcmToken,versionName)



                }
                catch (e:Exception) {
                    e.printStackTrace()
                    isWebServiceCallSucess = false
                }
            }

        })

        thread.start()
      //  return isWebServiceCallSucess




    }

    fun  callSyncApi (context: Context,userNameStr: String, passwordStr: String,fcmToken:String,versionName:String) : Boolean
    {
        var apiPostService: ApiPostService? = null
        apiPostService = ApiPostUtils.apiPostService
        var preferenceHelper: PreferenceHelper?=null
        var isWebServiceCallSyncSucess = false

        val callSync = apiPostService.postLogin(userNameStr, passwordStr,fcmToken,versionName)
        try
        {

            val response = callSync.execute()


            val apiResponse = response.body()
            //showLoginResponse(response.body()!!.toString())

            if(response.isSuccessful)
            {
                preferenceHelper = PreferenceHelper(context)
                preferenceHelper!!.loggedInUser = response.body()!!.data!!
                isWebServiceCallSyncSucess = true

            }else
            {
                Toast.makeText(context, response.body()!!.message, Toast.LENGTH_LONG).show();
                isWebServiceCallSyncSucess = false

            }




            //API response
            LogUtils.error("TAG", "apiResponse : "+ apiResponse)

        }
        catch (ex:Exception) {
            ex.printStackTrace()

            LogUtils.error("TAG", "Unable to submit loginPostToGetNewShiftTime to API.")
            Toast.makeText(context, " Unable to submit loginPostToGetNewShiftTime to API.!", Toast.LENGTH_LONG).show()
            isWebServiceCallSyncSucess = false

        }

        return isWebServiceCallSyncSucess
    }





    fun showLoginResponse(response: String) {
        LogUtils.error("TAG>>>>RESPONSE>>>>", response)
    }











}