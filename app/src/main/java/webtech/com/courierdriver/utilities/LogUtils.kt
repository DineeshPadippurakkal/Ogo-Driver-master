package webtech.com.courierdriver.utilities

/*
Created by ​
Hannure Abdulrahim
WebTech Co.
Swissbell Plaza Kuwait Hotel,
9th Floor, Office No.- 915, Kuwait City, KUWAIT

on 12/19/2017.
 */

import android.util.Log

import webtech.com.courierdriver.BuildConfig


/*
*
* Ensure every Log.d you made by calling LogUtils.debug instead.
* every Log.e you made by calling LogUtils.error instead.
* every Log.w you made by calling LogUtils.warnings instead.
*
*/

object LogUtils {

    var TAG = "OGODriver"
    var TAG_FCM = "OGODriverFCM"


    fun debug(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }

    fun error(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message)
        }
    }

    fun warnings(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
           Log.w(tag, message)
        }
    }

    fun info(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message)
        }
    }

}
