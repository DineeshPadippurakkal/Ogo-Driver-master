package webtech.com.courierdriver.firebase

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import webtech.com.courierdriver.BuildConfig
import webtech.com.courierdriver.activity.LoginActivity
import webtech.com.courierdriver.activity.MainActivity
import webtech.com.courierdriver.communication.response.OGoDriverUpdateInfo
import webtech.com.courierdriver.customview.UpdateMeeDialog
import webtech.com.courierdriver.events.ApplicationUpdateEvent
import webtech.com.courierdriver.utilities.LogUtils
import java.util.concurrent.TimeUnit

/*
Created by ​
Hannure Abdulrahim


on 4/22/2019.
 */
object CheckUpdateFromFireBaseConfig {

    ///////////////////////////////////////////
    //Check versionCode and versionName from firebase remote config file and proceed accordingly ( removed and deleted )
    // ogo_driver_update_info from firebase remote config file and proceed accordingly
    ///////////////////////////////////////////

    fun checkAndShowUpdateAvailableAlert(mcontext: Context) {
        try {
            // val VERSION = "versionCode"
            // val NEW_FEATURES = "newFeatures"


            /////NEW
            val APP_UPDATE_OGO_DRIVER = "ogo_driver_update_info"



            // Fetch the remote config values from the server at a certain rate. If we are in debug
            // mode, fetch every time we create the app. Otherwise, fetch a new value ever X hours.
            var minimumFetchIntervalInSeconds = 0
            if (BuildConfig.DEBUG) {
                minimumFetchIntervalInSeconds = 0
            } else {
                //// convert 1 hrs to seconds ....change value here if you want 2,3 hrs
                minimumFetchIntervalInSeconds = TimeUnit.HOURS.toSeconds(1).toInt()
            }
            var firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
            var configSettings = FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(TimeUnit.HOURS.toSeconds(minimumFetchIntervalInSeconds.toLong()))
                    .build()

            ///// testing one hrs to seconds
            //LogUtils.error(LogUtils.TAG, " TimeUnit.HOURS.toSeconds(1) : " + TimeUnit.HOURS.toSeconds(1).toInt())

            //On first run of an application firebaseRemoteConfig won't fetch data and will return default values.
            firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
            //var defaultValueHashMap = HashMap<String, String>()
            //defaultValueHashMap.put(VERSION, BuildConfig.VERSION_CODE.toString())
            //defaultValueHashMap.put(NEW_FEATURES, "New features")
            //firebaseRemoteConfig.setDefaults(defaultValueHashMap as Map<String, Any>)



            val fetch: Task<Void> = firebaseRemoteConfig.fetch()
            fetch.addOnSuccessListener {
                firebaseRemoteConfig.activate()
                LogUtils.error(LogUtils.TAG, "FirebaseRemoteConfig : Firebase Remote config Fetch Succeeded and Activated ");
                // Update parameter method(s) would be here


                //val newFeatures = firebaseRemoteConfig.getString(NEW_FEATURES)
                // val remoteVersionCode = firebaseRemoteConfig.getLong(VERSION)
                //val remoteVersionCode = firebaseRemoteConfig.getString(VERSION)
                //LogUtils.error(LogUtils.TAG, "FirebaseRemoteConfig : Remote version: " + remoteVersionCode + ", New Features: " + newFeatures)

//                      proceedToUpdate(mcontext,remoteVersionCode,newFeatures)

//                        remoteVersionCode?.let {remoteVersionCodeStr->
//                            newFeatures?.let {newFeaturesStr->
//                                    enableForceUpdate(mcontext, remoteVersionCodeStr, newFeaturesStr)
//                                }
//
//                            }


                val appUpdateOgoDriver = firebaseRemoteConfig.getString(APP_UPDATE_OGO_DRIVER)

                appUpdateOgoDriver?.let {

                    LogUtils.error(LogUtils.TAG, "FirebaseRemoteConfig : appUpdateOgoDriver: " + it)
                    proceedToUpdate(mcontext,it)
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun proceedToUpdate(mcontext: Context, appUpdateOgoDriver: String) {


        ///converting map to jason
        val gson = Gson()
        ////converting  MAP to POJO model class
        var ogo_driver_update_info: OGoDriverUpdateInfo? = null
        //if its already json string then we dont need gson.toJsonTree
        //val jsonElement = gson.toJsonTree(appUpdateOgoDriver)
        ogo_driver_update_info = gson.fromJson(appUpdateOgoDriver, OGoDriverUpdateInfo::class.java)

        ogo_driver_update_info?.let {


//                            LogUtils.error(LogUtils.TAG, "FirebaseRemoteConfig :   isUpdateAvailableOgoDriver: " +   it.isUpdateAvailableOgoDriver)
//                            LogUtils.error(LogUtils.TAG, "FirebaseRemoteConfig :   isForceUpdateOgoDriver: " +   it.isForceUpdateOgoDriver)
//                            LogUtils.error(LogUtils.TAG, "FirebaseRemoteConfig :   versionCodeOgoDriver: " +   it.versionCodeOgoDriver)
//                            LogUtils.error(LogUtils.TAG, "FirebaseRemoteConfig :   newFeaturesOgoDriver: " +   it.newFeaturesOgoDriver)
//                            LogUtils.error(LogUtils.TAG, "FirebaseRemoteConfig :   appUpdateMessageAndroidEngOgoDriver: " +   it.appUpdateMessageAndroidEngOgoDriver)
//                            LogUtils.error(LogUtils.TAG, "FirebaseRemoteConfig :   appUpdateMessageAndroidArOgoDriver: " +   it.appUpdateMessageAndroidArOgoDriver)



            it.isUpdateAvailableOgoDriver?.let { isUpdateAvailableOgoDriver ->
                it.isForceUpdateOgoDriver?.let { isForceUpdateOgoDriver ->

                    if (isUpdateAvailableOgoDriver && isForceUpdateOgoDriver) {
                        it.versionCodeOgoDriver?.let { versionCodeOgoDriver ->

                            it.newFeaturesOgoDriver?.let { newFeaturesOgoDriver ->

                                enableForceUpdate(mcontext, versionCodeOgoDriver, newFeaturesOgoDriver)
                            }

                        }

                    }


                }
            }


        }




    }

    private fun enableForceUpdate(mcontext: Context, versionCodeOgoDriver: String, newFeaturesOgoDriver: String)
    {
        if (versionCodeOgoDriver.toInt() > BuildConfig.VERSION_CODE) {


            if (mcontext is MainActivity) {
                /// force to login and redirect to LoginActivity

                Looper.myLooper()?.let {
                    Handler(it).postDelayed({
                        // YOUR CODE after duration finished
                        EventBus.getDefault().post(ApplicationUpdateEvent())


                    },1 * 1000)//one sec duration
                }



            } else if (mcontext is LoginActivity) {

                ///display dialogue and send APPLICATION_ID to redirect to play store
                UpdateMeeDialog.showDialogAddRoute(mcontext as Activity, BuildConfig.APPLICATION_ID, BuildConfig.VERSION_CODE.toString(), versionCodeOgoDriver, newFeaturesOgoDriver)
            }


        }

    }




}