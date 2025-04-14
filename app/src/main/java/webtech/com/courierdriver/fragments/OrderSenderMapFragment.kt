package webtech.com.courierdriver.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.joinersa.oooalertdialog.Animation
import br.com.joinersa.oooalertdialog.OnClickListener
import br.com.joinersa.oooalertdialog.OoOAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.maps.GoogleMap
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import webtech.com.courierdriver.R
import webtech.com.courierdriver.activity.MainActivity
import webtech.com.courierdriver.communication.ApiPostService
import webtech.com.courierdriver.communication.ApiPostUtils
import webtech.com.courierdriver.communication.response.ReceiveCurrentOrderResponseData
import webtech.com.courierdriver.communication.response.UpdateOderStatusResp
import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.databinding.FragmentOrderSenderMapBinding
import webtech.com.courierdriver.databinding.FragmentOrdersBinding
import webtech.com.courierdriver.events.GoReceiverEvent
import webtech.com.courierdriver.events.LocationChangedEvent
import webtech.com.courierdriver.events.OrderDetailsEvent
import webtech.com.courierdriver.firebase.UpdateRealTimeDriverOrderStatus
import webtech.com.courierdriver.utilities.DriverUtilities
import webtech.com.courierdriver.utilities.LogUtils
import webtech.com.courierdriver.utilities.NetworkUtil
import webtech.com.courierdriver.utilities.PreferenceHelper
import java.io.IOException


/*
Created by ​
Hannure Abdulrahim


on 10/25/2018.
 */


class OrderSenderMapFragment : Fragment() {


    private var orderEntity: ReceiveCurrentOrderResponseData? = null
    //private OrderRequest orderResquest;

    private var mMap: GoogleMap? = null
    private var senderLat: String? = null
    private var senderLon: String? = null

    private var myLat = 0.0
    private var myLon = 0.0

    var mActivity: Activity? = null
    var apiPostService: ApiPostService? = null
    private var preferenceHelper: PreferenceHelper?=null
    var orderEntityJson: String? = null

    private lateinit var binding: FragmentOrderSenderMapBinding



//    private var mMapReadyCallback: OnMapReadyCallback? = null
//
//    val mapAsync: OnMapReadyCallback
//        get() {
//            if (mMapReadyCallback == null) {
//                mMapReadyCallback = OnMapReadyCallback { googleMap ->
//                    mMap = googleMap
//                    mMap!!.uiSettings.isZoomControlsEnabled = false
//                    mMap!!.uiSettings.isCompassEnabled = false
//                    mMap!!.uiSettings.isRotateGesturesEnabled = false
//                    mMap!!.uiSettings.isTiltGesturesEnabled = false
//                    mMap!!.animateCamera(CameraUpdateFactory.zoomTo(13f), 2000, null)
//
//                    updateMap()
//                }
//            }
//            return mMapReadyCallback as OnMapReadyCallback
//        }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
        ////check companion object
        if (arguments != null) {
            orderEntityJson = requireArguments().getString(ORDER_ENTITY_JSON)
        }
        preferenceHelper = PreferenceHelper(mActivity!!)




    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_sender_map, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentOrderSenderMapBinding.bind(view)

        orderEntity = Gson().fromJson(orderEntityJson, ReceiveCurrentOrderResponseData::class.java)

       // orderEntity!!.orderStatusInt

        //orderResquest = new Gson().fromJson(orderEntity.getJsonOrderDetails(), OrderRequest.class);

        LogUtils.error(LogUtils.TAG," orderEntity!!.senderName in OrderSenderMapFragment => " +  orderEntity!!.senderName);

        //if(orderEntity!!.typeOfOrder!!.equals(OGoConstant.SINGLE_TRIP_COMP_TO_CLIENT)|| orderEntity!!.typeOfOrder!!.equals(OGoConstant.SINGLE_TRIP_CLIENT_TO_COMP) )

        if (orderEntity!!.typeOfOrder!!.equals(OGoConstant.RETURN_TRIP_COMP_TO_CLIENT)|| orderEntity!!.typeOfOrder!!.equals(OGoConstant.RETURN_TRIP_CLIENT_TO_COMP) )
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



        binding.senderName!!.setText(orderEntity!!.senderName!!)
        binding.editAddress!!.setText(orderEntity!!.senderLocation!!)
        binding.orderSource!!.setText(orderEntity!!.source!!)
        binding.editPayment!!.setText(orderEntity!!.gatewayType!!)
        //editArea.setText(orderResquest.getUserInfo().getArea());

        binding.tvPhoneNumberSender!!.setText(orderEntity!!.senderPhone!!)

