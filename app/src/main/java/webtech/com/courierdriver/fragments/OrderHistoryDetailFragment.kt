package webtech.com.courierdriver.fragments


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import webtech.com.courierdriver.R
import webtech.com.courierdriver.activity.MainActivity
import webtech.com.courierdriver.adapter.OrderHistoryItemAdapter
import webtech.com.courierdriver.communication.ApiPostService
import webtech.com.courierdriver.communication.ApiPostUtils
import webtech.com.courierdriver.communication.response.AcceptedOrderResponse
import webtech.com.courierdriver.communication.response.OrderHistoryResponseData
import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.databinding.FragmentOrderHistoryDetailBinding
import webtech.com.courierdriver.databinding.FragmentOrderReceiverMapBinding
import webtech.com.courierdriver.utilities.LogUtils
import webtech.com.courierdriver.utilities.PreferenceHelper


/*
Created by ​
Hannure Abdulrahim


on 11/4/2018.
 */

class OrderHistoryDetailFragment : Fragment() {

    var preferenceHelper: PreferenceHelper? = null
    private var orderHistoryEntityJson: String? = null
    private var orderHistoryEntity: OrderHistoryResponseData? = null
    // private OrderRequest orderResquest;

    private var orderItemAdapter: OrderHistoryItemAdapter? = null
    var mActivity: Activity? = null


    private lateinit var binding: FragmentOrderHistoryDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            orderHistoryEntityJson = requireArguments().getString(ORDER_ENTITY_JSON)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_history_detail, container, false)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentOrderHistoryDetailBinding.bind(view)
        preferenceHelper = PreferenceHelper(mActivity!!)

        orderHistoryEntity =
            Gson().fromJson(orderHistoryEntityJson, OrderHistoryResponseData::class.java)

        //orderResquest = new Gson().fromJson(orderHistoryEntity.getJsonOrderDetails(), OrderRequest.class);

        binding.tvOrderId!!.text = getString(R.string.order_id) + orderHistoryEntity!!.orderId!!



        if (orderHistoryEntity!!.order_status_int != null) {
            if (orderHistoryEntity!!.order_status_int.toString().equals(OGoConstant.CANCEL, true)) {
                binding.tvOrderStatus!!.text = getString(R.string.order_history_cancelled)
                binding.btnAccept.visibility = View.GONE
                binding.tvOrderStatus!!.setTextColor(Color.parseColor("#ff0000"))


            } else if ((orderHistoryEntity!!.order_status_int.toString()
                    .equals(OGoConstant.COMPLETED, true))
            ) {
                binding.tvOrderStatus!!.setTextColor(Color.parseColor("#3CB371"))
                binding.tvOrderStatus!!.text = getString(R.string.order_history_completed)
                binding.btnAccept.visibility = View.GONE
            } else if ((orderHistoryEntity!!.order_status_int.toString()
                    .equals(OGoConstant.PENDING, true))
            ) {
                binding.tvOrderStatus!!.setTextColor(Color.parseColor("#F4C700"))
                binding.tvOrderStatus!!.text = "PENDING"
                binding.btnAccept.visibility = View.VISIBLE
            }else if ((orderHistoryEntity!!.order_status_int.toString()
                    .equals(OGoConstant.DRIVER_ACCEPTED, true)) ||
                (orderHistoryEntity!!.order_status_int.toString()
                    .equals(OGoConstant.DRIVER_AT_SOURCE, true)) ||
                (orderHistoryEntity!!.order_status_int.toString()
                    .equals(OGoConstant.ORDER_COLLECTED, true)) ||
                (orderHistoryEntity!!.order_status_int.toString()
                    .equals(OGoConstant.DRIVER_AT_DESTINATION, true)) ||
                (orderHistoryEntity!!.order_status_int.toString()
                    .equals(OGoConstant.DELIVERED, true))
            ) {
                binding.tvOrderStatus!!.setTextColor(Color.parseColor("#F4C700"))
                binding.tvOrderStatus!!.text = getString(R.string.order_history_ongoing)
                binding.btnAccept.visibility = View.GONE
            }
        }

        binding.tvDate!!.text =
            getString(R.string.order_history_date) + orderHistoryEntity!!.updatedOrderDate!!
        binding.tvPaymentType!!.text = orderHistoryEntity!!.gatewayType

        binding.addressLineOne!!.text = orderHistoryEntity!!.senderLocation
        binding.addressLineTwo!!.text = orderHistoryEntity!!.receiverLocation


        binding.btnAccept.setOnClickListener {
            callAcceptOrder(
                userNameStr = preferenceHelper!!.loggedInUser!!.emailId.toString(),
                orderId = orderHistoryEntity!!.orderId!!,
                companyId = preferenceHelper!!.loggedInUser!!.cid.toString()
            )
        }


        /*
       * String input = "[\"477\",\"com.dummybilling\",\"android.test.purchased\",\"inapp:com.dummybilling:android.test.purchased\",\"779\"]";

            JSONArray jsonArray = new JSONArray(input);
            String[] strArr = new String[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                strArr[i] = jsonArray.getString(i);
            }

        System.out.println(Arrays.toString(strArr));

        [477, com.dummybilling, android.test.purchased, inapp:com.dummybilling:android.test.purchased, 779]
       *
       *
       *
       * */


        val jsonArray: JSONArray? = null
        val imagesItems = ArrayList<String>()
        /////data from server is coming without {} hence can not be convert to json object hence added here
        val dataImagesStrings = orderHistoryEntity!!.productImageArrayJson



        if (dataImagesStrings != null && dataImagesStrings.length > 0) {
            val separatedDataImagesStrings =
                dataImagesStrings.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (item in separatedDataImagesStrings) {
                // System.out.println("item = " + item);
                Log.e("Testing:", " image url item =>$item\n")
                imagesItems.add(item.trim { it <= ' ' })
            }
        }



        if (imagesItems != null && imagesItems.size > 0) {
            binding.rowOrderImages.visibility = View.VISIBLE
            orderItemAdapter = OrderHistoryItemAdapter(imagesItems)
            //recyclerViewOrderItems!!.layoutManager = GridLayoutManager(mActivity!!, 3)
            val layoutManager =
                LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
            binding.recyclerViewOrderItems!!.layoutManager = layoutManager
            binding.recyclerViewOrderItems!!.adapter = orderItemAdapter
        } else {
            binding.rowOrderImages.visibility = View.GONE
        }

        //  totalLabel.setText("Total Items: " + orderHistoryEntity.get);
        var totalPriceD = 0.0
        if (orderHistoryEntity!!.totalFare != null) {
            if (orderHistoryEntity!!.totalFare!!.toDouble()!! > 0) {
                totalPriceD =
                    java.lang.Double.parseDouble(orderHistoryEntity!!.totalFare!!.toString())
            }
        }
