package webtech.com.courierdriver.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import webtech.com.courierdriver.databinding.FragmentOrderReceiverMapBinding
import webtech.com.courierdriver.databinding.FragmentOrderSenderMapBinding
import webtech.com.courierdriver.events.GoBackToSenderEvent
import webtech.com.courierdriver.events.ListOrdersEvent
import webtech.com.courierdriver.events.LocationChangedEvent
import webtech.com.courierdriver.events.OrderDetailsEvent
import webtech.com.courierdriver.firebase.UpdateRealTimeDriverOrderStatus
import webtech.com.courierdriver.utilities.DriverUtilities
import webtech.com.courierdriver.utilities.LogUtils
import webtech.com.courierdriver.utilities.NetworkUtil
import webtech.com.courierdriver.utilities.PreferenceHelper
import java.io.IOException

/*
Created by â€‹
Hannure Abdulrahim


on 10/28/2018.
 */


class OrderReceiverMapFragment : Fragment() {


    private var orderEntityJson: String? = null
    private var orderEntity: ReceiveCurrentOrderResponseData? = null
    //private OrderRequest orderResquest;

    private var mMap: GoogleMap? = null
    private var receiverLat: String? = null
    private var receiverLon: String? = null

    private var myLat = 0.0
    private var myLon = 0.0

    var mActivity: Activity? = null
    var apiPostService: ApiPostService? = null
    private var preferenceHelper: PreferenceHelper?=null

    private lateinit var binding: FragmentOrderReceiverMapBinding
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
        if (arguments != null) {
            orderEntityJson = requireArguments().getString(ORDER_ENTITY_JSON)
        }
        setHasOptionsMenu(true)
        preferenceHelper = PreferenceHelper(mActivity!!)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_receiver_map, container, false)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentOrderReceiverMapBinding.bind(view)


        orderEntity = Gson().fromJson(orderEntityJson, ReceiveCurrentOrderResponseData::class.java)

        //orderResquest = new Gson().fromJson(orderEntity.getJsonOrderDetails(), OrderRequest.class);

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
        }else {
            /// image is not coming from server ,use local assets
            Glide.with(binding.ivOrderType.context)
                .load(R.drawable.trip_single_en)
                .diskCacheStrategy(DiskCacheStrategy.NONE)///don't cache
                //.placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_image_preview)///if url exist but images not available on server
                .into(binding.ivOrderType)

            binding.tvOrderType.text = getString(R.string.single_trip)



        }

        binding.receiverName!!.setText(orderEntity!!.receiverName)
        binding.editAddress!!.setText(orderEntity!!.receiverLocation)
        binding.orderSource!!.setText(orderEntity!!.source!!)
        binding.editPayment!!.setText(orderEntity!!.gatewayType)
        // editArea.setText(orderResquest.getUserInfo().getArea());
        binding.tvPhoneNumberReceiver!!.text = orderEntity!!.receiverPhone

        val startAnimation = AnimationUtils.loadAnimation(context, R.anim.blinking_animation_once)
        binding.ivCallReceiver!!.startAnimation(startAnimation)




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



        binding.receiverPhoneLayout!!.setOnClickListener {
            val numberByTV = Uri.parse("tel:" + orderEntity!!.receiverPhone!!)
            val callIntentByTV = Intent(Intent.ACTION_DIAL, numberByTV)
            startActivity(callIntentByTV)

        }

        binding.tvDirectionReceiverLayout.setOnClickListener {

            val intent = Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("https://maps.google.com/maps?daddr=$receiverLat,$receiverLon"))
            startActivity(intent)



        }

        binding.transparentOverlayReceiverDetails.setOnClickListener {

            EventBus.getDefault().post(OrderDetailsEvent(orderEntity))
        }


        /// manage here your order status and based on order status call api

        LogUtils.error(LogUtils.TAG,"orderStatus in OrderReceiverMapFragment : "+preferenceHelper!!.orderStatus.toString())
        when(preferenceHelper!!.orderStatus.toString().toInt())
        {


            OGoConstant.ORDER_COLLECTED.toInt() ->

                orderCollectedCase()
            OGoConstant.DRIVER_AT_DESTINATION.toInt()->
                driverAtDestinationCase()

            OGoConstant.DELIVERED.toInt()->

                if(preferenceHelper!!.orderStatus.toString().equals(OGoConstant.DELIVERED) && orderEntity!!.typeOfOrder!!.equals(OGoConstant.SINGLE_TRIP_CLIENT_TO_COMP,true) || orderEntity!!.typeOfOrder!!.equals(OGoConstant.SINGLE_TRIP_COMP_TO_CLIENT,true))
                {
                    orderFinishCase()
                }else
                {
                    orderReturnCollectedCase()
                }

            OGoConstant.RETURN_ORDER_COLLECTED.toInt() ->
                orderBackToSenderCase()

            OGoConstant.RETURN_ORDER_TO_SOURCE.toInt() ->
                orderBackToSenderCase()

            OGoConstant.RETURN_ORDER_DELIVERED.toInt() ->
                orderBackToSenderCase()









        }




        binding.arrivedToReceiverSlideView!!.setOnSlideCompleteListener {
            // vibrate the device
            DriverUtilities.vibrateDevice(requireContext(),100)
            run {
                val mPlayer = MediaPlayer.create(mActivity, R.raw.fire_pager)
                mPlayer.start()
                LogUtils.error(LogUtils.TAG, "Status => mPlayer.start(); called : driver at destination")


            }




            val startAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.blinking_animation_once)
            binding.deliveredOrderSlideView.startAnimation(startAnimation)




            if (NetworkUtil.getInstance(mActivity!!).isOnline)
            {
                //////finish order here directly without taking ratings
                /////update driver order status here
                updateOrderStatusPost(preferenceHelper!!.loggedInUser!!.emailId!!,orderEntity!!.orderId!!,preferenceHelper!!.loggedInUser!!.cid!!, OGoConstant.DRIVER_AT_DESTINATION)

            }else
            {
                NetworkUtil.getInstance(mActivity!!).showCustomNetworkError(mActivity!!)

            }


        }



        binding.deliveredOrderSlideView!!.setOnSlideCompleteListener {


            var titleMsg:String
            var message:String
            var positiveButtonMsg:String
            var negativeButtonMsg:String

            titleMsg = getString(R.string.order_delivered_title)
            message = getString(R.string.order_delivered_messege)
            positiveButtonMsg = getString(R.string.order_delivered_no)
            negativeButtonMsg = getString(R.string.order_delivered_yes)


            showAlertDialogue(titleMsg,message,positiveButtonMsg,negativeButtonMsg,OGoConstant.DELIVERED)






        }



        binding.tvFinishLayout.setOnClickListener {

            finishOrder()

        }

        binding.backToSenderLayout.setOnClickListener {

            /// here go back to sender with new GUI
            EventBus.getDefault().post(GoBackToSenderEvent(orderEntity!!))


        }







