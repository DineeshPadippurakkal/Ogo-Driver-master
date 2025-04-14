package webtech.com.courierdriver.activity

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import webtech.com.courierdriver.utilities.Language
import webtech.com.courierdriver.utilities.LogUtils
import webtech.com.courierdriver.utilities.PreferenceHelper
import java.util.*


/*
Created by ​
Hannure Abdulrahim
WebTech Co.
Swissbell Plaza Kuwait Hotel,
9th Floor, Office No.- 915, Kuwait City, KUWAIT

on 27/Sept/2017.
 */

open class MasterAppCombatActivity : AppCompatActivity() {
    private val language: Int = 0
    private var preferenceHelper: PreferenceHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ///Locale.getDefault().getLanguage()=="en"
        preferenceHelper = PreferenceHelper(this)

        // fixing portrait mode problem for SDK 26 if using windowIsTranslucent = true
        /*if (Build.VERSION.SDK_INT == 26) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }*/




        //// 0 IS FOR ENGLISH
        //// 1 IS FOR ARABIC
        //// -1 is for NONE

        if (preferenceHelper!!.language == Language.ENGLISH) {
            setLangLocal("en")
            LogUtils.error(LogUtils.TAG, "Language in MasterAppCombatActivity =>$language")
        } else if (preferenceHelper!!.language == Language.ARABIC) {
            setLangLocal("ar")
            LogUtils.error(LogUtils.TAG,"Language in MasterAppCombatActivity =>$language")
        }else
        {
            ////default is english language
            setLangLocal("en");
            LogUtils.error(LogUtils.TAG,"Language in MasterAppCombatActivity  ( default ) =>"+language)

        }
    }

    fun setLangLocal(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config,
                baseContext.resources.displayMetrics)



    }


    ///This will call : Wherever Activity Implementing MasterAppCombatActivity , if its destroyed or removed from task manager or clear from task manager

    override fun onDestroy() {
        super.onDestroy()
       // yourAppIsBeingKilled()


    }

    fun setLog(message :String ) {
        Log.e("OGODRIVER",message)
    }


// here you can write logic what if app killed
@SuppressLint("NewApi")
fun yourAppIsBeingKilled()
{
    val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
    val taskList = activityManager.getRunningTasks(10)
    if (!taskList.isEmpty())
    {
        var runningTaskInfo = taskList.get(0)
        if ((runningTaskInfo.topActivity != null && !runningTaskInfo.topActivity!!.getClassName().contains("webtech.com.courierdriver")))
        {
            //You are App is being killed so here you can add some code
            LogUtils.error(LogUtils.TAG," in MasterAppCombatActivity::  ##Your  App is being killed##");
        }
    }
}



}