        val startAnimation = AnimationUtils.loadAnimation(context, R.anim.blinking_animation_once)
        binding.ivCallSender!!.startAnimation(startAnimation)


//        editDeliveryCharges!!.setText( String.format("%.3f", orderEntity!!.totalFare!!.toDouble()) + " KD")
        binding.editDeliveryCharges!!.setText( orderEntity!!.estimatedDistanceKm.toString() + " KM")


        ///// only web has invoice , mobile dont hence no need to show invoice for mobile
        if(orderEntity!!.source!!.equals(OGoConstant.WEB,true))
        {
            //show invoice here
            binding.editTotalInvoiceOrder!!.setText( String.format("%.3f", orderEntity!!.totalInvoiceAmount!!.toDouble() )+ " KD")

            if(orderEntity!!.gatewayType!!.equals(OGoConstant.COD,true))
            {
                //orderPaymentNote!!.setTextColor(Color.parseColor("#ff0000"))
                // here collect invoice amount , delivery charges already deducted during order creation from web
                binding.orderPaymentNote!!.setText(getString(R.string.order_payment_msg_unpaid)+ "("+ String.format("%.3f", orderEntity!!.totalInvoiceAmount!!.toDouble() )+ " KD"+ ")")


            }else
            {
               // orderPaymentNote!!.setTextColor(Color.parseColor("#3CB371"))
                binding.orderPaymentNote!!.setText(getString(R.string.order_payment_msg_paid))

            }




        }else
        {
            // mobile dont have any invoice now , so hide here
            binding.editTotalInvoiceOrder!!.setText( "--")

            if(orderEntity!!.gatewayType!!.equals(OGoConstant.COD,true))
            {
               // orderPaymentNote!!.setTextColor(Color.parseColor("#ff0000"))
                // here collect only delivery charges since from mobile order created ....
                binding.orderPaymentNote!!.setText(getString(R.string.order_payment_msg_unpaid)+ "("+ String.format("%.3f", orderEntity!!.totalFare!!.toDouble()) + " KD" +")")


            }else
            {
               // orderPaymentNote!!.setTextColor(Color.parseColor("#3CB371"))
                binding.orderPaymentNote!!.setText(getString(R.string.order_payment_msg_paid))

            }





        }


        binding.senderPhoneLayout.setOnClickListener {
            val numberByTV = Uri.parse("tel:" + orderEntity!!.senderPhone!!)
            val callIntentByTV = Intent(Intent.ACTION_DIAL, numberByTV)
            startActivity(callIntentByTV)

        }

        binding.tvDirectionSenderLayout.setOnClickListener {

            val intent = Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr=$senderLat,$senderLon"))
            startActivity(intent)

        }

        binding.transparentOverlaySenderDetails.setOnClickListener {

            EventBus.getDefault().post(OrderDetailsEvent(orderEntity))
        }



        /// manage here your order status and based on order status call api

        LogUtils.error(LogUtils.TAG,"orderStatus in  OrderSenderMapFragment : "+preferenceHelper!!.orderStatus.toString())
       when(preferenceHelper!!.orderStatus.toString().toInt())
       {


           OGoConstant.DRIVER_ACCEPTED.toInt() ->
                orderAcceptedCase()

           OGoConstant.DRIVER_AT_SOURCE.toInt() ->
               driverAtSourceCase()
           OGoConstant.ORDER_COLLECTED.toInt()  ->
               orderCollectedCase()
           OGoConstant.DRIVER_AT_DESTINATION.toInt() ->
               //same as status 3
               orderCollectedCase()
           OGoConstant.DELIVERED.toInt()  ->
               //same as status 3
               orderCollectedCase()
           OGoConstant.RETURN_ORDER_COLLECTED.toInt() ->
               //same as status 3
               orderCollectedCase()
           OGoConstant.RETURN_ORDER_TO_SOURCE.toInt() ->
               //same as status 3
               orderCollectedCase()
           OGoConstant.RETURN_ORDER_DELIVERED.toInt()->
               //same as status 3
               orderCollectedCase()







       }





        binding.arrivedToSenderSlideView!!.setOnSlideCompleteListener {
            // vibrate the device
            DriverUtilities.vibrateDevice(requireContext(),100)

            run {
                val mPlayer = MediaPlayer.create(mActivity, R.raw.fire_pager)
                mPlayer.start()
                LogUtils.error(LogUtils.TAG, "Status => mPlayer.start(); called : driver at source")

            }




            //////////////////////////////////////////
            ////Animation
            //////////////////////////////////////////

            val startAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.blinking_animation_once)
            binding.goToReceiverSlideView.startAnimation(startAnimation)


