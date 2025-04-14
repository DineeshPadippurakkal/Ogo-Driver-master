package webtech.com.courierdriver.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import org.greenrobot.eventbus.EventBus
import webtech.com.courierdriver.R
import webtech.com.courierdriver.databinding.ActivityArrivedOrderBinding
import webtech.com.courierdriver.databinding.ActivityLanguageBinding
import webtech.com.courierdriver.events.LanguageSelectedEvent
import webtech.com.courierdriver.utilities.Language
import webtech.com.courierdriver.utilities.LogUtils
import webtech.com.courierdriver.utilities.PreferenceHelper

/*
Created by ​
Hannure Abdulrahim


on 5/9/2019.
 */

// nj
class LanguageActivity : MasterAppCombatActivity() {

    var preferenceHelper: PreferenceHelper? = null
    private var language: Int = 0
    private lateinit var binding: ActivityLanguageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_language)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceHelper = PreferenceHelper(this@LanguageActivity)

        binding.tvEngLayout.setOnClickListener(object:View.OnClickListener {
            override  fun onClick(v:View) {



                preferenceHelper!!.language= Language.ENGLISH
                EventBus.getDefault().post(LanguageSelectedEvent(Language.ENGLISH))

                language = preferenceHelper!!.language
                // Log.e("langauage","is : =>"+language);
//                LogUtils.error(LogUtils.TAG, "first time language selected by user in LanguageActivity  : =>" + language)
                val i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName())
                if (i != null)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                finish()
                startActivity(i)


            }
        })
        binding.tvArLayout.setOnClickListener(object:View.OnClickListener {
            override  fun onClick(v:View) {


                preferenceHelper!!.language= Language.ARABIC
                EventBus.getDefault().post(LanguageSelectedEvent(Language.ARABIC))

                language = preferenceHelper!!.language
                // Log.e("langauage","is : =>"+language);
//                LogUtils.error(LogUtils.TAG, "first time language selected by user in LanguageActivity  : =>" + language)
                val i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName())
                if (i != null)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                finish()
                startActivity(i)
            }
        })






    }

    override fun onResume() {
        super.onResume()
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


    }


    override fun onPause() {

        super.onPause()

        ///these below two line will disable landscape mode ,check onResume() also
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }


    override fun onDestroy() {

        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()


    }




}