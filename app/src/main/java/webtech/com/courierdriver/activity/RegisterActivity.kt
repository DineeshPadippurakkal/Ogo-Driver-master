package webtech.com.courierdriver.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.hbb20.CountryCodePicker
import webtech.com.courierdriver.R
import webtech.com.courierdriver.databinding.ActivityRegisterBinding
import webtech.com.courierdriver.databinding.ActivitySplashBinding
import webtech.com.courierdriver.utilities.Language
import webtech.com.courierdriver.utilities.NetworkUtil
import webtech.com.courierdriver.utilities.PreferenceHelper

/*
Created by ​
Hannure Abdulrahim


on 4/22/2019.
 */
class RegisterActivity : MasterAppCombatActivity() {


    private var preferenceHelper: PreferenceHelper? = null
    var EMAIL = "info@ogo.delivery"
    private lateinit var binding: ActivityRegisterBinding

    //var SUBJECT="Subject : testing request for driver registration "
    //var BODY="Respected sir, This requesting is testing Please ignore this message. and dont create any account."


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_register)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceHelper = PreferenceHelper(this@RegisterActivity)


        if (preferenceHelper!!.language == Language.ARABIC) {
            binding.ccpCountry.changeDefaultLanguage(CountryCodePicker.Language.ARABIC)

        } else {
            binding.ccpCountry.changeDefaultLanguage(CountryCodePicker.Language.ENGLISH)
        }


        binding.tvRegisterLayout.setOnClickListener {

            /////Check whether user is online or not
            if (NetworkUtil.getInstance(this@RegisterActivity).isOnline) {

                attemptRegister()


            } else {
//                LogUtils.error(LogUtils.TAG, "CheckConnection =>" + "\"########You are offline!!!!")

                // showNetworkError()
                NetworkUtil.getInstance(this@RegisterActivity)
                    .showCustomNetworkError(this@RegisterActivity)

            }


        }


    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()

    }

    public override fun onResume() {
        super.onResume()
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }


    public override fun onPause() {

        super.onPause()
        ///these below two line will disable landscape mode ,check onResume() also
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

    }


    /**
     * Attempts to  register the account specified by the register form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual register request attempt is made.
     */
    private fun attemptRegister() {

        // Reset errors.
        binding.edtTxtUserName.error = null
        binding.edtTxtEmail.error = null

        binding.edtTxtAge.error = null
        binding.edtTxtPhone.error = null


        // Store values at the time of the regsiter attempt.
        val userNameStr = binding.edtTxtUserName.text.toString()
        val emailStr = binding.edtTxtEmail.text.toString()
        val ageStr = binding.edtTxtAge.text.toString()
        val phoneStr = binding.edtTxtPhone.text.toString()


        var cancel = false
        var focusView: View? = null



        if (TextUtils.isEmpty(userNameStr)) {
            binding.edtTxtUserName.error = getString(R.string.error_field_required)
            focusView = binding.edtTxtUserName
            cancel = true
        } else if (TextUtils.isEmpty(emailStr)) {
            binding.edtTxtEmail.error = getString(R.string.error_field_required)
            focusView = binding.edtTxtEmail
            cancel = true
        } else if (!isEmailValid(emailStr)) {  // Check for a valid email address.
            binding.edtTxtEmail.error = getString(R.string.error_invalid_email)
            focusView = binding.edtTxtEmail
            cancel = true
        } else if (TextUtils.isEmpty(ageStr)) {
            binding.edtTxtAge.error = getString(R.string.error_field_required)
            focusView = binding.edtTxtAge
            cancel = true
        } else if (!isAgeValid(ageStr)) {
            binding.edtTxtAge.error = getString(R.string.age_error)
            cancel = true

        } else if (!TextUtils.isEmpty(phoneStr) && !isPhoneValid(phoneStr)) {
            binding.edtTxtPhone.error = getString(R.string.phone_error)
            focusView = binding.edtTxtPhone
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {

            var SUBJECT = getString(R.string.subject)
            var BODY = getString(R.string.body)




            binding.ccpCountry.registerCarrierNumberEditText(binding.edtTxtPhone)
            //get formatted number i.e "+1 (469) 664-1766"
            //Log.e("Test",ccp.getFormattedFullNumber());
//            LogUtils.error(LogUtils.TAG, "binding.ccpCountry.getFormattedFullNumber():" +binding.ccpCountry.getFormattedFullNumber())
            /// with +
//            LogUtils.error(LogUtils.TAG,"binding.ccpCountry.getFullNumberWithPlus():"+ binding.ccpCountry.getFullNumberWithPlus())

            /// get country
//            LogUtils.error(LogUtils.TAG,"binding.ccpCountry.selectedCountryEnglishName():"+ binding.ccpCountry.selectedCountryEnglishName)
//            LogUtils.error(LogUtils.TAG,"binding.ccpCountry.selectedCountryName():"+ binding.ccpCountry.selectedCountryName)

            //var phoneWithCountryCode= binding.ccpCountry.getFormattedFullNumber()
            //var phoneWithouthCountryCode= binding.edtTxtPhone.text.toString()

            var selectedCountryName: String
            var phoneNum: String

            if (preferenceHelper!!.language == Language.ENGLISH) {
                selectedCountryName = binding.ccpCountry.selectedCountryEnglishName.toString()
                /// phone num with country code
                phoneNum = binding.ccpCountry.getFormattedFullNumber()
            } else {
                selectedCountryName = binding.ccpCountry.selectedCountryName.toString()
                /// phone num without country code ( issue in arabic hence without county code )
                phoneNum = binding.edtTxtPhone.text.toString()
            }


            var denDetails = "\n\n" +

                    getString(R.string.mail_cname) + userNameStr + "\n" +
                    getString(R.string.mail_email) + emailStr + "\n" +
                    getString(R.string.mail_age) + ageStr + "\n" +
                    getString(R.string.mail_country) + selectedCountryName + "\n" +
                    getString(R.string.mail_phone) + phoneNum

            val intent = Intent(Intent.ACTION_SEND)
            val recipients = arrayOf<String>(EMAIL)
            intent.putExtra(Intent.EXTRA_EMAIL, recipients)
            intent.putExtra(Intent.EXTRA_SUBJECT, SUBJECT)
            intent.putExtra(Intent.EXTRA_TEXT, BODY + denDetails)
            // intent.putExtra(Intent.EXTRA_CC, "mailcc@gmail.com")
            intent.setType("text/html")
            intent.setPackage("com.google.android.gm")
            startActivity(Intent.createChooser(intent, "Send mail"))


        }


    }

    private fun isEmailValid(email: String): Boolean {

        return email.contains("@")
    }

    private fun isAgeValid(age: String): Boolean {

        return age.toInt() > 18 && age.toInt() < 55
    }


    /// considring phone number only for kuwait now having only 8 digits without +965 pr 965
    private fun isPhoneValid(phone: String): Boolean {

//        LogUtils.error(LogUtils.TAG,"phone.length:"+ phone.length)

        return phone.length == 8
    }


}