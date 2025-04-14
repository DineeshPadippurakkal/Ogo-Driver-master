package webtech.com.courierdriver.fragments

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle

import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import webtech.com.courierdriver.R
import webtech.com.courierdriver.databinding.FragmentWelcomeBinding
import webtech.com.courierdriver.utilities.PreferenceHelper

/*
Created by ​
Hannure Abdulrahim


on 6/25/2019.
 */
class WelcomeFragment : Fragment() {

    var mActivity: Activity? = null
    private var preferenceHelper: PreferenceHelper? = null
    private var language: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)


    }

    private lateinit var binding: FragmentWelcomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_welcome, container, false)



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentWelcomeBinding.bind(view)
        preferenceHelper = PreferenceHelper(mActivity!!)
        language = preferenceHelper!!.language


        if (preferenceHelper!!.loggedInUser != null) {

//            Glide.with(mActivity!!)
//                    .load(preferenceHelper!!.loggedInUser!!.driverImage)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)///don't cache
//                    //.placeholder(R.drawable.ic_profile)
//                    .error(R.mipmap.ic_profile_test)///if url exist but images not available on server
//                    .into(iv_driver_welcome)


            binding.tvWelcomeUsername.text = preferenceHelper!!.loggedInUser!!.name
            binding.tvWelcome.text = getString(R.string.welcome_msg)


        }


    }


    /////////////////////////////////////S
    /*//Error If i rotate screen from portrait to landscape then it was getting crash hence restricted to portrait mode
     below two method will work*/

    override fun onResume() {
        super.onResume()
        mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        (mActivity as AppCompatActivity).getSupportActionBar()!!
            .setTitle(getString(R.string.welcome_title))


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
        (mActivity as AppCompatActivity).getSupportActionBar()!!
            .setTitle(getString(R.string.welcome_title))


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


        fun newInstance(): WelcomeFragment {
            return WelcomeFragment()
        }
    }


}
