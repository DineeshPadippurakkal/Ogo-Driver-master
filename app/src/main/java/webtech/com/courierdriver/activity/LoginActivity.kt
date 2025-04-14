package webtech.com.courierdriver.activity

import android.Manifest
import android.Manifest.permission.READ_CONTACTS
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.AlertDialog
import android.app.LoaderManager.LoaderCallbacks
import android.content.BroadcastReceiver
import android.content.Context
import android.content.CursorLoader
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.Loader
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.agrawalsuneet.loaderspack.loaders.CircularSticksLoader
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import webtech.com.courierdriver.BuildConfig
import webtech.com.courierdriver.R
import webtech.com.courierdriver.communication.ApiPostService
import webtech.com.courierdriver.communication.ApiPostUtils
import webtech.com.courierdriver.communication.request.RealTimeFireBaseAppStatus
import webtech.com.courierdriver.communication.request.RealTimeFireBaseDriverDetails
import webtech.com.courierdriver.communication.request.RealTimeFireBaseLocation
import webtech.com.courierdriver.communication.request.RealTimeFireBaseLoginDataModel
import webtech.com.courierdriver.communication.request.RealTimeFireBaseLogoutAppStatus
import webtech.com.courierdriver.communication.request.RealTimeFireBaseLogoutDataModel
import webtech.com.courierdriver.communication.request.RealTimeFireBaseLogoutDriverDetails
import webtech.com.courierdriver.communication.request.RealTimeFireBaseLogoutLocation
import webtech.com.courierdriver.communication.request.RealTimeFireBaseLogoutOnlineStatus
import webtech.com.courierdriver.communication.request.RealTimeFireBaseOnlineStatus
import webtech.com.courierdriver.communication.request.RealTimeFireBaseOrderStatus
import webtech.com.courierdriver.communication.response.LoginResp
import webtech.com.courierdriver.communication.response.LoginRespData
import webtech.com.courierdriver.constants.ApiConstant
import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.databinding.ActivityLoginBinding
import webtech.com.courierdriver.firebase.CheckUpdateFromFireBaseConfig
import webtech.com.courierdriver.utilities.CourierAlertDialog
import webtech.com.courierdriver.utilities.DriverUtilities
import webtech.com.courierdriver.utilities.GPSUtility
import webtech.com.courierdriver.utilities.LogUtils
import webtech.com.courierdriver.utilities.NetworkUtil
import webtech.com.courierdriver.utilities.PreferenceHelper
import webtech.com.courierdriver.utilities.UpdateRealTimeDriverDetails


