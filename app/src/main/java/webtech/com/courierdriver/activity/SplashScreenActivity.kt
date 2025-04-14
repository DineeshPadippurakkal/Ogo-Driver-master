package webtech.com.courierdriver.activity

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject
import webtech.com.courierdriver.R
import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.databinding.ActivitySplashBinding
import webtech.com.courierdriver.utilities.GPSUtility
import webtech.com.courierdriver.utilities.Language
import webtech.com.courierdriver.utilities.PreferenceHelper


/*
Created by ​
Hannure Abdulrahim


on 12/4/2018.
 */
class SplashScreenActivity : MasterAppCombatActivity(){

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    var preferenceHelper: PreferenceHelper? = null
    private var language: Int = 0
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
//        setContentView(R.layout.activity_splash)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        preferenceHelper = PreferenceHelper(this@SplashScreenActivity)

//        val options = FirebaseOptions.Builder()
//                .setApplicationId("1:659250976484:android:fc5500b78a5736d8")
//                .setGcmSenderId("courierdriver-6b3c1")
//                .setApiKey(getString(R.string.google_maps_key))
//                .build()
//
//
//        FirebaseApp.initializeApp(applicationContext, options)

        // testingFirebaseCrashlytics()

//        FirebaseInstallations.getInstance().getToken(/* forceRefresh */ true)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//
//                        LogUtils.error(LogUtils.TAG, "Installation auth token: " + task.result?.token)
//                    } else {
//
//                        LogUtils.error(LogUtils.TAG, "Unable to get Installation auth token")
//                    }
//                }

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->

                if (task.isSuccessful) {

//                        LogUtils.error(LogUtils.TAG, " Installation ID:  token :${task.result?.token}")
                    //Getting the token if everything is fine
                    val token = task.result

                    preferenceHelper!!.fcmToken = token
                    /////Actual Call


                } else {
//                        LogUtils.error(LogUtils.TAG, "Unable to get Installation ID (token).")
                    Toast.makeText(this, "Unable to get Installation ID (token).", Toast.LENGTH_SHORT).show()
                    return@OnCompleteListener
                }


            })


//        Looper.myLooper()?.let {
//            Handler(it).postDelayed({
//                //Your Code
//                language = preferenceHelper!!.language
//
//
//                if (language === Language.NONE) {
//                    val i = Intent(this@SplashScreenActivity, LanguageActivity::class.java)
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    startActivity(i)
//                    // close this activity
//                    finish()
//
//                } else {
//
//                    /// if location permission not allowed then show Prominent disclosure location screen else go to login screen
//                    var intent:Intent? =null
//                    if (GPSUtility.isLocationPermissionGranted( this@SplashScreenActivity))
//                    {
//                        intent= Intent(this@SplashScreenActivity, LoginActivity::class.java)
//
//                    }else
//                    {
//                        //display Prominent disclosure location screen
//                        intent = Intent(this@SplashScreenActivity, LocationDisclosureActivity::class.java)
//                    }
//
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    startActivity(intent)
//                    // close this activity
//                    finish()
//
//                }
//            },OGoConstant.SPLASH_TIME_OUT!!.toLong())
//        }

//        getSwitchStatus()
        goNormal()
        postNotification()
    }


    public override fun onResume() {
        super.onResume()
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding.shimmerViewContainer.startShimmer()
    }

    public override fun onPause() {
        binding.shimmerViewContainer.stopShimmer()
        super.onPause()
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }
    public fun postNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.d("PERMISSION", "Notification permission granted!")
            } else {
                Log.d("PERMISSION", "Notification permission denied.")
            }
        }
    }
    private fun testingFirebaseCrashlytics()
    {
        //  https://firebase.google.com/docs/crashlytics/upgrade-sdk?platform=android
        ////////////////////////////////////////
//       testing log Events in Firebase Analytics
        ////////////////////////////////////////

        // OPTIONAL: If crash reporting has been explicitly disabled previously, add:
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ID")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "NAME")
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)


        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.log("my message")
        // To log a message to a crash report, use the following syntax:
        crashlytics.log("E/TAG: my message")
        //setBool, setDouble, setFloat, and setInt, setLong, and setString are aggregated into setCustomKey.
        crashlytics.setCustomKey("bool_key", true);
        crashlytics.setCustomKey("double_key", 42.0);
        crashlytics.setCustomKey("float_key", 42.0F);
        crashlytics.setCustomKey("int_key", 42);
        crashlytics.setCustomKey("long_key", 42L);
        crashlytics.setCustomKey("str_key", "42");
        //Firebase Crashlytics SDK
        // //setUserIdentifier is now setUserId. setUserName and setUserEmail are removed.
        crashlytics.setUserId("myAppUserId");

        //Crashlytics.logException(Throwable) is replaced by FirebaseCrashlytics.recordException(Throwable).

        try {
            /* Code that can throw checked
                exceptions. */

            // ...
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }


        ///The CrashlyticsListener is replaced by didCrashOnPreviousExecution().
        if (FirebaseCrashlytics.getInstance().didCrashOnPreviousExecution()) {
            // ...App code to execute if a crash occurred during previous execution.


        }

        ///The crash method is removed.
        throw  RuntimeException("Test Crash");
    }


    private fun getSwitchStatus() {

        val url = "https://agam-6498f.firebaseio.com/off_webtech.json"
        val params = HashMap<String, String>()
        val jsonObject = (params as Map<*, *>?)?.let { JSONObject(it) }

        val queue: RequestQueue = Volley.newRequestQueue(this@SplashScreenActivity)
        val request: JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET,
            url,
            jsonObject,
            Response.Listener { response ->
                try {
                    if(response.getString("ogo_status")=="off") {
                        val i = Intent(this@SplashScreenActivity, ErrorActivity::class.java)
//                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        startActivity(i)
//                        // close this activity
//                        finish()
                    }else{
                        goNormal()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    println("params--> ${e.toString()}")
                    goNormal()
                    Log.e("TAG", "RESPONSE IS ${e.toString()}")

                }
            },
            Response.ErrorListener { error ->
                Log.e("TAG", "RESPONSE IS $error")
                goNormal()
            }) {
        }
        queue.add(request)
    }

    private fun goNormal() {
        Looper.myLooper()?.let {
            Handler(it).postDelayed({
                //Your Code
                language = preferenceHelper!!.language


                if (language === Language.NONE) {
                    val i = Intent(this@SplashScreenActivity, LanguageActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(i)
                    // close this activity
                    finish()

                } else {

                    /// if location permission not allowed then show Prominent disclosure location screen else go to login screen
                    var intent:Intent? =null
                    if (GPSUtility.isLocationPermissionGranted( this@SplashScreenActivity))
                    {
                        intent= Intent(this@SplashScreenActivity, LoginActivity::class.java)

                    }else
                    {
                        //display Prominent disclosure location screen
                        intent = Intent(this@SplashScreenActivity, LocationDisclosureActivity::class.java)
                    }

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    // close this activity
                    finish()

                }
            },OGoConstant.SPLASH_TIME_OUT!!.toLong())
        }
    }


}