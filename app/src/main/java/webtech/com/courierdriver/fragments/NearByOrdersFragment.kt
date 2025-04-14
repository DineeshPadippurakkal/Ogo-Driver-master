package webtech.com.courierdriver.fragments

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import net.vrgsoft.layoutmanager.RollingLayoutManager
import webtech.com.courierdriver.OrderSingletonQueue.NearByOrderSingletonQueue
import webtech.com.courierdriver.R
import webtech.com.courierdriver.adapter.NearByOrdersAdapter
import webtech.com.courierdriver.databinding.FragmentNearByOrdersBinding
import webtech.com.courierdriver.databinding.FragmentOrderBackToSenderMapBinding
import webtech.com.courierdriver.utilities.PreferenceHelper

/*
Created by ​
Hannure Abdulrahim


on 11/4/2019.
 */
class NearByOrdersFragment : Fragment() {

    private lateinit var binding: FragmentNearByOrdersBinding

    private var preferenceHelper: PreferenceHelper?=null
    private var language: Int = 0
    var mActivity: Activity? = null

    private var nearByOrdersAdapter: NearByOrdersAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_near_by_orders, container, false)
        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentNearByOrdersBinding.bind(view)

        preferenceHelper = PreferenceHelper(mActivity!!)
        language = preferenceHelper!!.language

        showNearByOrders()

        binding.tvtotalNearByOrders.text = getString(R.string.total_near_by_orders)+" " + NearByOrderSingletonQueue.instance.notificationNearByOrdersResponseDataList!!.size.toString()!!

        binding.refreshOrdersLayout.setOnClickListener {
            showNearByOrders()
        }








    }




    /////////////////////////////////////S
    /*//Error If i rotate screen from portrait to landscape then it was getting crash hence restricted to portrait mode
     below two method will work*/

    override fun onResume() {
        super.onResume()
        mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        (mActivity as AppCompatActivity).getSupportActionBar()!!.setTitle(getString(R.string.near_by_orders))



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
        (mActivity as AppCompatActivity).getSupportActionBar()!!.setTitle(getString(R.string.near_by_orders))


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


        fun newInstance(): NearByOrdersFragment {
            return NearByOrdersFragment()
        }
    }


    private fun showNearByOrders()
    {

        val rollingLayoutManager =  RollingLayoutManager(mActivity)
        binding.nearByOrdersRecyclerView!!.layoutManager = rollingLayoutManager

        nearByOrdersAdapter = NearByOrdersAdapter(NearByOrderSingletonQueue.instance.notificationNearByOrdersResponseDataList)
        binding.nearByOrdersRecyclerView!!.adapter = nearByOrdersAdapter
        nearByOrdersAdapter!!.notifyDataSetChanged()


    }






}