/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : MasterAppCombatActivity(), LoaderCallbacks<Cursor> {

    private lateinit var tokenReceiver: BroadcastReceiver


    private var apiPostService: ApiPostService? = null


    private var preferenceHelper: PreferenceHelper? = null
    private var mpreferenceHelper: PreferenceHelper? = null
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // fixing portrait mode problem for SDK 26 if using windowIsTranslucent = true
        /*  if (Build.VERSION.SDK_INT == 26) {
              setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
          } else {
              setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
          }*/

        // radioGroup.clearCheck()
        // this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT

        preferenceHelper = PreferenceHelper(this@LoginActivity)


        // preferenceHelper!!.appVersionName=BuildConfig.VERSION_NAME

        /////set default language as english
//        if (preferenceHelper!!.language == Language.NONE)
//            preferenceHelper!!.language = Language.ENGLISH


        //////////////////////////////////S
//            //Setting each character animation delay
//            typeWriterView.setDelay(OGoConstant.TWO_SEC!!.toInt())///3 sec
//
//            //Setting music effect On/Off
//            typeWriterView.setWithMusic(false)
//
//            //Animating Text
//            typeWriterView.animateText(getString(R.string.login_text));
//            typeWriterView.removeAnimation()
        //////////////////////////////////E


        ///un stretchable background image can be set here
        // window.setBackgroundDrawableResource(R.drawable.ic_login_bg)


        ///set the image to round ImageView library
        // logo.setImageDrawable(getResources().getDrawable(R.drawable.splash_logo))
        //logo.setImageResource(R.drawable.splash_logo);

        if (preferenceHelper!!.lastUsername != null) {
            binding.userNameTV.setText(preferenceHelper!!.lastUsername)
        }

        if (preferenceHelper!!.lastPassword != null) {
            binding.passwordEdtTxt.setText(preferenceHelper!!.lastPassword)
        }


        ///Here change intype to asterisk password
//        binding.passwordEdtTxt.setTransformationMethod(AsteriskPasswordTransformationMethod())

        // Set up the login form.
        populateAutoComplete()
        binding.passwordEdtTxt.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })


        binding.tvSignInLayout.setOnClickListener {

            attemptLogin()

            /*  FancyGifDialog.Builder(this)
                      .setTitle("Order Collected ?")
                      .setMessage("Please Make sure you collected order before going to proceed")
                      .setNegativeBtnText("Cancel")
                      .setPositiveBtnBackground("#FF4081")
                      .setPositiveBtnText("Ok")
                      .setNegativeBtnBackground("#FFA9A7A8")
                      .setGifResource(R.drawable.collected_order)   //Pass your Gif here
                      .isCancellable(true)
                      .OnPositiveClicked { Toast.makeText(this@LoginActivity, "Ok", Toast.LENGTH_SHORT).show() }
                      .OnNegativeClicked { Toast.makeText(this@LoginActivity, "Cancel", Toast.LENGTH_SHORT).show() }
                      .build()
              */
        }


        binding.tvRegister.setOnClickListener {

            val i = Intent(this@LoginActivity, RegisterActivity::class.java)
            //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            // finish()


        }

        binding.tvPrivacyPolicy.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ApiConstant.PRIVACY_POLICY))
            startActivity(browserIntent)
        }


        /* slideView.setOnSlideCompleteListener {


             val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
             vibratorService.vibrate(500)


             //slideView!!.setTextColor(getResources().getColor(R.color.your_color))
             //val  myList =  ColorStateList(states, colors);
             //slideView!!.setButtonBackgroundColor(myList)


         }*/


        /* textViewSignUp.setOnClickListener {

             val i = Intent(this@LoginActivity, SignUpActivity::class.java)
             startActivity(i)
             //finish()
         }

         textViewForgotPwd.setOnClickListener {

             val i = Intent(this@LoginActivity, TestFontActivity::class.java)
             startActivity(i)
             //finish()
         }*/


    }

    override fun onStart() {
        super.onStart()

        ///////////////////////////////////////////////////////////////////////////S
        //add a new BroadcastReceiver: to receive new token
        tokenReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val token = intent.getStringExtra("token")
                if (token != null) {
                    LogUtils.error("firebase FCM toke in LoginActivity->onStart() ", token.toString())
                    // send token to your server or check at login time
                    //TODO call update FC token webservice
                }
            }
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(tokenReceiver,
            IntentFilter("FCM_TOKEN_REFRESHED"))
        ///////////////////////////////////////////////////////////////////////////E

    }

    override fun onStop() {
        super.onStop()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(tokenReceiver)

    }


    private fun populateAutoComplete() {
        if (!mayRequestContacts()) {
            return
        }

        loaderManager.initLoader(0, null, this)
    }

    private fun mayRequestContacts(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(binding.userNameTV, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok,
                    { requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS) })
        } else {
            requestPermissions(arrayOf(READ_CONTACTS), REQUEST_READ_CONTACTS)
        }
        return false
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete()
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {


        // Reset errors.
        binding.userNameTV.error = null
        binding.passwordEdtTxt.error = null

        // Store values at the time of the login attempt.
        val userNameStr = binding.userNameTV.text.toString()
        val passwordStr = binding.passwordEdtTxt.text.toString()

        ////////////////////S
        // get selected radio button from radioGroup
        // val selectedId = radioGroup.getCheckedRadioButtonId()
        // find the radiobutton by returned id
        //radioButtonVehicleType  =  findViewById(selectedId)
        // val vanType=radioButtonVehicleType!!.getText().toString()
        ////////////////////E

        //Toast.makeText(this@LoginActivity,radioButtonVehicleType!!.getText(), Toast.LENGTH_SHORT).show();


        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            binding.passwordEdtTxt.error = getString(R.string.error_invalid_password)
            focusView = binding.passwordEdtTxt
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(userNameStr)) {
            binding.userNameTV.error = getString(R.string.error_field_required)
            focusView = binding.userNameTV
            cancel = true
        } else if (!isEmailValid(userNameStr)) {
            binding.userNameTV.error = getString(R.string.error_invalid_email)
            focusView = binding.userNameTV
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {


            /////Check whether user has internet connection or not
            if (NetworkUtil.getInstance(this@LoginActivity).isOnline) {

//                var uiHelper = OGoUiHelper()
//                if(!uiHelper.isPlayServicesAvailable(this@LoginActivity))
//                {
//                    Toast.makeText(this@LoginActivity, getString(R.string.google_play_services_error), Toast.LENGTH_SHORT).show()
//                    showGPlayServiceAlertToUser()
//
//
//                } else
                if (true) {

                    ////Check for location permission
                    /////Now check GPS is on or not , if not ask for GPS to on it

                    if (GPSUtility.askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, GPSUtility.LOCATION, this@LoginActivity)) {

                        if (GPSUtility.isLocationEnabled(this@LoginActivity)) {
                            //////Here Location permission is granted and GPS is ON , ok to proceed


                            ////////////////////////////////
                            ///loader animation
                            val loader = CircularSticksLoader(this@LoginActivity, 80, 90f, 40f,
                                ContextCompat.getColor(this, R.color.colorLoader),
                                ContextCompat.getColor(this, R.color.colorYellow),
                                ContextCompat.getColor(this, R.color.color_them))
                                .apply {

                                }
                            binding.loadingLayout.addView(loader)
                            ////////////////////////////////


                            DriverUtilities.hideKeyboard(binding.passwordEdtTxt, this@LoginActivity)
                            // DriverUtilities.hideKeyboard(this@LoginActivity)

                            if (userNameStr.isEmpty()) {
                                val dialog = CourierAlertDialog(this@LoginActivity, "Login", getString(R.string.empty_user_name), getString(R.string.login_ok), null)
                                dialog.setOnActionListener(object : CourierAlertDialog.OnActionListener {
                                    override fun onOK() {
                                        dialog.dismiss()
                                    }

                                    override fun onCancel() {
                                        //NOT REQUIRED HERE
                                    }
                                })

                                dialog.show()
                                return

                            } else if (passwordStr.isEmpty()) {
                                val dialog = CourierAlertDialog(this@LoginActivity, "Login", getString(R.string.empty_password), getString(R.string.login_ok), null)
                                dialog.setOnActionListener(object : CourierAlertDialog.OnActionListener {
                                    override fun onOK() {
                                        dialog.dismiss()

                                    }
                                    override fun onCancel() {
                                        //NOT REQUIRED HERE
                                    }
                                })

                                dialog.show()
                                return
                            } else {

                                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->

                                    if (task.isSuccessful) {

                                        preferenceHelper!!.lastUsername = userNameStr
                                        preferenceHelper!!.lastPassword = passwordStr

//                                                LogUtils.error(LogUtils.TAG, " Installation ID:  token :${task.result?.token}")
                                        //Getting the token if everything is fine
                                        val token = task.result
                                        preferenceHelper!!.fcmToken = token
                                        /////Actual Call
                                        loginPost(userNameStr, passwordStr, preferenceHelper!!.fcmToken!!, BuildConfig.VERSION_NAME)

                                    } else {
//                                                LogUtils.error(LogUtils.TAG, "Unable to get Installation ID (token).")
                                        Toast.makeText(this, "Unable to get Installation ID (token).", Toast.LENGTH_SHORT).show()
                                        return@OnCompleteListener
                                    }


                                })


                            }


                        } else {
                            ////call setting to enable GPS
                            showGPSDisabledAlertToUser()


                        }


                    }

                }


            } else {
                LogUtils.error(LogUtils.TAG, "CheckConnection =>" + "\"########You are offline!!!!")
                // showNetworkError()
                NetworkUtil.getInstance(this@LoginActivity).showCustomNetworkError(this@LoginActivity)

            }


        }
    }

    private fun isEmailValid(email: String): Boolean {
        //TODO: Replace this with your own logic
        return email.contains("@")
    }

    private fun isPasswordValid(password: String): Boolean {

        return password.length > 4
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            binding.loginForm.visibility = if (show) View.GONE else View.VISIBLE
            binding.loginForm.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 0 else 1).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.loginForm.visibility = if (show) View.GONE else View.VISIBLE
                    }
                })

            binding.loginProgress.visibility = if (show) View.VISIBLE else View.GONE
            binding.loginProgress.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.loginProgress.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            binding.loginProgress.visibility = if (show) View.VISIBLE else View.GONE
            binding.loginForm.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<Cursor> {
        return CursorLoader(this,
            // Retrieve data rows for the device user's 'profile' contact.
            Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

            // Select only email addresses.
            ContactsContract.Contacts.Data.MIMETYPE + " = ?", arrayOf(ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE),

            // Show primary email addresses first. Note that there won't be
            // a primary email address if the user hasn't specified one.
            ContactsContract.Contacts.Data.IS_PRIMARY + " DESC")
    }

    override fun onLoadFinished(cursorLoader: Loader<Cursor>, cursor: Cursor) {
        val emails = ArrayList<String>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS))
            cursor.moveToNext()
        }

        addEmailsToAutoComplete(emails)
    }

    override fun onLoaderReset(cursorLoader: Loader<Cursor>) {

    }

    private fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        val adapter = ArrayAdapter(this@LoginActivity,
            android.R.layout.simple_dropdown_item_1line, emailAddressCollection)

        binding.userNameTV?.setAdapter(adapter!!)
    }

    object ProfileQuery {
        val PROJECTION = arrayOf(
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Email.IS_PRIMARY)
        val ADDRESS = 0
        val IS_PRIMARY = 1
    }


    companion object {

        /**
         * Id to identity READ_CONTACTS permission request.
         */
        private val REQUEST_READ_CONTACTS = 0

        /**
         * A dummy authentication store containing known user names and passwords.
         * TODO: remove after connecting to a real authentication system.
         */
        private val DUMMY_CREDENTIALS = arrayOf("foo@example.com:hello", "bar@example.com:world")
    }


    public override fun onResume() {
        super.onResume()
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        mpreferenceHelper = PreferenceHelper(this@LoginActivity)

        if (mpreferenceHelper!!.loggedInUser != null) {


            val i = Intent(this@LoginActivity, MainActivity::class.java)
            //i.putExtra("DRIVER_NAME", mpreferenceHelper!!.lastUsername)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            finish()

        } else {

            //// check Version from firebase config file and proceed accordingly
            CheckUpdateFromFireBaseConfig.checkAndShowUpdateAvailableAlert(this@LoginActivity)


        }


    }

    public override fun onPause() {

        super.onPause()
        ///these below two line will disable landscape mode ,check onResume() also
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

    }


    /*fun showNetworkError() {

        val dialog = CourierAlertDialog(this@LoginActivity, getString(R.string.network_error_title), getString(R.string.network_error_message), getString(R.string.network_error_wifi_setting), getString(R.string.network_error_cancel))
        dialog.setOnActionListener(object : CourierAlertDialog.OnActionListener {
            override fun onOK() {
                startActivity(Intent(WifiManager.ACTION_PICK_WIFI_NETWORK))
                dialog.dismiss()
                //finish();
            }

            override fun onCancel() {
                finish()
            }
        })

        dialog.show()
        // LogUtils.error(LogUtils.TAG,"CheckConnection =>"+"############################You are not online!!!!")

    }
*/


    /*
    * This is method which  will open setting to enable GPS ,
    * This will not automatically enable after clicking on 'Enable GPS' , user has to click manually
    * to enable manually please check method GPSUtility.askForGPS(...)
    *
    *
    * */
    private fun showGPSDisabledAlertToUser() {
        LogUtils.error(LogUtils.TAG, "showGPSDisabledAlertToUser called!")

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
            .setCancelable(false)
            .setPositiveButton("Enable GPS"
            ) { dialog, id ->
                val callGPSSettingIntent = Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(callGPSSettingIntent)
            }
        alertDialogBuilder.setNegativeButton("Cancel",
            object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, id: Int) {
                    dialog.cancel()
                }
            })
        val alert = alertDialogBuilder.create()
        alert.show()


    }


    private fun showGPlayServiceAlertToUser() {
        LogUtils.error(LogUtils.TAG, getString(R.string.google_play_services_error))

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage(getString(R.string.google_play_services_error))
            .setCancelable(false)

        alertDialogBuilder.setNegativeButton("OK",
            object : DialogInterface.OnClickListener {

                override fun onClick(dialog: DialogInterface, id: Int) {
                    dialog.cancel()
                }
            })


        val alert = alertDialogBuilder.create()
        alert.show()


    }


    /*
    * web service call via retrofit
    * */


    private fun loginPost(userNameStr: String, passwordStr: String, fcmToken: String, versionName: String) {
        apiPostService = ApiPostUtils.apiPostService

        apiPostService!!.postLogin(userNameStr, passwordStr, fcmToken, versionName)
            .enqueue(object : Callback<LoginResp> {
                override fun onResponse(call: Call<LoginResp>, response: Response<LoginResp>) {

                    // LogUtils.error(LogUtils.TAG, "response.raw().toString() =>" + response.raw().toString())

                    if (response.isSuccessful) {

                        showLoginResponse(response.body()!!.toString())

                        // login_activity_gif_imgView.setBackgroundResource(0)
                        ///////remove here animation
                        binding.loadingLayout.removeAllViewsInLayout()

                        if (response.body()!!.status.toString().equals("true", true)) {
//                                LogUtils.error(LogUtils.TAG, "postLogin response.body()!!.message.toString() =>" + response.body()!!.message.toString())
                            // showProgress(false)

                            ////before launching main activity set below things
                            // set login object in shared preferences
                            // set driver ID in shared preferences
                            ///update status on server or fire base

                            PreferenceHelper(this@LoginActivity).loggedInUser = response.body()!!.data!!
                            /// here feed data in real time fire base data model and use everywhere
                            feedLoggedInDetails(response.body()!!.data!!)


                        } else {


                            Toast.makeText(this@LoginActivity, response.body()!!.message, Toast.LENGTH_LONG).show();

                        }


                    }
                }

                override fun onFailure(call: Call<LoginResp>, t: Throwable) {

//                        LogUtils.error("TAG", "Unable to submit postLogin to API.")
                    Toast.makeText(this@LoginActivity, " Unable to submit postLogin to API.!", Toast.LENGTH_LONG).show();

                    // showProgress(false)
                    //login_activity_gif_imgView.setBackgroundResource(0)
                    ///////remove here animation
                    binding.loadingLayout.removeAllViewsInLayout()

                }
            })


    }

    fun showLoginResponse(response: String) {
        LogUtils.error("TAG>>>>RESPONSE>>>>", response)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


    //this function is design to take only necessary fields from logged in response and then push in to real time fire base database
    fun feedLoggedInDetails(loginRespData: LoginRespData) {

        var driverImage: List<String>? = null
        if (loginRespData!!.driverImage != null && loginRespData!!.driverImage!!.length > 0) {
            driverImage = loginRespData!!.driverImage!!.split(ApiConstant.DRIVER_IMAGE_BASE_URL)
            LogUtils.error(LogUtils.TAG, "driverImage.size:"+driverImage!!.size)
            LogUtils.error(LogUtils.TAG, "driverImage[0]:"+driverImage[0])
            LogUtils.error(LogUtils.TAG, "driverImage.get(1):" + driverImage.get(1))
        }


        //// here take all necessary fields and then save in sharePreferences
        var realTimeLoginDataModel = RealTimeFireBaseLoginDataModel()
        realTimeLoginDataModel.appStatus = RealTimeFireBaseAppStatus()
        realTimeLoginDataModel.onlineStatus = RealTimeFireBaseOnlineStatus()
        realTimeLoginDataModel.orderStatus = RealTimeFireBaseOrderStatus()
        realTimeLoginDataModel!!.location = RealTimeFireBaseLocation()
        realTimeLoginDataModel!!.driverDetails = RealTimeFireBaseDriverDetails()

        realTimeLoginDataModel.appStatus!!.status = loginRespData!!.status
        realTimeLoginDataModel!!.onlineStatus!!.onlineDatetime = loginRespData!!.onlineDatetime
        realTimeLoginDataModel!!.onlineStatus!!.onlineStatus = loginRespData!!.onlineStatus

        realTimeLoginDataModel!!.orderStatus!!.orderStatus = loginRespData!!.status
        realTimeLoginDataModel!!.orderStatus!!.busyStatus = loginRespData!!.busyStatus
        realTimeLoginDataModel!!.orderStatus!!.e5 = loginRespData!!.e5


        realTimeLoginDataModel!!.location!!.loctLatt = loginRespData!!.loctLatt
        realTimeLoginDataModel!!.location!!.loctLong = loginRespData!!.loctLong
        realTimeLoginDataModel!!.location!!.lastLocationDandT = loginRespData!!.lastLocationDandT
        realTimeLoginDataModel!!.location!!.previousLatitude = loginRespData!!.previous_latitude
        realTimeLoginDataModel!!.location!!.previousLongitude = loginRespData!!.previous_longitude
        realTimeLoginDataModel!!.location!!.speedInKmph = loginRespData!!.speed_in_kmph


        realTimeLoginDataModel!!.driverDetails!!.appVersion = loginRespData!!.app_version
        realTimeLoginDataModel!!.driverDetails!!.cid = loginRespData!!.cid
        realTimeLoginDataModel!!.driverDetails!!.cidType = loginRespData!!.cidType

        if (driverImage?.get(1)!! != null && driverImage?.get(1)!!.length > 0)
            realTimeLoginDataModel!!.driverDetails!!.driverImage = driverImage.get(1)

        /// old way full URL
        // realTimeLoginDataModel!!.driverDetails!!.driverImage= loginRespData!!.driverImage

        realTimeLoginDataModel!!.driverDetails!!.e1 = loginRespData!!.e1

        realTimeLoginDataModel!!.driverDetails!!.emailId = loginRespData!!.emailId
        realTimeLoginDataModel!!.driverDetails!!.id = loginRespData!!.id
        realTimeLoginDataModel!!.driverDetails!!.name = loginRespData!!.name
        realTimeLoginDataModel!!.driverDetails!!.phone = loginRespData!!.phone
        realTimeLoginDataModel!!.driverDetails!!.vehicleType = loginRespData!!.vehicleType
        realTimeLoginDataModel!!.driverDetails!!.vehicleNo = loginRespData!!.vehicle_no

        PreferenceHelper(this@LoginActivity).realTimeFireBaseLoginDataModel = realTimeLoginDataModel!!


        //  ////LOGOUT MODEL saved in share preferences
        var realTimeLogoutDataModel = RealTimeFireBaseLogoutDataModel()
        realTimeLogoutDataModel!!.appSt = RealTimeFireBaseLogoutAppStatus()
        realTimeLogoutDataModel!!.onSt = RealTimeFireBaseLogoutOnlineStatus()
        realTimeLogoutDataModel!!.lo = RealTimeFireBaseLogoutLocation()
        realTimeLogoutDataModel!!.drD = RealTimeFireBaseLogoutDriverDetails()

        realTimeLogoutDataModel!!.appSt!!.status = OGoConstant.LOGOUT
        realTimeLogoutDataModel!!.onSt!!.onlineStatus = OGoConstant.OFFLINE
        realTimeLogoutDataModel!!.drD!!.emailId = loginRespData!!.emailId
        realTimeLogoutDataModel!!.drD!!.id = loginRespData!!.id

        PreferenceHelper(this@LoginActivity).realTimeFireBaseLogOutDataModel = realTimeLogoutDataModel!!

        ////Update here driver details once driver is logged in
        UpdateRealTimeDriverDetails.updateDriverDetails(this@LoginActivity)
        //PreferenceHelper(this@LoginActivity).driverId = response.body()!!.data!!.id

        ////update app status here to fire base  as LOGIN

        val i = Intent(this@LoginActivity, MainActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        //i.putExtra("DRIVER_NAME", response.body()!!.data.name)
        startActivity(i)
        // close this activity
        finish()


    }


}