//        totalPrice!!.text = "KD " + String.format("%.2f", totalPriceD)
        binding.totalPrice!!.text = orderHistoryEntity!!.estimatedDistanceKm!!.toString() + " KM"

        // orderType!!.text = orderHistoryEntity!!.typeOfOrder

        if (orderHistoryEntity!!.typeOfOrder!!.equals(OGoConstant.RETURN_TRIP_COMP_TO_CLIENT) || orderHistoryEntity!!.typeOfOrder!!.equals(
                OGoConstant.RETURN_TRIP_CLIENT_TO_COMP
            )
        ) {
            /// image is not coming from server ,use local assets
            Glide.with(mActivity!!)
                .load(R.drawable.trip_return)
                .diskCacheStrategy(DiskCacheStrategy.NONE)///don't cache
                //.placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_image_preview)///if url exist but images not available on server
                .into(binding.ivOrderTrip)


        } else {
            /// image is not coming from server ,use local assets
            Glide.with(mActivity!!)
                .load(R.drawable.trip_single_en)
                .diskCacheStrategy(DiskCacheStrategy.NONE)///don't cache
                //.placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_image_preview)///if url exist but images not available on server
                .into(binding.ivOrderTrip)


        }


        /*  if (orderHistoryEntity.getStatusDescription().equalsIgnoreCase(OrderStatus.ORDER_BEING_COLLECTED)) {
            orderCard.setBackgroundResource(R.drawable.rect_green_border);
        } else if (orderHistoryEntity.getStatusDescription().equalsIgnoreCase(OrderStatus.OUT_FOR_DELIVERY)) {
            orderCard.setBackgroundResource(R.drawable.rect_green_border);
        } else if (orderHistoryEntity.getStatusDescription().equalsIgnoreCase(OrderStatus.ORDER_DELIVERD)) {
            orderCard.setBackgroundResource(R.drawable.rect_green_border);
        } else {
            orderCard.setBackgroundResource(R.drawable.rect_red_border);
        }


        btnGoRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new ShowDirectionEvent(orderHistoryEntity.getResId(), orderHistoryEntity.getBranchId()));
            }
        });*/
    }

    private fun callAcceptOrder(userNameStr: String, orderId: String, companyId: String) {
        var apiPostService: ApiPostService? = null

        apiPostService = ApiPostUtils.apiPostService

        apiPostService!!.postAcceptOrdersRequest(userNameStr, orderId, companyId).enqueue(object :
            Callback<AcceptedOrderResponse> {

            override fun onResponse(
                call: Call<AcceptedOrderResponse>,
                response: Response<AcceptedOrderResponse>
            ) {

                LogUtils.error(
                    LogUtils.TAG,
                    "response.raw().toString() =>" + response.raw().toString()
                )

                if (response.isSuccessful) {

                    if (response.body()!!.status.toString().equals("true", true)) {

                        LogUtils.error(LogUtils.TAG, "postAcceptOrdersRequest response.body()!!.message.toString() =>" + response.body()!!.message.toString())
                        Toast.makeText(mActivity, "Order Accepted", Toast.LENGTH_LONG).show()

                        var intent = Intent(activity, MainActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)

                    } else {
                        Toast.makeText(mActivity, response.body()!!.message, Toast.LENGTH_LONG).show()
                    }


                }
            }

            override fun onFailure(call: Call<AcceptedOrderResponse>, t: Throwable) {

                LogUtils.error(
                    "TAG",
                    "Unable to submit postAcceptOrdersRequest to API." + t.printStackTrace()
                )
                LogUtils.error("TAG", "Unable to submit postAcceptOrdersRequest to API.")

                // showProgress(false)
            }

        })

    }


    /////////////////////////////////////S
    /*//Error If i rotate screen from portrait to landscape then it was getting crash hence restricted to portrait mode
     below two method will work*/

    override fun onResume() {
        super.onResume()
        mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

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
            val activity = if (context is Activity) context else null
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
    }

    companion object {

        private val ORDER_ENTITY_JSON = "order_entity_json"

        fun newInstance(orderHistoryEntityJson: String): OrderHistoryDetailFragment {
            val fragment = OrderHistoryDetailFragment()
            val args = Bundle()
            args.putString(ORDER_ENTITY_JSON, orderHistoryEntityJson)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor

