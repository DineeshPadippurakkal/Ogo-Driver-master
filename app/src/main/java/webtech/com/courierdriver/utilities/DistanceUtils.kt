package webtech.com.courierdriver.utilities

import org.json.JSONException
import org.json.JSONObject

import java.io.BufferedInputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException

import java.net.URL

/*
Created by ​
Hannure Abdulrahim


on 1/10/2019.
 */



object DistanceUtils {



    var parsedDistance:String?=null
    var parsedDuration:String?=null

    private var distanceAndDuration: ArrayList<String> = ArrayList<String>()
    fun getDistance(sourceLatitude:Double, sourceLongitude:Double, destinationLatitude:Double, destinationLongitude:Double) : ArrayList<String>? {

        val thread = Thread(object:Runnable {
            override fun run() {
                try
                {
                    val url = URL("https://maps.googleapis.com/maps/api/directions/json?origin=" + sourceLatitude + "," + sourceLongitude + "&destination=" + destinationLatitude + "," + destinationLongitude + "&sensor=false&units=metric&mode=driving&key=AIzaSyC695nD8aNVqejZ-r7IrYqJXNCWt4ykB4U")
                    LogUtils.error(LogUtils.TAG, "url In DistanceUtils-> getDistance()=>" + url)
                    val conn = url.openConnection() as HttpURLConnection
                    conn.setRequestMethod("POST")
                    val inputStream = BufferedInputStream(conn.getInputStream())
                    var response = org.apache.commons.io.IOUtils.toString(inputStream, "UTF-8")
                    val jsonObject = JSONObject(response)
                    LogUtils.error(LogUtils.TAG, "parsedDistance=> In DistanceUtils> jsonObject.get(status)#####=>" + jsonObject.get("status"))
                    if (jsonObject.get("status").toString().equals("OVER_QUERY_LIMIT",true))
                    {
                        /////LogUtils.error(LogUtils.TAG, "parsedDistance=> In DistanceUtils-> jsonObject.get(status)=>" + jsonObject.get("status"));
                    }
                    else
                    {
                        val array = jsonObject.getJSONArray("routes")
                        val routes = array.getJSONObject(0)
                        val legs = routes.getJSONArray("legs")
                        val steps = legs.getJSONObject(0)
                        val distance = steps.getJSONObject("distance")
                        val duration = steps.getJSONObject("duration")
                        parsedDistance = distance.getString("text")
                        parsedDuration = duration.getString("text")

                        LogUtils.error(LogUtils.TAG, "parsedDistance=> In DistanceUtils-> getDistance()=>" + parsedDistance)
                        LogUtils.error(LogUtils.TAG, "parsedDuration=> In DistanceUtils-> getDistance()=>" + parsedDuration)

                        distanceAndDuration .clear()
                        distanceAndDuration!!.add(parsedDistance!!)
                        distanceAndDuration!!.add(parsedDuration!!)

                    }
                }
                catch (e: Throwable) {
                    e.printStackTrace()
                }
                catch (e: MalformedURLException) {
                    e.printStackTrace()
                }
                catch (e: IOException) {
                    e.printStackTrace()
                }
                catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })

        ////android.os.NetworkOnMainThreadException will solve
        thread.start()

        /*try {
       thread.join();
       } catch (InterruptedException e) {
       e.printStackTrace();
       }*/
        return distanceAndDuration
    }


    fun convertFromMpsToKmph(mps:Float): Double {
        return (3.6 * mps);

    }


}

