package webtech.com.courierdriver.fragments

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper

import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.hsalf.smilerating.BaseRating
import com.hsalf.smilerating.SmileRating

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import webtech.com.courierdriver.R
import webtech.com.courierdriver.communication.ApiPostService
import webtech.com.courierdriver.communication.ApiPostUtils
import webtech.com.courierdriver.communication.response.ReceiveCurrentOrderResponseData
import webtech.com.courierdriver.communication.response.UpdateOderStatusResp
import webtech.com.courierdriver.communication.response.UpdateOrderRatingResp
import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.databinding.FragmentOrdersRatingBinding
import webtech.com.courierdriver.databinding.FragmentScannedOrderBinding
import webtech.com.courierdriver.events.ListOrdersEvent
import webtech.com.courierdriver.events.LocationChangedEvent
import webtech.com.courierdriver.firebase.UpdateRealTimeDriverOrderStatus
import webtech.com.courierdriver.utilities.LogUtils
import webtech.com.courierdriver.utilities.NetworkUtil
import webtech.com.courierdriver.utilities.PreferenceHelper
import java.io.IOException

/*
Created by ​
Hannure Abdulrahim


on 11/12/2018.
 */


class OrdersRatingFragment : Fragment() {

    var mActivity: Activity? = null
    private var preferenceHelper: PreferenceHelper? = null
    var apiPostService: ApiPostService? = null
    private var orderEntityJson: String? = null
    private var orderEntity: ReceiveCurrentOrderResponseData? = null

    private var myLat = 0.0
    private var myLon = 0.0