            if (NetworkUtil.getInstance(mActivity!!).isOnline)
            {
                /////update driver order status here
                updateOrderStatusPost(preferenceHelper!!.loggedInUser!!.emailId!!,orderEntity!!.orderId!!,preferenceHelper!!.loggedInUser!!.cid!!, OGoConstant.DRIVER_AT_SOURCE)


            }else
            {
                NetworkUtil.getInstance(mActivity!!).showCustomNetworkError(mActivity!!)

            }




        }

        binding.goToReceiverSlideView!!.setOnSlideCompleteListener {




            DriverUtilities.vibrateDevice(requireContext(),100)



            var titleMsg:String
            var message:String
            var positiveButtonMsg:String
            var negativeButtonMsg:String

            titleMsg = getString(R.string.order_collection_title)
            message = getString(R.string.order_collection_messege)
            positiveButtonMsg = getString(R.string.order_collection_no)
            negativeButtonMsg = getString(R.string.order_collection_yes)



            OoOAlertDialog.Builder(mActivity)
                    .setTitle(titleMsg)
                    .setTitleColor(R.color.color_them)
                    .setMessage(message)
                    .setMessageColor(R.color.black)
                    .setImage(R.drawable.order_collected)
                    .setAnimation(Animation.ZOOM)
                    .setPositiveButton(positiveButtonMsg, null)
                    .setNegativeButton(negativeButtonMsg,  OnClickListener() {

                        if (NetworkUtil.getInstance(mActivity!!).isOnline)
                        {


                            updateOrderStatusPost(preferenceHelper!!.loggedInUser!!.emailId!!,orderEntity!!.orderId!!,preferenceHelper!!.loggedInUser!!.cid!!,OGoConstant.ORDER_COLLECTED)

                            run {
                                val mPlayer = MediaPlayer.create(mActivity, R.raw.door_entry_notification)
                                mPlayer.start()

                                LogUtils.error(LogUtils.TAG, "Status => mPlayer.start(); called : order collection confirmed!")
                            }






                        }else
                        {
                            NetworkUtil.getInstance(mActivity!!).showCustomNetworkError(mActivity!!)

                        }


                    })
                    .build()

        }


        binding.tvForwordLayout.setOnClickListener {

            EventBus.getDefault().post(GoReceiverEvent(orderEntity))

        }







//        val mapFragment = SupportMapFragment()
//        childFragmentManager.beginTransaction()
//                .replace(R.id.mapContainer, mapFragment).commit()
//        mapFragment.getMapAsync(mapAsync)


        senderLat = orderEntity!!.senderLatitude
        senderLon = orderEntity!!.senderLongitute

        if ((mActivity as MainActivity).lat != null && (mActivity as MainActivity).lng != null) {
            myLat = (mActivity as MainActivity).lat!!
            myLon = (mActivity as MainActivity).lng!!
        }



        //Log.e("Test"," myLat in onCreateView() OrderSenderMapFragment.java =>  "+ myLat );
        //Log.e("Test"," myLon in onCreateView() OrderSenderMapFragment.java =>  "+ myLon );


    }