//        val mapFragment = SupportMapFragment()
//        childFragmentManager.beginTransaction()
//                .replace(R.id.mapContainer, mapFragment).commit()
//        mapFragment.getMapAsync(mapAsync)

        receiverLat = orderEntity!!.receiverLatitude
        receiverLon = orderEntity!!.receiverLongitute

        if ((mActivity as MainActivity).lat != null && (mActivity as MainActivity).lng != null) {
            myLat = (mActivity as MainActivity).lat!!
            myLon = (mActivity as MainActivity).lng!!
        }


        //Log.e("Test"," myLat in onCreateView() OrderReceiverMapFragment.java =>  "+ myLat );
        //Log.e("Test"," myLon in onCreateView() OrderReceiverMapFragment.java =>  "+ myLon );


    }

//    private fun updateMap() {
//
//        if (receiverLat != null && receiverLon != null) {
//            if (!receiverLat!!.equals("", ignoreCase = true) && !receiverLon!!.equals("", ignoreCase = true)) {
//                val customerLocation = LatLng(Double.parseDouble(receiverLat!!),
//                        Double.parseDouble(receiverLon!!))
//                //Customar Marker
//                mMap!!.addMarker(MarkerOptions().position(customerLocation)
//                        .snippet(getString(R.string.sender_location))
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.receiver_marker))
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
//
//    }





    /*//Error If i rotate screen from portrait to landscape then it was getting crash hence restricted to portrait mode
     below two method will work*/

    override fun onResume() {
        super.onResume()
        mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        (mActivity as AppCompatActivity).getSupportActionBar()!!.setTitle(R.string.receiver_title)

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

        // Log.e("Test"," myLat in onLocationUpdated() OrderReceiverMapFragment.java =>  "+ myLat );
        // Log.e("Test"," myLon in onLocationUpdated() OrderReceiverMapFragment.java =>  "+ myLon );
    }

    companion object {

        private val ORDER_ENTITY_JSON = "order_entity_json"



        // Required empty public constructor

        fun newInstance(orderEntityJson: String): OrderReceiverMapFragment {
            val fragment = OrderReceiverMapFragment()
            val args = Bundle()
            args.putString(ORDER_ENTITY_JSON, orderEntityJson)
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
                    var responseBodyRecived = response.body()!!

                    showUpdateOderStatusResponse(responseBodyRecived.toString())

                    if (responseBodyRecived.status.toString().equals("true", true)) {


                        LogUtils.error(LogUtils.TAG, "postUpdateOrderStatus responseBodyRecived.message.toString() =>" + responseBodyRecived.message.toString())
                        //Toast.makeText(mActivity, responseBodyRecived!!.message, Toast.LENGTH_LONG).show()


                        if(responseBodyRecived!!.data!!.equals(OGoConstant.DRIVER_AT_DESTINATION_STRING))
                        {

                            preferenceHelper!!.orderStatus  = OGoConstant.DRIVER_AT_DESTINATION
                            UpdateRealTimeDriverOrderStatus.updateRealTimeDriverOrderStatus(mActivity!!,OGoConstant.DRIVER_AT_DESTINATION)

                        }


                        if(orderEntity!!.typeOfOrder!!.equals(OGoConstant.SINGLE_TRIP_CLIENT_TO_COMP,true) || orderEntity!!.typeOfOrder!!.equals(OGoConstant.SINGLE_TRIP_COMP_TO_CLIENT,true))
                        {

                            /// section will continue SEND/RECEIVE SINGLE TRIP

                            binding.arrivedToReceiverSlideView!!.visibility = View.GONE
                            binding.deliveredOrderSlideView!!.visibility = View.VISIBLE
                            binding.backToSenderLayout!!.visibility = View.GONE
                            binding.tvFinishLayout!!.visibility = View.GONE

                            if(responseBodyRecived!!.data!!.equals(OGoConstant.DELIVERED_STRING))
                            {
                                preferenceHelper!!.orderStatus  = OGoConstant.DELIVERED
                                UpdateRealTimeDriverOrderStatus.updateRealTimeDriverOrderStatus(mActivity!!,OGoConstant.DELIVERED)

                                ////////ask for rating here
                                ///Now no need of rating in driver application
                                //EventBus.getDefault().post(GoRatingEvent(orderEntity))

                                finishOrder()
                            }else if(responseBodyRecived.data!!.equals(OGoConstant.COMPLETED_STRING))
                            {
                                Toast.makeText(mActivity, "order finished successfully ", Toast.LENGTH_SHORT).show()

                                preferenceHelper!!.orderStatus  = OGoConstant.COMPLETED
                                UpdateRealTimeDriverOrderStatus.updateRealTimeDriverOrderStatus(mActivity!!,OGoConstant.COMPLETED)



                                Looper.myLooper()?.let {
                                    Handler(it).postDelayed({
                                        // YOUR CODE after duration finished

                                        EventBus.getDefault().post(ListOrdersEvent())
                                    },OGoConstant.DELAY_HALF_SECOND)
                                }


                            }



                        }else
                        {

                            /// section will is for  SEND/RECEIVE RETURN TRIP

                            binding.arrivedToReceiverSlideView!!.visibility = View.GONE
                            binding.deliveredOrderSlideView!!.visibility = View.VISIBLE
                            binding.backToSenderLayout!!.visibility = View.GONE
                            binding.backToSenderLayout!!.visibility = View.GONE
                            binding.tvFinishLayout!!.visibility = View.GONE


                            if(responseBodyRecived!!.data!!.equals(OGoConstant.DELIVERED_STRING))
                            {
                                preferenceHelper!!.orderStatus  = OGoConstant.DELIVERED
                                UpdateRealTimeDriverOrderStatus.updateRealTimeDriverOrderStatus(mActivity!!,OGoConstant.DELIVERED)

                                var titleMsg:String
                                var message:String
                                var positiveButtonMsg:String
                                var negativeButtonMsg:String

                                titleMsg = getString(R.string.order_return_collected_title)
                                message = getString(R.string.order_return_collected_messege)
                                positiveButtonMsg = getString(R.string.order_return_collected_no)
                                negativeButtonMsg = getString(R.string.order_return_collected_yes)

                                showAlertDialogue(titleMsg,message,positiveButtonMsg,negativeButtonMsg,OGoConstant.RETURN_ORDER_COLLECTED)

                            } else if(responseBodyRecived.data!!.equals(OGoConstant.RETURN_ORDER_COLLECTED_STRING)){


                                preferenceHelper!!.orderStatus  = OGoConstant.RETURN_ORDER_COLLECTED
                                UpdateRealTimeDriverOrderStatus.updateRealTimeDriverOrderStatus(mActivity!!,OGoConstant.RETURN_ORDER_COLLECTED)
                                /// here go back to sender with new GUI
                                EventBus.getDefault().post(GoBackToSenderEvent(orderEntity!!))



                            }

                        }



                    } else {

                        Toast.makeText(mActivity, responseBodyRecived!!.message, Toast.LENGTH_LONG).show()

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

    fun finishOrder()
    {

        if (NetworkUtil.getInstance(mActivity!!).isOnline)
        {
            //////finish order here directly without taking ratings
            updateOrderStatusPost(preferenceHelper!!.loggedInUser!!.emailId!!,orderEntity!!.orderId!!,preferenceHelper!!.loggedInUser!!.cid!!, OGoConstant.COMPLETED)

        }else
        {
            NetworkUtil.getInstance(mActivity!!).showCustomNetworkError(mActivity!!)

        }






    }


    fun orderCollectedCase () {
        LogUtils.error(LogUtils.TAG,"Case3")
        binding.arrivedToReceiverSlideView!!.visibility = View.VISIBLE
        binding.deliveredOrderSlideView!!.visibility = View.GONE
        binding.backToSenderLayout!!.visibility = View.GONE
        binding.tvFinishLayout!!.visibility = View.GONE

    }

    fun driverAtDestinationCase () {
        LogUtils.error(LogUtils.TAG,"Case4")
        binding.arrivedToReceiverSlideView!!.visibility = View.GONE
        binding.deliveredOrderSlideView!!.visibility = View.VISIBLE
        binding.backToSenderLayout!!.visibility = View.GONE
        binding.tvFinishLayout!!.visibility = View.GONE
    }

    fun orderFinishCase () {
        LogUtils.error(LogUtils.TAG,"Case5")

        binding.arrivedToReceiverSlideView!!.visibility = View.GONE
        binding.deliveredOrderSlideView!!.visibility = View.GONE
        binding.backToSenderLayout!!.visibility = View.GONE
        binding.tvFinishLayout!!.visibility = View.VISIBLE
    }

    fun orderReturnCollectedCase () {

        LogUtils.error(LogUtils.TAG,"Case5")
        binding.arrivedToReceiverSlideView!!.visibility = View.GONE
        binding.deliveredOrderSlideView!!.visibility =   View.VISIBLE
        binding.backToSenderLayout!!.visibility = View.GONE
        binding.tvFinishLayout!!.visibility = View.GONE
    }


    fun orderBackToSenderCase () {

        LogUtils.error(LogUtils.TAG,"Case8")
        binding.arrivedToReceiverSlideView!!.visibility = View.GONE
        binding.deliveredOrderSlideView!!.visibility =   View.GONE
        binding.backToSenderLayout!!.visibility = View.VISIBLE
        binding.tvFinishLayout!!.visibility =  View.GONE
    }




    fun showAlertDialogue(titleMsg:String,message:String,positiveButtonMsg:String,negativeButtonMsg:String,orderStatus:String)
    {


        // vibrate the device
        DriverUtilities.vibrateDevice(requireContext(),100)

        OoOAlertDialog.Builder(mActivity)
            .setTitle(titleMsg)
            .setTitleColor(R.color.color_them)
            .setMessage(message)
            .setMessageColor(R.color.black)
            .setImage(R.drawable.order_delivered)
            .setAnimation(Animation.SIDE)
            .setPositiveButton(positiveButtonMsg, null)
            .setNegativeButton(negativeButtonMsg,  OnClickListener() {

                ////accept here OTP then update status to delivered
                //Toast.makeText(mActivity, "Ok", Toast.LENGTH_SHORT).show()

                run {
                    val mPlayer = MediaPlayer.create(mActivity, R.raw.door_entry_notification)
                    mPlayer.start()

                    LogUtils.error(LogUtils.TAG, "Status => mPlayer.start(); called : order finishing/completing confirmed!  ")
                }




                if (NetworkUtil.getInstance(mActivity!!).isOnline)
                {
                    updateOrderStatusPost(preferenceHelper!!.loggedInUser!!.emailId!!,orderEntity!!.orderId!!,preferenceHelper!!.loggedInUser!!.cid!!,orderStatus)

                }else
                {
                    NetworkUtil.getInstance(mActivity!!).showCustomNetworkError(mActivity!!)

                }



            })
            .build()

    }



}