    private lateinit var binding: FragmentOrdersRatingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            orderEntityJson = requireArguments().getString(ORDER_ENTITY_JSON)
        }

        setHasOptionsMenu(true)
        preferenceHelper = PreferenceHelper(mActivity!!)

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_orders_rating, container, false)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding=FragmentOrdersRatingBinding.bind(view)
        orderEntity = Gson().fromJson(orderEntityJson, ReceiveCurrentOrderResponseData::class.java)



        binding.smileRating!!.setOnSmileySelectionListener(object : SmileRating.OnSmileySelectionListener {
            override fun onSmileySelected(@BaseRating.Smiley smiley: Int, reselected: Boolean) {
                // reselected is false when user selects different smiley that previously selected one
                // true when the same smiley is selected.
                // Except if it first time, then the value will be false.

                when (smiley) {

                    SmileRating.BAD -> {
                        //LogUtils.error(LogUtils.TAG, "Bad")
                        // smile_rating.setSelectedSmile(BaseRating.BAD, true)
                    }
                    SmileRating.GOOD -> {
                        //LogUtils.error(LogUtils.TAG ,"Good")
                        //smile_rating.setSelectedSmile(BaseRating.GOOD, true)

                    }
                    SmileRating.GREAT -> {
                        //LogUtils.error(LogUtils.TAG, "Great")
                        //smile_rating.setSelectedSmile(BaseRating.GREAT, true)

                    }
                    SmileRating.OKAY -> {
                        //  LogUtils.error(LogUtils.TAG ,"Okay")
                        // smile_rating.setSelectedSmile(BaseRating.OKAY, true)

                    }
                    SmileRating.TERRIBLE -> {

                        //LogUtils.error(LogUtils.TAG ,"Terrible")
                        //smile_rating.setSelectedSmile(BaseRating.TERRIBLE, true)
                    }
                }
            }
        })


        /////finish order here and call rating web service here
        binding.tvSubmitRatingLayout.setOnClickListener {

            finishOrderWithRating()


        }


        ////////finish order and dont call rating web service here
        binding.skipLayout.setOnClickListener {

            finishOrderWithoutRating()


        }


    }


    override fun onResume() {
        super.onResume()
        mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        (mActivity as AppCompatActivity).getSupportActionBar()!!.setTitle(R.string.order_rating)

    }

    override fun onPause() {
        super.onPause()
        mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        /// for new version > M
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Code here
            EventBus.getDefault().register(this)
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
            EventBus.getDefault().register(this)
            mActivity = activity
        }
    }

    override fun onDetach() {
        super.onDetach()
        (mActivity as AppCompatActivity).getSupportActionBar()!!.setTitle("")
        EventBus.getDefault().unregister(this)


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


    @Subscribe
    fun onLocationUpdated(event: LocationChangedEvent) {
        myLat = event.lat
        myLon = event.lng

        // Log.e("Test"," myLat in onLocationUpdated() OrderReceiverMapFragment.java =>  "+ myLat );
        // Log.e("Test"," myLon in onLocationUpdated() OrderReceiverMapFragment.java =>  "+ myLon );
    }


    companion object {

        private val ORDER_ENTITY_JSON = "order_entity_json"


        // Required empty public constructor

        fun newInstance(orderEntityJson: String): OrdersRatingFragment {
            val fragment = OrdersRatingFragment()
            val args = Bundle()
            args.putString(ORDER_ENTITY_JSON, orderEntityJson)
            fragment.arguments = args
            return fragment
        }
    }


    ///////Web Service call to update order status
    fun updateOrderStatusPost(userNameStr: String, orderId: String, companyId: String, orderStatus: String) {
        // LogUtils.error(LogUtils.TAG, "updateOrderStatusPost userNameStr=>" + userNameStr)
        // LogUtils.error(LogUtils.TAG, "updateOrderStatusPost orderId=>" + orderId)
        // LogUtils.error(LogUtils.TAG, "updateOrderStatusPost companyId=>" + companyId)
        // LogUtils.error(LogUtils.TAG, "updateOrderStatusPost orderStatus=>" + orderStatus)


        apiPostService = ApiPostUtils.apiPostService
        apiPostService!!.postUpdateOrderStatus(userNameStr, orderId, companyId, orderStatus).enqueue(object : Callback<UpdateOderStatusResp> {

            override fun onResponse(call: Call<UpdateOderStatusResp>, response: Response<UpdateOderStatusResp>) {

                LogUtils.error(LogUtils.TAG, "response.raw().toString() =>" + response.raw().toString())

                if (response.isSuccessful) {

                    showUpdateOderStatusResponse(response.body()!!.toString())

                    if (response.body()!!.status.toString().equals("true", true)) {
                        LogUtils.error(LogUtils.TAG, "postUpdateOrderStatus response.body()!!.message.toString() =>" + response.body()!!.message.toString())
                        //Toast.makeText(mActivity, response.body()!!.message, Toast.LENGTH_LONG).show()


                    } else {

                        Toast.makeText(mActivity, response.body()!!.message, Toast.LENGTH_LONG).show()

                    }


                }
            }

            override fun onFailure(call: Call<UpdateOderStatusResp>, t: Throwable) {

                if (t is IOException) {
                    Toast.makeText(mActivity, "this is an actual network failure :( inform the user and possibly retry", Toast.LENGTH_SHORT).show();
                    // logging probably not necessary
                } else {
                    Toast.makeText(mActivity, "conversion issue! big problems :(", Toast.LENGTH_SHORT).show();
                    // todo log to some central bug tracking service
                    Toast.makeText(mActivity, " Unable to submit postUpdateOrderStatus to API.!", Toast.LENGTH_LONG).show();
                }

                LogUtils.error("TAG", "Unable to submit postUpdateOrderStatus to API." + t.printStackTrace())
                LogUtils.error("TAG", "Unable to submit postUpdateOrderStatus to API.")

                // showProgress(false)

            }
        })


    }

    fun showUpdateOderStatusResponse(response: String) {
        LogUtils.error("TAG>>>>RESPONSE>>>>", response)
    }


    ///////Web Service call to update rating

    fun updateOrderRatingPost(orderId: String, userNameStr: String, typesOfDriver: String, rating: Int, reviewMessage: String) {
        // LogUtils.error(LogUtils.TAG, "updateOrderRatingPost userNameStr=>" + userNameStr)
        // LogUtils.error(LogUtils.TAG, "updateOrderRatingPost orderId=>" + orderId)
        // LogUtils.error(LogUtils.TAG, "updateOrderRatingPost companyId=>" + companyId)
        // LogUtils.error(LogUtils.TAG, "updateOrderRatingPost orderStatus=>" + orderStatus)


        apiPostService = ApiPostUtils.apiPostService
        apiPostService!!.postUpdateOrderRating(orderId, userNameStr, typesOfDriver, rating, reviewMessage).enqueue(object : Callback<UpdateOrderRatingResp> {

            override fun onResponse(call: Call<UpdateOrderRatingResp>, response: Response<UpdateOrderRatingResp>) {

                LogUtils.error(LogUtils.TAG, "response.raw().toString() =>" + response.raw().toString())

                if (response.isSuccessful) {

                    showUpdateOderRatingResponse(response.body()!!.toString())

                    if (response.body()!!.status.toString().equals("true", true)) {
                        LogUtils.error(LogUtils.TAG, "postUpdateOrderRating response.body()!!.message.toString() =>" + response.body()!!.message.toString())
                        //Toast.makeText(mActivity, response.body()!!.message, Toast.LENGTH_LONG).show()


                    } else {

                        Toast.makeText(mActivity, response.body()!!.message, Toast.LENGTH_LONG).show()

                    }


                }
            }

            override fun onFailure(call: Call<UpdateOrderRatingResp>, t: Throwable) {

                if (t is IOException) {
                    Toast.makeText(mActivity, "this is an actual network failure :( inform the user and possibly retry", Toast.LENGTH_SHORT).show();
                    // logging probably not necessary
                } else {
                    Toast.makeText(mActivity, "conversion issue! big problems :(", Toast.LENGTH_SHORT).show();
                    // todo log to some central bug tracking service
                    Toast.makeText(mActivity, " Unable to submit postUpdateOrderRating to API.!", Toast.LENGTH_LONG).show();
                }

                LogUtils.error("TAG", "Unable to submit postUpdateOrderRating to API." + t.printStackTrace())
                LogUtils.error("TAG", "Unable to submit postUpdateOrderRating to API.")

                // showProgress(false)

            }
        })


    }

    fun showUpdateOderRatingResponse(response: String) {
        LogUtils.error("TAG>>>>RESPONSE>>>>", response)
    }


    fun finishOrderWithoutRating() {


        if (NetworkUtil.getInstance(mActivity!!).isOnline)
        {
            preferenceHelper!!.orderStatus = OGoConstant.COMPLETED
            UpdateRealTimeDriverOrderStatus.updateRealTimeDriverOrderStatus(mActivity!!, OGoConstant.COMPLETED)

            //////finish order here directly without taking ratings
            updateOrderStatusPost(preferenceHelper!!.loggedInUser!!.emailId!!, orderEntity!!.orderId!!, preferenceHelper!!.loggedInUser!!.cid!!, OGoConstant.COMPLETED)
            Toast.makeText(mActivity, "order finished successfully ", Toast.LENGTH_SHORT).show()

            /////first make last order as null in shared preference
            //preferenceHelper!!.lastOrderResponseData=null

            Looper.myLooper()?.let {
                Handler(it).postDelayed({
                    // YOUR CODE after duration finished

                    EventBus.getDefault().post(ListOrdersEvent())
                },OGoConstant.DELAY)
            }



        }else
        {
            NetworkUtil.getInstance(mActivity!!).showCustomNetworkError(mActivity!!)

        }




    }


    fun finishOrderWithRating() {


        if (NetworkUtil.getInstance(mActivity!!).isOnline)
        {

            /////submit rating here by calling web service

            preferenceHelper!!.orderStatus = OGoConstant.COMPLETED
            UpdateRealTimeDriverOrderStatus.updateRealTimeDriverOrderStatus(mActivity!!, OGoConstant.COMPLETED)




            ////////finish order
            updateOrderStatusPost(preferenceHelper!!.loggedInUser!!.emailId!!, orderEntity!!.orderId!!, preferenceHelper!!.loggedInUser!!.cid!!, OGoConstant.COMPLETED)
            // Toast.makeText(mActivity, "order finished successfully ", Toast.LENGTH_SHORT).show()




            //////take comments here
            var driverComment = binding.etComment!!.text.toString()
            if (driverComment.length < 1)
                driverComment = "NA" //// not applicable or not written any comments


            // level is from 1 to 5
            val ratingLevel = binding.smileRating.getRating()
            // Will return 0 if NONE selected

            when (ratingLevel) {


                SmileRating.BAD -> {
                    LogUtils.error(LogUtils.TAG, "Bad")
                    ////// submit rating
                    updateOrderRatingPost(orderEntity!!.orderId!!, preferenceHelper!!.loggedInUser!!.emailId!!, OGoConstant.TYPES_OF_DRIVER, SmileRating.BAD, driverComment!!)
                    Toast.makeText(mActivity, "updated rating and order finished successfully!", Toast.LENGTH_SHORT).show()

                }
                SmileRating.GOOD -> {
                    LogUtils.error(LogUtils.TAG, "Good")
                    updateOrderRatingPost(orderEntity!!.orderId!!, preferenceHelper!!.loggedInUser!!.emailId!!, OGoConstant.TYPES_OF_DRIVER, SmileRating.GOOD, driverComment)
                    Toast.makeText(mActivity, "updated rating and order finished successfully!", Toast.LENGTH_SHORT).show()

                }
                SmileRating.GREAT -> {
                    LogUtils.error(LogUtils.TAG, "Great")
                    updateOrderRatingPost(orderEntity!!.orderId!!, preferenceHelper!!.loggedInUser!!.emailId!!, OGoConstant.TYPES_OF_DRIVER, SmileRating.GREAT, driverComment)
                    Toast.makeText(mActivity, "updated rating and order finished successfully!", Toast.LENGTH_SHORT).show()

                }
                SmileRating.OKAY -> {
                    LogUtils.error(LogUtils.TAG, "Okay")
                    updateOrderRatingPost(orderEntity!!.orderId!!, preferenceHelper!!.loggedInUser!!.emailId!!, OGoConstant.TYPES_OF_DRIVER, SmileRating.OKAY, driverComment)
                    Toast.makeText(mActivity, "updated rating and order finished successfully!", Toast.LENGTH_SHORT).show()

                }
                SmileRating.TERRIBLE -> {

                    LogUtils.error(LogUtils.TAG, "Terrible")
                    updateOrderRatingPost(orderEntity!!.orderId!!, preferenceHelper!!.loggedInUser!!.emailId!!, OGoConstant.TYPES_OF_DRIVER, SmileRating.TERRIBLE, driverComment)
                    Toast.makeText(mActivity, "updated rating and order finished successfully!", Toast.LENGTH_SHORT).show()
                }
            }


            /////first make last order as null in shared preference
            //preferenceHelper!!.lastOrderResponseData=null

            Handler().postDelayed({

                // YOUR CODE after duration finished

                EventBus.getDefault().post(ListOrdersEvent())


            }, 1 * 1000)////one sec duration


        }else
        {
            NetworkUtil.getInstance(mActivity!!).showCustomNetworkError(mActivity!!)

        }




    }


}