package webtech.com.courierdriver.utilities

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import webtech.com.courierdriver.communication.response.direction.Duration
import java.util.*

/*
Created by ​
Hannure Abdulrahim


on 12/6/2018.
 */
object DriverUtilities
{

    ////hide keyboard by activity object
    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.getCurrentFocus()
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null)
        {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
    }



    //////hide keyboard by view and activity object
     fun hideKeyboard(view: View,activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)

    }


    var deviceId: String? = null
    /**
     * getDeviceId
     * @param context
     * @return String
     */
    fun getDeviceId(context:Context):String {
        //lets get device Id if not available, we create and save it
        //means device Id is created once
        //if the deviceId is not null, return it
        if (deviceId != null)
        {
            return deviceId!!
        }//end


        //shared preferences

        var preferenceHelper = PreferenceHelper(context)

        //lets get the device Id
        deviceId = preferenceHelper.appUUID
        //if the saved device Id is null, lets create it and save it
        if (deviceId == null)
        {
            //generate new device id
            deviceId = generateUniqueID()

            preferenceHelper.appUUID=deviceId

        }//end if device id was null
        //return
        return deviceId!!
    }//


    /**
     * generateUniqueID - Generate Device Id
     * @return
     */
    fun generateUniqueID():String {
        val id = UUID.randomUUID().toString()
        return id
    }//end method



    // Vibrates the device for 100 milliseconds.
    fun vibrateDevice(context: Context,milliSeconds: Long) {
        val vibrator = ContextCompat.getSystemService(context, Vibrator::class.java)
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= 26) {
                it.vibrate(VibrationEffect.createOneShot(milliSeconds, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(100)
            }
        }
    }


}
