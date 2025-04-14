package webtech.com.courierdriver.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import webtech.com.courierdriver.R
import webtech.com.courierdriver.communication.APIGetService
import webtech.com.courierdriver.communication.ApiGetUtils
import webtech.com.courierdriver.communication.response.ContactUsResp
import webtech.com.courierdriver.constants.ApiConstant
import webtech.com.courierdriver.databinding.FragmentContactUsBinding
import webtech.com.courierdriver.utilities.LogUtils
import webtech.com.courierdriver.utilities.PreferenceHelper

/*
Created by ​
Hannure Abdulrahim

on 7/23/2019.
 */


class ContactUsFragment : Fragment() {

    var mActivity: Activity? = null
    private var mAPIGetService: APIGetService? = null
    private var preferenceHelper: PreferenceHelper?=null
    private var language: Int = 0

    private lateinit var binding: FragmentContactUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_contact_us, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentContactUsBinding.bind(view)

        preferenceHelper = PreferenceHelper(mActivity!!)
        language = preferenceHelper!!.language

        ///// working only in test not for live
        getContactUsDetails(ApiConstant.CONTACT_US_EN)


        val startAnimation = AnimationUtils.loadAnimation(context, R.anim.blinking_animation_once)
        binding.tvPhoneNumberContactUs!!.startAnimation(startAnimation)


        binding.tvContactUsPhoneLandline!!.startAnimation(startAnimation)

        binding.tvContactUsPhoneLayout.setOnClickListener {
            val numberByTV = Uri.parse("tel:" + binding.tvPhoneNumberContactUs.text)
            val callIntentByTV = Intent(Intent.ACTION_DIAL, numberByTV)
            startActivity(callIntentByTV)

        }

        binding.tvContactUsPhoneLandlineLayout.setOnClickListener {
            val numberByTV = Uri.parse("tel:" + binding.tvContactUsPhoneLandline.text)
            val callIntentByTV = Intent(Intent.ACTION_DIAL, numberByTV)
            startActivity(callIntentByTV)

        }

    }

    /////////////////////////////////////S
    /*//Error If i rotate screen from portrait to landscape then it was getting crash hence restricted to portrait mode
     below two method will work*/

    override fun onResume() {
        super.onResume()
        mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        (mActivity as AppCompatActivity).getSupportActionBar()!!.setTitle(getString(R.string.contact_us_title))



    }

    override fun onPause() {
        super.onPause()
        mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

    }
    /////////////////////////////////////E


    override fun onAttach(context: Context) {
        super.onAttach(context)


        /// for new version > M
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Code here

            //first convert context into Activity ,since context is also Activity
            val activity = context as? Activity
            mActivity = activity





        }
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)


        /// for old  version < M
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Code here

            mActivity = activity

        }
    }

    override fun onDetach() {
        super.onDetach()
        (mActivity as AppCompatActivity).getSupportActionBar()!!.setTitle(getString(R.string.contact_us_title))


    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item!!.itemId) {

        }/* case R.id.directions:

                break;*/
        return super.onOptionsItemSelected(item)
    }


    companion object {


        fun newInstance(): ContactUsFragment {
            return ContactUsFragment()
        }
    }



    fun getContactUsDetails(contactUsUrl: String) {
        mAPIGetService = ApiGetUtils.apiGetService
        mAPIGetService!!.getContactUsDetails(contactUsUrl).enqueue(object : Callback<ContactUsResp> {


            override fun onResponse(call: Call<ContactUsResp>, response: Response<ContactUsResp>) {

                if (response.isSuccessful) {
                    binding.tvPhoneNumberContactUs.text = response.body()?.data?.phoneEn.toString()
                    binding.tvContactUsEmail.text = response.body()?.data?.emailEn.toString()
                    binding.tvContactUsWebsite.text = response.body()?.data?.websiteEn.toString()
                }
            }

            override fun onFailure(call: Call<ContactUsResp>, t: Throwable) {
                LogUtils.error(LogUtils.TAG, "server contact failed")


            }
        })
    }















}