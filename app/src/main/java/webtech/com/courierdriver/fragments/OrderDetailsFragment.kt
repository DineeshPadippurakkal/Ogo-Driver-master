package webtech.com.courierdriver.fragments

/*
Created by ​
Hannure Abdulrahim


on 11/5/2018.
 */


import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.json.JSONArray
import webtech.com.courierdriver.R
import webtech.com.courierdriver.adapter.OrderDetailsItemAdapter
import webtech.com.courierdriver.communication.response.ReceiveCurrentOrderResponseData
import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.databinding.FragmentOrderDetailsBinding
import webtech.com.courierdriver.databinding.FragmentOrderHistoryDetailBinding


/*
Created by ​
Hannure Abdulrahim


on 11/5/2018.
 */
 class OrderDetailsFragment : Fragment() {

    private var orderDetailsEntityJson: String? = null
    private var orderDetailsEntity: ReceiveCurrentOrderResponseData? = null
    // private OrderRequest orderResquest;

    private var orderItemAdapter: OrderDetailsItemAdapter? = null
    var mActivity: Activity? = null

    private lateinit var binding: FragmentOrderDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            orderDetailsEntityJson = requireArguments().getString(ORDER_ENTITY_JSON)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_details, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentOrderDetailsBinding.bind(view)

        orderDetailsEntity = Gson().fromJson(orderDetailsEntityJson, ReceiveCurrentOrderResponseData::class.java)

        //orderResquest = new Gson().fromJson(orderDetailsEntity.getJsonOrderDetails(), OrderRequest.class);

        binding.tvOrderId!!.text = getString(R.string.order_id) + orderDetailsEntity!!.orderId!!

        binding.tvDate!!.text = getString(R.string.order_details_date) + orderDetailsEntity!!.updatedOrderDate!!
        binding.source!!.text = orderDetailsEntity!!.source

        //orderType!!.text = orderDetailsEntity!!.typeOfOrder

        if (orderDetailsEntity!!.typeOfOrder!!.equals(OGoConstant.RETURN_TRIP_COMP_TO_CLIENT)|| orderDetailsEntity!!.typeOfOrder!!.equals(OGoConstant.RETURN_TRIP_CLIENT_TO_COMP) )
        {
            /// image is not coming from server ,use local assets
            Glide.with(binding.ivOrderType.context)
                    .load(R.drawable.trip_return)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)///don't cache
                    //.placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_image_preview)///if url exist but images not available on server
                    .into(binding.ivOrderType)

            binding.tvOrderType.text = binding.tvOrderType.context.getString(R.string.return_trip)
        } else{
            /// image is not coming from server ,use local assets
            Glide.with(binding.ivOrderType.context)
                    .load(R.drawable.trip_single_en)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)///don't cache
                    //.placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_image_preview)///if url exist but images not available on server
                    .into(binding.ivOrderType)

            binding.tvOrderType.text = getString(R.string.single_trip)



        }


        binding.orderInvoice!!.text = "KD " + String.format("%.3f", orderDetailsEntity!!.totalInvoiceAmount!!.toDouble())

        binding.tvPaymentType!!.text = orderDetailsEntity!!.gatewayType

        binding.tvSenderName.text = getString(R.string.order_details_sender_name)+orderDetailsEntity!!.senderName
        binding.tvSenderAddress.text = getString(R.string.order_details_sender_address)+orderDetailsEntity!!.senderLocation
        binding.tvSenderPhone.text = getString(R.string.order_details_sender_phone)+orderDetailsEntity!!.senderPhone

        binding.tvReceiverName.text = getString(R.string.order_details_receiver_name)+orderDetailsEntity!!.receiverName
        binding.tvReceiverAddress.text = getString(R.string.order_details_receiver_address)+orderDetailsEntity!!.receiverLocation
        binding.tvReceiverPhone.text = getString(R.string.order_details_receiver_phone)+orderDetailsEntity!!.receiverPhone


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
        val dataImagesStrings = orderDetailsEntity!!.productImageArrayJson



        if (dataImagesStrings != null && dataImagesStrings.length > 0) {
            val separatedDataImagesStrings = dataImagesStrings.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (item in separatedDataImagesStrings) {
                // System.out.println("item = " + item);
                Log.e("Testing:", " image url item =>$item\n")
                imagesItems.add(item.trim { it <= ' ' })
            }
        }



        orderItemAdapter = OrderDetailsItemAdapter(imagesItems)
        //recyclerViewOrderItems!!.layoutManager = GridLayoutManager(mActivity!!, 1)
        val layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewOrderItems!!.layoutManager=layoutManager
        binding.recyclerViewOrderItems!!.adapter = orderItemAdapter

        //  totalLabel.setText("Total Items: " + orderDetailsEntity.get);
        var totalDistance = orderDetailsEntity!!.estimatedDistanceKm.toString()
        var totalPriceD = 0.0
        if (orderDetailsEntity!!.totalFare != null) {
            if (orderDetailsEntity!!.totalFare!!.toDouble()> 0) {
                totalPriceD = java.lang.Double.parseDouble(orderDetailsEntity!!.totalFare!!.toString())
            }
        }
//        deliveryCharges!!.text = "KD " + String.format("%.2f", totalPriceD)
        binding.deliveryCharges!!.text = totalDistance + " KM"

        /*  if (orderDetailsEntity.getStatusDescription().equalsIgnoreCase(OrderStatus.ORDER_BEING_COLLECTED)) {
            orderCard.setBackgroundResource(R.drawable.rect_green_border);
        } else if (orderDetailsEntity.getStatusDescription().equalsIgnoreCase(OrderStatus.OUT_FOR_DELIVERY)) {
            orderCard.setBackgroundResource(R.drawable.rect_green_border);
        } else if (orderDetailsEntity.getStatusDescription().equalsIgnoreCase(OrderStatus.ORDER_DELIVERD)) {
            orderCard.setBackgroundResource(R.drawable.rect_green_border);
        } else {
            orderCard.setBackgroundResource(R.drawable.rect_red_border);
        }


        btnGoRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new ShowDirectionEvent(orderDetailsEntity.getResId(), orderDetailsEntity.getBranchId()));
            }
        });*/
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
        //if (EventBus.getDefault().isRegistered(mActivity))
        //EventBus.getDefault().unregister(mActivity)
    }

    companion object {

        private val ORDER_ENTITY_JSON = "order_entity_json"

        fun newInstance(orderDetailsEntityJson: String): OrderDetailsFragment {
            val fragment = OrderDetailsFragment()
            val args = Bundle()
            args.putString(ORDER_ENTITY_JSON, orderDetailsEntityJson)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor

