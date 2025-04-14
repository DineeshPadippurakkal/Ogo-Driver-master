package webtech.com.courierdriver.fragments

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import net.vrgsoft.layoutmanager.RollingLayoutManager
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import webtech.com.courierdriver.R
import webtech.com.courierdriver.adapter.MyOrdersAdapter
import webtech.com.courierdriver.communication.ApiPostService
import webtech.com.courierdriver.communication.ApiPostUtils
import webtech.com.courierdriver.communication.response.AcceptedOrderResponse
import webtech.com.courierdriver.communication.response.OrderHistoryResponse
import webtech.com.courierdriver.communication.response.OrderHistoryResponseData
import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.databinding.FragmentScannedOrderBinding
import webtech.com.courierdriver.events.ScanningOrderEvent
import webtech.com.courierdriver.firebase.UpdateRealTimeDriverOrderStatus
import webtech.com.courierdriver.utilities.LogUtils
import webtech.com.courierdriver.utilities.PreferenceHelper
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 * Use the [ScannedOrderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScannedOrderFragment : Fragment() {
    var mActivity: Activity? = null
    private var scannedOrderID: String? = null
    var preferenceHelper: PreferenceHelper? = null

    private var ordersAdapter: MyOrdersAdapter? = null
    private val orderHistoryEntities = ArrayList<OrderHistoryResponseData>()
    private var apiPostService: ApiPostService? = null


    private lateinit var binding: FragmentScannedOrderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            scannedOrderID = requireArguments().getString(SCANNED_ORDER_ID)
        }


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scanned_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentScannedOrderBinding.bind(view)
        binding.ScanLayout!!.setOnClickListener {
            EventBus.getDefault().post(ScanningOrderEvent())
        }




        val rollingLayoutManager = RollingLayoutManager(mActivity)
           binding.orderRecyclerView!!.layoutManager = rollingLayoutManager!!
        ordersAdapter = MyOrdersAdapter(orderHistoryEntities)
           binding.orderRecyclerView!!.adapter = ordersAdapter

        scannedOrderID?.let{ scannedOrderIDStr->
            //// call here order accept API
            preferenceHelper = PreferenceHelper(mActivity!!)
            preferenceHelper?.let {preferenceHelper->
                preferenceHelper.loggedInUser?.let {loginResposeData->
                    acceptScannedOrderRequestPost(loginResposeData.emailId!!, scannedOrderID!!, loginResposeData.cid!!)

                }
            }



        }



    }


    override fun onResume() {
        super.onResume()
        mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        ///check on detach also removed title
        (mActivity as AppCompatActivity).supportActionBar!!.setTitle(R.string.scanned_orders)

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
            //first convert context into Activity ,since context is also Activity
            val activity = if (context is Activity) context else null
            mActivity = activity
            if (!EventBus.getDefault().isRegistered(mActivity))
                EventBus.getDefault().register(mActivity)
        }
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        /// for old  version < M
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Code here
            mActivity = activity
            if (!EventBus.getDefault().isRegistered(mActivity))
                EventBus.getDefault().register(mActivity)
        }
    }

    override fun onDetach() {
        super.onDetach()
        (mActivity as AppCompatActivity).getSupportActionBar()!!.setTitle("")



    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {

       // private val DRIVER_EMAIL_ID = "driver_email_id"
       private val SCANNED_ORDER_ID = "scanned_order-id"



        fun newInstance(scannedOrderID: String? = null ): ScannedOrderFragment {
            val fragment = ScannedOrderFragment()
            val args = Bundle()
            //args.putString(DRIVER_EMAIL_ID, driverId)
            args.putString(SCANNED_ORDER_ID, scannedOrderID)
            fragment.arguments = args
            return fragment
        }
    }



    /*
       *
       * Accept Order request
       *
       * */

    private fun acceptScannedOrderRequestPost(userNameStr: String, orderId: String, companyId: String) {
        LogUtils.error(LogUtils.TAG, "acceptScannedOrderRequestPost userNameStr=>" + userNameStr)
        LogUtils.error(LogUtils.TAG, "acceptScannedOrderRequestPost orderId=>" + orderId)
        LogUtils.error(LogUtils.TAG, "acceptScannedOrderRequestPost companyId=>" + companyId)
        apiPostService = ApiPostUtils.apiPostService

        //apiPostService!!.postAcceptOrdersRequest("abdul@gmail.com","OID-2021-THWN","2").enqueue(object : Callback<AcceptedOrderResponse> {
        apiPostService!!.postAcceptOrdersRequest(userNameStr, orderId, companyId).enqueue(object : Callback<AcceptedOrderResponse> {

            override fun onResponse(call: Call<AcceptedOrderResponse>, response: Response<AcceptedOrderResponse>) {

                LogUtils.error(LogUtils.TAG, "response.raw().toString() =>" + response.raw().toString())

                if (response.isSuccessful) {

                    showAcceptedOrderResponseonse(response.body()!!.toString())

                    if (response.body()!!.status.toString().equals("true", true)) {

                        LogUtils.error(LogUtils.TAG, "postAcceptOrdersRequest response.body()!!.message.toString() =>" + response.body()!!.message.toString())
                        LogUtils.error(LogUtils.TAG, "postAcceptOrdersRequest response.body()!!.data!!.orderId =>" + response.body()!!.data!!.orderId)

                        Toast.makeText(mActivity, getString(R.string.order_accepted), Toast.LENGTH_LONG).show()

                        /////// update current order status here in share preference
                        /// value is not coming from server hence taken as hard coded
                        preferenceHelper!!.orderStatus = OGoConstant.DRIVER_ACCEPTED

                        preferenceHelper!!.busyStatus=OGoConstant.BUSY

                        ///....below value is pushing in fir base database on request of backend developer
                        preferenceHelper!!.fireBase_e5 = response.body()!!.data!!.cid

                        LogUtils.error(LogUtils.TAG, "postAcceptOrdersRequest response.body()!!.data!!.cid=>" + response.body()!!.data!!.cid)

                        UpdateRealTimeDriverOrderStatus.updateRealTimeDriverOrderStatus(mActivity!!, OGoConstant.DRIVER_ACCEPTED)

                        //// show here all scanned order accepted list ( api needed )
                        /// as of now showing only order history ( for testing )
                       ordersHistoryPost("john@ogo.delivery","01/6/2021","01/6/2021","ALL","ALL")



                    } else {

                        Toast.makeText(mActivity, response.body()!!.message, Toast.LENGTH_LONG).show()

                    }


                }
            }

            override fun onFailure(call: Call<AcceptedOrderResponse>, t: Throwable) {

                if (t is IOException) {
                    Toast.makeText(mActivity, "this is an actual network failure :( inform the user and possibly retry", Toast.LENGTH_SHORT).show();
                    // logging probably not necessary
                } else {
                    Toast.makeText(mActivity, "conversion issue! big problems :(", Toast.LENGTH_SHORT).show();
                    // todo log to some central bug tracking service
                    Toast.makeText(mActivity, " Unable to submit postAcceptOrdersRequest to API.!", Toast.LENGTH_LONG).show();
                }

                LogUtils.error("TAG", "Unable to submit postAcceptOrdersRequest to API." + t.printStackTrace())
                LogUtils.error("TAG", "Unable to submit postAcceptOrdersRequest to API.")


                // showProgress(false)


            }
        })


    }

    fun showAcceptedOrderResponseonse(response: String) {
        LogUtils.error("TAG>>>>RESPONSE>>>>", response)
    }



    ///////Web Service call to get order history
    fun ordersHistoryPost(userNameStr: String, fromDate: String, toDate: String, paymentType: String, orderStatusString: String) {
        LogUtils.error(LogUtils.TAG, "OrdersHistoryPost userNameStr=>" + userNameStr)
        LogUtils.error(LogUtils.TAG, "OrdersHistoryPost fromDate=>" + fromDate)
        LogUtils.error(LogUtils.TAG, "OrdersHistoryPost toDate=>" + toDate)
        LogUtils.error(LogUtils.TAG, "OrdersHistoryPost paymentType=>" + paymentType)


        apiPostService = ApiPostUtils.apiPostService

        apiPostService!!.postOrdersHistory(userNameStr, fromDate, toDate, paymentType).enqueue(object : Callback<OrderHistoryResponse> {

            override fun onResponse(call: Call<OrderHistoryResponse>, response: Response<OrderHistoryResponse>) {

                LogUtils.error(LogUtils.TAG, "response.raw().toString() =>" + response.raw().toString())

                if (response.isSuccessful) {

                    showOrderHistoryResponseonse(response.body()!!.toString())

                    var orderHistoryResponseReceived = response.body()!!
                    var s = orderHistoryResponseReceived.data

                    if (orderHistoryResponseReceived.status.toString().equals("true", true)) {
                        LogUtils.error(LogUtils.TAG, "postOrdersHistory orderHistoryResponseReceived.message.toString() =>" + orderHistoryResponseReceived.message.toString())
                        //Toast.makeText(ctx, orderHistoryResponseReceived.message, Toast.LENGTH_LONG).show()


                        if (orderHistoryResponseReceived.data!!.isNotEmpty()) {


                            ///clear oder from here also
                            orderHistoryEntities.clear()



                            for (orderHistoryResponseData in orderHistoryResponseReceived.data!!) {

                                if (orderStatusString.equals(OGoConstant.COMPLETED_ORDER_STRING) && orderHistoryResponseData.order_status_int.equals(OGoConstant.COMPLETED)) {
                                    orderHistoryEntities.add(orderHistoryResponseData)
                                    ordersAdapter!!.notifyDataSetChanged()

                                } else if (orderStatusString.equals(OGoConstant.CANCEL_ORDER_STRING) && orderHistoryResponseData.order_status_int.equals(OGoConstant.CANCEL))
                                {
                                    orderHistoryEntities.add(orderHistoryResponseData)
                                    ordersAdapter!!.notifyDataSetChanged()

                                } else if(orderStatusString.equals(OGoConstant.ALL_STRING))
                                {
                                    orderHistoryEntities.add(orderHistoryResponseData)
                                    ordersAdapter!!.notifyDataSetChanged()

                                }

                            }

                        } else {

//                            binding.row4!!.visibility = View.GONE
                            ///clear oder from here also
                            orderHistoryEntities.clear()
                            ordersAdapter!!.notifyDataSetChanged()
                            Toast.makeText(mActivity, "No order found.", Toast.LENGTH_LONG).show()


                        }


                    } else {

//                        binding.row4!!.visibility = View.GONE
                        ///clear order from here also
                        orderHistoryEntities.clear()
                        ordersAdapter!!.notifyDataSetChanged()

                        Toast.makeText(mActivity, orderHistoryResponseReceived.message, Toast.LENGTH_LONG).show()

                    }


                }
            }

            override fun onFailure(call: Call<OrderHistoryResponse>, t: Throwable) {

                if (t is IOException) {
                    Toast.makeText(mActivity, "this is an actual network failure :( inform the user and possibly retry", Toast.LENGTH_SHORT).show();
                    // logging probably not necessary
                } else {
                    Toast.makeText(mActivity, "conversion issue! big problems :(", Toast.LENGTH_SHORT).show();
                    // todo log to some central bug tracking service
                    Toast.makeText(mActivity, " Unable to submit postOrdersHistory to API.!", Toast.LENGTH_LONG).show();
                }

                LogUtils.error("TAG", "Unable to submit postOrdersHistory to API." + t.printStackTrace())
                LogUtils.error("TAG", "Unable to submit postOrdersHistory to API.")

                // showProgress(false)

            }
        })


    }


    fun showOrderHistoryResponseonse(response: String) {
        LogUtils.error("TAG>>>>RESPONSE>>>>", response)
    }


}