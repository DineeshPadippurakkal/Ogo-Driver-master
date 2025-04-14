package webtech.com.courierdriver.utilities

/*
Created by ​
Hannure Abdulrahim
WebTech Co.
Swissbell Plaza Kuwait Hotel,
9th Floor, Office No.- 915, Kuwait City, KUWAIT

on 4/5/2018.
 */

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.renderscript.ScriptGroup

import android.util.Log
import android.widget.Toast
import com.shashank.sony.fancygifdialoglib.FancyGifDialog
import webtech.com.courierdriver.R

class NetworkUtil {
    internal lateinit var connectivityManager: ConnectivityManager



    val isOnline: Boolean
        get() {
            var result = false
            try {

                val connectivityManager =
                        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val networkCapabilities = connectivityManager.activeNetwork ?: return false
                    val actNw =
                            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
                    result = when {
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        else -> false
                    }
                } else {
                    connectivityManager.run {
                        connectivityManager.activeNetworkInfo?.run {
                            result = when (type) {
                                ConnectivityManager.TYPE_WIFI -> true
                                ConnectivityManager.TYPE_MOBILE -> true
                                ConnectivityManager.TYPE_ETHERNET -> true
                                else -> false
                            }

                        }
                    }
                }

                return result



            } catch (e: Exception) {
                println("CheckConnectivity Exception: " + e.message)
                Log.v("connectivity", e.toString())
            }

            return result
        }

    companion object {
         val instance = NetworkUtil()

        internal lateinit var context: Context

        fun getInstance(ctx: Context): NetworkUtil {
            context = ctx.applicationContext
            return instance
        }
    }

    fun   showCustomNetworkError(contextNetworkError : Context) {

        FancyGifDialog.Builder(contextNetworkError as Activity)
                .setTitle(contextNetworkError.getString(R.string.network_error_title))
                .setMessage(contextNetworkError.getString(R.string.network_error_message))
                .setNegativeBtnText("Cancel")
                .setPositiveBtnBackground(contextNetworkError.getString(R.string.color_them_string))
                .setPositiveBtnText("Ok")
                .setNegativeBtnBackground("#FFA9A7A8")
                .setGifResource(R.drawable.no_internet)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked {
                    // click
                    //Toast.makeText(this, getString(R.string.network_error_title), Toast.LENGTH_SHORT).show()
                    //startActivity(Intent(WifiManager.ACTION_PICK_WIFI_NETWORK))
                    contextNetworkError.startActivity(Intent(Settings.ACTION_SETTINGS))

                }
                .OnNegativeClicked {

                    Toast.makeText(contextNetworkError, "Cancel", Toast.LENGTH_SHORT).show()

                }
                .build()



    }



}