//    private fun updateMap() {
//
//        if (senderLat != null && senderLon != null) {
//
//            if (!senderLat!!.equals("", ignoreCase = true) && !senderLon!!.equals("", ignoreCase = true)) {
//                val customerLocation = LatLng(java.lang.Double.parseDouble(senderLat!!),
//                        java.lang.Double.parseDouble(senderLon!!))
//                //Customar Marker
//                mMap!!.addMarker(MarkerOptions().position(customerLocation)
//                        .snippet(getString(R.string.sender_location))
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.sender_marker))
//                        .anchor(0.5f, 1f))
//
//                //Driver Marker
//                val myLocation = LatLng(myLat, myLon)
//                mMap!!.addMarker(MarkerOptions().position(myLocation)
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.driver_marker_top_view))
//                        .snippet(getString(R.string.my_location))
//                        .anchor(0.5f, 1f))
//
//                mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(customerLocation, 16f))
//                mMap!!.animateCamera(CameraUpdateFactory.zoomTo(13f), 2000, null)
//
//            }
//
//        }
//
//    }





    /*//Error If i rotate screen from portrait to landscape then it was getting crash hence restricted to portrait mode
     below two method will work*/

    override fun onResume() {
        super.onResume()
        mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        (mActivity as AppCompatActivity).getSupportActionBar()!!.setTitle(R.string.sender_title)



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
            val activity = context as? Activity
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

        // Log.e("Test"," myLat in onLocationUpdated() OrderSenderMapFragment.java =>  "+ myLat );
        // Log.e("Test"," myLon in onLocationUpdated() OrderSenderMapFragment.java =>  "+ myLon );
    }





    companion object {

        private val ORDER_ENTITY_JSON = "order_entity_json"


        // Required empty public constructor
        fun newInstance(orderEntityJsonString: String): OrderSenderMapFragment {

            val fragment = OrderSenderMapFragment()
            val args = Bundle()
            args.putString(ORDER_ENTITY_JSON, orderEntityJsonString)
            fragment.arguments = args
            return fragment

        }
    }

    ///////Web Service call to update order status
    fun updateOrderStatusPost(userNameStr: String, orderId: String,companyId: String,orderStatus: String) {
       // LogUtils.error(LogUtils.TAG, "updateOrderStatusPost userNameStr=>" + userNameStr)
       // LogUtils.error(LogUtils.TAG, "updateOrderStatusPost orderId=>" + orderId)
       // LogUtils.error(LogUtils.TAG, "updateOrderStatusPost companyId=>" + companyId)
       // LogUtils.error(LogUtils.TAG, "updateOrderStatusPost orderStatus=>" + orderStatus)


        apiPostService= ApiPostUtils.apiPostService
        apiPostService!!.postUpdateOrderStatus(userNameStr,orderId,companyId,orderStatus).enqueue(object : Callback<UpdateOderStatusResp> {

            override fun onResponse(call: Call<UpdateOderStatusResp>, response: Response<UpdateOderStatusResp>) {

                LogUtils.error(LogUtils.TAG, "response.raw().toString() =>" + response.raw().toString())

                if (response.isSuccessful) {

                    showUpdateOderStatusResponse(response.body()!!.toString())

                    if (response.body()!!.status.toString().equals("true", true)) {
                        LogUtils.error(LogUtils.TAG, "postUpdateOrderStatus response.body()!!.message.toString() =>" + response.body()!!.message.toString())
                        LogUtils.error(LogUtils.TAG, "response.body()!!.data!! =>" + response.body()!!.data!!)
                        //Toast.makeText(mActivity, response.body()!!.message, Toast.LENGTH_LONG).show()

                        if(response.body()!!.data!!.equals(OGoConstant.DRIVER_AT_SOURCE_STRING))
                        {

                            binding.goToReceiverSlideView!!.visibility = View.VISIBLE
                            binding.arrivedToSenderSlideView!!.visibility = View.GONE
                            binding.tvForwordLayout!!.visibility = View.GONE

                            preferenceHelper!!.orderStatus  = OGoConstant.DRIVER_AT_SOURCE
                            UpdateRealTimeDriverOrderStatus.updateRealTimeDriverOrderStatus(mActivity!!,OGoConstant.DRIVER_AT_SOURCE)


                        }else if(response.body()!!.data!!.equals(OGoConstant.ORDER_COLLECTED_STRING))
                        {
                            preferenceHelper!!.orderStatus  = OGoConstant.ORDER_COLLECTED
                            UpdateRealTimeDriverOrderStatus.updateRealTimeDriverOrderStatus(mActivity!!,OGoConstant.ORDER_COLLECTED)
                            EventBus.getDefault().post(GoReceiverEvent(orderEntity))

                        }


                    } else {

                        Toast.makeText(mActivity, response.body()!!.message, Toast.LENGTH_LONG).show()

                    }


                }
            }

            override fun onFailure(call: Call<UpdateOderStatusResp>, t: Throwable) {

                if (t is IOException) {
                    Toast.makeText(mActivity, "this is an actual network failure :( inform the user and possibly retry", Toast.LENGTH_SHORT).show();
                    // logging probably not necessary
                }
                else {
                    Toast.makeText(mActivity, "conversion issue! big problems :(", Toast.LENGTH_SHORT).show();
                    // todo log to some central bug tracking service
                    Toast.makeText(mActivity, " Unable to submit postUpdateOrderStatus to API.!", Toast.LENGTH_LONG).show();
                }

                LogUtils.error("TAG", "Unable to submit postUpdateOrderStatus to API."+t.printStackTrace())
                LogUtils.error("TAG", "Unable to submit postUpdateOrderStatus to API.")

                // showProgress(false)

            }
        })


    }

    fun showUpdateOderStatusResponse(response: String) {
        LogUtils.error("TAG>>>>RESPONSE>>>>", response)
    }


    /////// Accepted Case

    fun orderAcceptedCase () {
        LogUtils.error(LogUtils.TAG,"Case1")
        binding.arrivedToSenderSlideView!!.visibility = View.VISIBLE
        binding.goToReceiverSlideView!!.visibility =  View.GONE
        binding.tvForwordLayout!!.visibility = View.GONE

    }

    fun driverAtSourceCase () {
        LogUtils.error(LogUtils.TAG,"Case2")
        binding.arrivedToSenderSlideView!!.visibility =  View.GONE
        binding.goToReceiverSlideView!!.visibility = View.VISIBLE
        binding.tvForwordLayout!!.visibility = View.GONE

    }

    fun orderCollectedCase () {
        LogUtils.error(LogUtils.TAG,"Case3")
        binding.arrivedToSenderSlideView!!.visibility =  View.GONE
        binding.goToReceiverSlideView!!.visibility =  View.GONE
        binding.tvForwordLayout!!.visibility = View.VISIBLE

    }





}



