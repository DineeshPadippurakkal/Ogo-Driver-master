package webtech.com.courierdriver.activity

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.*
import androidx.preference.PreferenceManager

import android.view.View
import android.view.animation.AnimationUtils


import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import com.agrawalsuneet.loaderspack.loaders.CircularSticksLoader
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

import webtech.com.courierdriver.communication.ApiPostService

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import webtech.com.courierdriver.OrderSingletonQueue.OrderSingletonQueue
import webtech.com.courierdriver.R

import webtech.com.courierdriver.communication.ApiPostUtils
import webtech.com.courierdriver.communication.DirectionApiPostService
import webtech.com.courierdriver.communication.DirectionApiPostUtils
import webtech.com.courierdriver.communication.response.AcceptedOrderResponse
import webtech.com.courierdriver.communication.NotificationResponseData.NotificationOrdersResponseData
import webtech.com.courierdriver.communication.response.direction.DirectionApiResponse

import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.databinding.ActivityArrivedOrderBinding
import webtech.com.courierdriver.databinding.ActivityMainBinding
import webtech.com.courierdriver.firebase.UpdateRealTimeDriverOrderStatus
import webtech.com.courierdriver.utilities.*
import java.io.IOException
import java.util.concurrent.TimeUnit


/*
Created by ​
Hannure Abdulrahim


on 8/30/2018.
 */

class ArrivedOrderActivity : MasterAppCombatActivity(), View.OnClickListener {


    private val timeCountInMilliSeconds = (1000 * 30).toLong()
    private var timerStatus = TimerStatus.STOPPED
    private var progressBarCircle: ProgressBar? = null
    private var textViewTime: TextView? = null

    private var senderTV: TextView? = null


    private var arrived_order_details_layout: LinearLayoutCompat? = null
    private var countDownTimer: CountDownTimer? = null
    internal var mediaPlayer: MediaPlayer? = null
    internal var currentMediaPlayerRes: Int = 0
    //private var jsonOrder: String? = null
    //private var arrivedOrderPojo:NotificationOrdersResponseData?=null

    //private var imageViewZoom: ImageView? = null
    internal var notificationOrdersResponseData: NotificationOrdersResponseData? = null
    private var apiPostService: ApiPostService? = null
    var preferenceHelper: PreferenceHelper? = null

    private var directionApiPostService: DirectionApiPostService? = null

    private enum class TimerStatus {
        STARTED,
        STOPPED
    }

    private lateinit var binding: ActivityArrivedOrderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//            setContentView(R.layout.activity_arrived_order)
        binding = ActivityArrivedOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // fixing portrait mode problem for SDK 26 if using windowIsTranslucent = true
        /*  if (Build.VERSION.SDK_INT == 26) {
              setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
          } else {
              setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
          }*/

        preferenceHelper = PreferenceHelper(this@ArrivedOrderActivity)
        ////if driver is busy don't display ArrivedOrderActivity
        if (preferenceHelper!!.busyStatus!!.equals(OGoConstant.FREE, true)) {
//                LogUtils.error(LogUtils.TAG, "ArrivedOrderActivity : ### DRIVER IS FREE ###")


        } else {
//                LogUtils.error(LogUtils.TAG, "ArrivedOrderActivity : ### DRIVER HAS ORDER###")
            finish()
        }

        playSound(R.raw.alert_sound, true)


        /*jsonOrder = this.intent.getStringExtra("ARRIVED_ORDER")

        // Convert JSON string back to Map.
        val gson = Gson()
        val type = object : TypeToken<Map<String, String>>() {

        }.type

        val mapOrder = gson.fromJson<Map<String, String>>(jsonOrder, type)


        ////converting  MAP to POJO
        val jsonElement = gson.toJsonTree(mapOrder)
        notificationOrdersResponseData = gson.fromJson(jsonElement, NotificationOrdersResponseData::class.java)

        ///print all keys
        for (String keyOrder : mapOrder.keySet()) {     LogUtils.error(LogUtils.TAG,"mapOrder.get = "+mapOrder.get(keyOrder));  }

        LogUtils.error(LogUtils.TAG, "sender_name=>" + mapOrder["sender_name"])

        */


        notificationOrdersResponseData =
            this.intent.getSerializableExtra("ARRIVED_ORDER") as? NotificationOrdersResponseData


        // method call to initialize the views
        initViews()
        ////set text
        setOrdersText(notificationOrdersResponseData)
        // method call to initialize the listeners
        initListeners()
        startStop()


        ///////////////////////Accept Order

        if (preferenceHelper!!.language == Language.ENGLISH)
            binding.arrivedOrderShimmerViewContainer.startShimmer()
        else {
            val startAnimation = AnimationUtils.loadAnimation(this, R.anim.blinking_animation)
            binding.acceptOrderSlideView.startAnimation(startAnimation)
        }


        binding.acceptOrderSlideView.setOnSlideCompleteListener {
            DriverUtilities.vibrateDevice(this@ArrivedOrderActivity, 100)
            stopCountDownTimer()
            binding.acceptOrderSlideView.visibility = View.INVISIBLE

            ////////////////////////////////
            ///loader animation
            val loader = CircularSticksLoader(
                this@ArrivedOrderActivity, 50, 60f, 20f,
                ContextCompat.getColor(this, R.color.colorLoader),
                ContextCompat.getColor(this, R.color.colorYellow),
                ContextCompat.getColor(this, R.color.color_them)
            )
                .apply {

                }
            binding.loadingLayout.addView(loader)
            ////////////////////////////////


            preferenceHelper?.let { preferenceHelper ->
                preferenceHelper.loggedInUser?.let { loginResposeData ->
                    notificationOrdersResponseData?.let { notificationOrdersResponseData ->

                        acceptOrdersRequestPost(
                            loginResposeData.emailId!!,
                            notificationOrdersResponseData!!.orderId!!,
                            loginResposeData.cid!!
                        )


                    }
                }
            }


            //////change text color and  Button Background Color
            //slideView!!.setTextColor(getResources().getColor(R.color.your_color))
            //val  myList =  ColorStateList(states, colors);
            //slideView!!.setButtonBackgroundColor(myList)


        }


    }

    override fun onResume() {
        super.onResume()
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //// check Version from firebase config file and proceed accordingly
        //CheckUpdateFromFireBaseConfig.checkAndShowUpdateAvailableAlert(this@ArrivedOrderActivity)

        ///Another way to save value in share preference
        PreferenceManager.getDefaultSharedPreferences(this@ArrivedOrderActivity).edit()
            .putBoolean("isActiveArrivedOrderActivity", true).apply()
    }

    override fun onPause() {
        super.onPause()
        ///these below two line will disable landscape mode ,check onResume() also
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        stopPlaying()
        ///Another way to save value in share preference
        PreferenceManager.getDefaultSharedPreferences(this@ArrivedOrderActivity).edit()
            .putBoolean("isActiveArrivedOrderActivity", false).apply()

    }


    override fun onDestroy() {

        super.onDestroy()
        stopPlaying()
        ///Another way to save value in share preference
        PreferenceManager.getDefaultSharedPreferences(this@ArrivedOrderActivity).edit()
            .putBoolean("isActiveArrivedOrderActivity", false).apply()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        stopCountDownTimer()
        stopPlaying()
        skipOrder()

    }

    /**
     * method to initialize the views
     */
    private fun initViews() {
        progressBarCircle = findViewById<View>(R.id.progressBarCircle) as ProgressBar
        textViewTime = findViewById<View>(R.id.textViewTime) as TextView
        // imageViewZoom = findViewById<View>(R.id.imageViewZoom) as ImageView
        arrived_order_details_layout =
            findViewById<View>(R.id.arrived_order_details_layout) as LinearLayoutCompat

        senderTV = findViewById<View>(R.id.senderTV) as TextView
        //sender_addressTV = findViewById<View>(R.id.sender_addressTV) as TextView
        //receiverTV = findViewById<View>(R.id.receiverTV) as TextView
        //receiver_addressTV = findViewById<View>(R.id.receiver_addressTV) as TextView

    }

    /**
     * method to initialize the click listeners
     */
    private fun initListeners() {
        progressBarCircle!!.setOnClickListener(this)
        binding.skipOrderLayout.setOnClickListener {

            skipOrder()


        }

    }


    private fun skipOrder() {

        ///
        OrderSingletonQueue.instance.notificationOrdersResponseDataList?.let { notificationOrdersResponseDataList ->

            ///we need at least two order to delete and to add from queue
            /// if order is 1 or 0 no need to delete or add from queue

            if (notificationOrdersResponseDataList.size > 1) {
                var topQueueOrder = notificationOrdersResponseDataList.get(0)

                //// just delete order from top and add it to at end of queue
                OrderSingletonQueue.instance.deleteOrder(OGoConstant.AT_TOP)
                //OrderSingletonQueue.instance.addOrderAt(notificationOrdersResponseData!!,OrderSingletonQueue.instance.notificationOrdersResponseDataList!!.size)
                //OR

                OrderSingletonQueue.instance.addOrderAt(
                    topQueueOrder!!,
                    notificationOrdersResponseDataList.size
                )

            }

        }

        finish()

    }


    /**
     * method to set text
     * @param jsonOrder
     */
    private fun setOrdersText(arrivedOrderPojo: NotificationOrdersResponseData?) {

        if (arrivedOrderPojo != null) {


//                LogUtils.error(LogUtils.TAG, "order_id via Model class objects =>" + notificationOrdersResponseData!!.orderId)
            //LogUtils.error(LogUtils.TAG,"order_id=>"+mapOrder.get("order_id"));
            if (notificationOrdersResponseData!!.orderId != null)
                binding.tvOrderId.setText(notificationOrdersResponseData!!.orderId!!)



            if (notificationOrdersResponseData!!.source != null)
                binding.tvSource.setText(notificationOrdersResponseData!!.source!!)

            if (notificationOrdersResponseData!!.typeOfOrder != null) {
                binding.tvTypesOfOrder!!.setText(getString(R.string.order_type) + notificationOrdersResponseData!!.typeOfOrder)

                if (notificationOrdersResponseData!!.typeOfOrder!!.equals(OGoConstant.RETURN_TRIP_COMP_TO_CLIENT) || notificationOrdersResponseData!!.typeOfOrder!!.equals(
                        OGoConstant.RETURN_TRIP_CLIENT_TO_COMP
                    )
                ) {
                    /// image is not coming from server ,use local assets
                    Glide.with(binding.ivOrderType.context)
                        .load(R.drawable.trip_return)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)///don't cache
                        //.placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_image_preview)///if url exist but images not available on server
                        .into(binding.ivOrderType)

                    binding.tvOrderType.text = binding.tvOrderType.context.getString(R.string.return_trip)
                } else {
                    /// image is not coming from server ,use local assets
                    Glide.with(binding.ivOrderType.context)
                        .load(R.drawable.trip_single_en)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)///don't cache
                        //.placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_image_preview)///if url exist but images not available on server
                        .into(binding.ivOrderType)

                    binding.tvOrderType.text = getString(R.string.single_trip)


                }

            }




            if (notificationOrdersResponseData!!.senderName != null)
                senderTV!!.text =
                    getString(R.string.sender) + " " + notificationOrdersResponseData!!.senderName

            if (notificationOrdersResponseData!!.senderLocation != null)
                binding.senderAddress!!.text =
                    getString(R.string.sender_address) + " " + notificationOrdersResponseData!!.senderLocation!!


            //if (notificationOrdersResponseData!!.senderLocation != null)
            // sender_addressTV!!.text = getString(R.string.sender_address) + notificationOrdersResponseData!!.senderLocation

//                if (notificationOrdersResponseData!!.receiverName != null)
//                    receiverTV!!.text = getString(R.string.receiver) + notificationOrdersResponseData!!.receiverName

            // if (notificationOrdersResponseData!!.receiverLocation != null)
            //  receiver_addressTV!!.text = getString(R.string.receiver_address) + notificationOrdersResponseData!!.receiverLocation

            // show here distance between driver and Sender
            // distance between driver and Receiver


            var lastLAT = preferenceHelper!!.lastLat
            var lastLONG = preferenceHelper!!.lastLong
            var serderLAT = notificationOrdersResponseData!!.senderLatitude
            var serderLONG = notificationOrdersResponseData!!.senderLongitute
            var receiverLAT = notificationOrdersResponseData!!.receiverLatitude
            var receiverLONG = notificationOrdersResponseData!!.receiverLongitute


//                LogUtils.error(LogUtils.TAG, ">>>> ArrivedOrderActivity :  ORDER_ID >>>>" + notificationOrdersResponseData!!.orderId)
//                LogUtils.error(LogUtils.TAG, ">>>> ArrivedOrderActivity :  lastLAT >>>>" + lastLAT)
//                LogUtils.error(LogUtils.TAG, ">>>> ArrivedOrderActivity : lastLONG >>>>" + lastLONG)
//
//                LogUtils.error(LogUtils.TAG, ">>>> ArrivedOrderActivity :  serderLAT >>>>" + serderLAT)
//                LogUtils.error(LogUtils.TAG, ">>>> ArrivedOrderActivity : serderLONG >>>>" + serderLONG)
//
//                LogUtils.error(LogUtils.TAG, ">>>> ArrivedOrderActivity : receiverLAT >>>>" + receiverLAT)
//                LogUtils.error(LogUtils.TAG, ">>>> ArrivedOrderActivity : receiverLONG >>>>" + receiverLONG)


            ////this will check not null also
            ////some time response come as "" which is "not null" so check other condition also
//                if ((serderLAT != null && !serderLAT.isEmpty()) && (serderLONG != null && !serderLONG.isEmpty()) && (receiverLAT != null && !receiverLAT.isEmpty()) && (receiverLONG != null && !receiverLONG.isEmpty()) && (lastLAT != null && !lastLAT.isEmpty()) && (lastLONG != null && !lastLONG.isEmpty())) {
//
//                    // 23/FEB/2019 Now we dont have to show dist and duration just show address
//                    //distanceAndDurationFromDriverToSenderPost(lastLAT, lastLONG, serderLAT, serderLONG, "false", "metric", "driving", ApiConstant.API_KEY)
//
//                    /// hide this now , driver are not accepting orders by looking this details
//                  ////// distanceAndDurationFromDriverToReceiverPost(lastLAT, lastLONG, receiverLAT, receiverLONG, "false", "metric", "driving", ApiConstant.API_KEY)
//
//
//
//    //                var fromDriverToSender = DistanceUtils.getDistance(lastLAT.toDoubleOrNull()!!,lastLONG.toDoubleOrNull()!!,serderLAT.toDoubleOrNull()!!,serderLONG.toDoubleOrNull()!!)
//    //
//    //                if(fromDriverToSender!!.size==2)
//    //                {
//    //
//    //
//    //
//    //                   // also from here both distance and duration
//    //                    LogUtils.error(LogUtils.TAG,">>>>fromDriverToSender.get(0) DISTANCE from driver to sender  >>>>"+fromDriverToSender!!.get(0))
//    //                    LogUtils.error(LogUtils.TAG,">>>>fromDriverToSender.get(1) DURATION> from driver to sender >>>"+fromDriverToSender!!.get(1))
//    //
//    //                    distance_value_sender.setText(fromDriverToSender!!.get(0))
//    //                    duration_value_sender.setText(fromDriverToSender!!.get(1))
//    //                }
//    //
//    //
//    //
//    //                    var fromDriverToReceiver = DistanceUtils.getDistance(lastLAT.toDoubleOrNull()!!,lastLONG.toDoubleOrNull()!!,receiverLAT.toDoubleOrNull()!!,receiverLONG.toDoubleOrNull()!!)
//    //                    if(fromDriverToReceiver!!.size==2)
//    //                    {
//    //                        //also from here both distance and duration
//    //                        LogUtils.error(LogUtils.TAG,">>>>OrdersDetails DISTANCE from driver to receiver >>>>"+fromDriverToReceiver!!.get(0))
//    //                        LogUtils.error(LogUtils.TAG,">>>>OrdersDetails DURATION from driver to receiver >>>>"+fromDriverToReceiver!!.get(1))
//    //
//    //                        distance_value_receiver.setText(fromDriverToReceiver!!.get(0))
//    //                        duration_value_receiver.setText(fromDriverToReceiver!!.get(1))
//    //
//    //                    }
//
//
//                }


            //////animate here this arrived order details layout
            //val startAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.blinking_animation)
            //arrived_order_details_layout!!.startAnimation(startAnimation)


        }


    }


    /**
     * implemented method to listen clicks
     *
     * @param view
     */
    override fun onClick(view: View) {
        when (view.id) {

            R.id.progressBarCircle -> {

                // acceptOrdersRequestPost(preferenceHelper!!.loggedInUser!!.emailId!!,notificationOrdersResponseData!!.orderId!!,preferenceHelper!!.loggedInUser!!.cid!!)
                //  stopCountDownTimer()

            }


        }/////// Here call web service
        //finish();
    }

    /**
     * method to reset count down timer
     */
    private fun reset() {
        stopCountDownTimer()
        startCountDownTimer()
    }


    /**
     * method to start and stop count down timer
     */
    private fun startStop() {
        if (timerStatus == TimerStatus.STOPPED) {

            // call to initialize the timer values
            //setTimerValues();
            // call to initialize the progress bar values
            setProgressBarValues()
            // showing the reset icon
            //imageViewReset.setVisibility(View.VISIBLE);
            // changing play icon to stop icon
            // imageViewStartStop.setImageResource(R.drawable.icon_stop);
            // making edit text not editable
            // editTextMinute.setEnabled(false);
            // changing the timer status to started
            timerStatus = TimerStatus.STARTED
            // call to start the count down timer
            startCountDownTimer()

        } else {

            // hiding the reset icon
            //imageViewReset.setVisibility(View.GONE);
            // changing stop icon to start icon
            // imageViewStartStop.setImageResource(R.drawable.icon_start);
            // making edit text editable
            //editTextMinute.setEnabled(true);
            // changing the timer status to stopped
            timerStatus = TimerStatus.STOPPED
            stopCountDownTimer()

        }

    }

    /**
     * method to initialize the values for count down timer
     */
    /*  private void setTimerValues() {
        int time = 0;
        if (!editTextMinute.getText().toString().isEmpty()) {
            // fetching value from edit text and type cast to integer
            time = Integer.parseInt(editTextMinute.getText().toString().trim());
        } else {
            // toast message to fill edit text
            Toast.makeText(getApplicationContext(), getString(R.string.message_minutes), Toast.LENGTH_LONG).show();
        }
        // assigning values after converting to milliseconds
        timeCountInMilliSeconds = time * 60 * 1000;
    }*/

    /**
     * method to start count down timer
     */
    private fun startCountDownTimer() {

        countDownTimer = object : CountDownTimer(timeCountInMilliSeconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                // textViewTime!!.text = hmsTimeFormatter(millisUntilFinished)
                textViewTime!!.text = msTimeFormatter(millisUntilFinished)

                progressBarCircle!!.progress = (millisUntilFinished / 1000).toInt()
                //val animation = AnimationUtils.loadAnimation(application, R.anim.zoom_in_animation)
                // imageViewZoom!!.startAnimation(animation)


            }

            override fun onFinish() {

                //  textViewTime!!.text = hmsTimeFormatter(timeCountInMilliSeconds)
                textViewTime!!.text = msTimeFormatter(timeCountInMilliSeconds)


                // call to initialize the progress bar values
                setProgressBarValues()
                // hiding the reset icon
                // imageViewReset.setVisibility(View.GONE);
                // changing stop icon to start icon
                // imageViewStartStop.setImageResource(R.drawable.icon_start);
                // making edit text editable
                //editTextMinute.setEnabled(true);
                // changing the timer status to stopped
                timerStatus = TimerStatus.STOPPED
                stopCountDownTimer()

                ///Toast.makeText(ArrivedOrderActivity.this,"This is toast message from inside onTick of the CountDownTimer inside the Service", Toast.LENGTH_LONG).show();
                //Toast.makeText(ArrivedOrderActivity.this,getString(R.string.order_rejected), Toast.LENGTH_LONG).show();
                finish()


            }

        }.start()
        countDownTimer!!.start()
    }

    /**
     * method to stop count down timer
     */
    private fun stopCountDownTimer() {
        countDownTimer!!.cancel()
    }

    /**
     * method to set circular progress bar values
     */
    private fun setProgressBarValues() {

        progressBarCircle!!.max = timeCountInMilliSeconds.toInt() / 1000
        progressBarCircle!!.progress = timeCountInMilliSeconds.toInt() / 1000
    }


    /**
     * method to convert millisecond to time format
     *
     * @param milliSeconds
     * @return HH:mm:ss time formatted string
     */
    private fun hmsTimeFormatter(milliSeconds: Long): String {

        return String.format(
            "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(milliSeconds),
            TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(
                    milliSeconds
                )
            ),
            TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    milliSeconds
                )
            )
        )


    }


    /**
     * method to convert millisecond to time format
     *
     * @param milliSeconds
     * @return mm:ss time formatted string
     */
    private fun msTimeFormatter(milliSeconds: Long): String {

        return String.format(
            "%02d",
            TimeUnit.MILLISECONDS.toSeconds(milliSeconds)
        )


    }

    private fun playSound(resId: Int, releaseAfter: Boolean) {
        if (currentMediaPlayerRes != resId || mediaPlayer == null) {
            if (mediaPlayer != null) {
                if (mediaPlayer!!.isPlaying)
                    mediaPlayer!!.stop()
                mediaPlayer!!.reset()
                mediaPlayer!!.release()
            }
            currentMediaPlayerRes = resId
            mediaPlayer = MediaPlayer.create(this, resId)
            if (releaseAfter) {
                mediaPlayer!!.setOnCompletionListener {
                    ///if you want to stop after completion
                    //mediaPlayer!!.release()
                    //mediaPlayer = null

                    ///if you want to start again after completion
                    mediaPlayer!!.start()

                }
            }
        }

        mediaPlayer!!.start()
    }

    private fun stopPlaying() {
        if (mediaPlayer != null) {
            // if (mediaPlayer!!.isPlaying)
            mediaPlayer!!.stop()

        }

    }


    /*
    *
    * Accept Order request
    *
    * */

    private fun acceptOrdersRequestPost(userNameStr: String, orderId: String, companyId: String) {
        LogUtils.error(LogUtils.TAG, "acceptOrdersRequestPost userNameStr=>" + userNameStr)
        LogUtils.error(LogUtils.TAG, "acceptOrdersRequestPost orderId=>" + orderId)
        LogUtils.error(LogUtils.TAG, "acceptOrdersRequestPost companyId=>" + companyId)
        apiPostService = ApiPostUtils.apiPostService

        //apiPostService!!.postAcceptOrdersRequest("abdul@gmail.com","OID-2021-THWN","2").enqueue(object : Callback<AcceptedOrderResponse> {
        apiPostService!!.postAcceptOrdersRequest(userNameStr, orderId, companyId)
            .enqueue(object : Callback<AcceptedOrderResponse> {

                override fun onResponse(
                    call: Call<AcceptedOrderResponse>,
                    response: Response<AcceptedOrderResponse>
                ) {

                    LogUtils.error(
                        LogUtils.TAG,
                        "response.raw().toString() =>" + response.raw().toString()
                    )

                    if (response.isSuccessful) {

                        showAcceptedOrderResponseonse(response.body()!!.toString())

                        if (response.body()!!.status.toString().equals("true", true)) {


                            /// firstly free our queue
                            OrderSingletonQueue.instance.notificationOrdersResponseDataList!!.clear()

                            LogUtils.error(
                                LogUtils.TAG,
                                "postAcceptOrdersRequest response.body()!!.message.toString() =>" + response.body()!!.message.toString()
                            )
                            LogUtils.error(
                                LogUtils.TAG,
                                "postAcceptOrdersRequest response.body()!!.data!!.orderId =>" + response.body()!!.data!!.orderId
                            )

                            Toast.makeText(
                                this@ArrivedOrderActivity,
                                getString(R.string.order_accepted),
                                Toast.LENGTH_LONG
                            ).show()



                            binding.acceptOrderSlideView.visibility = View.VISIBLE
                            binding.loadingLayout.removeAllViewsInLayout()
                            ////assign driver here
                            notificationOrdersResponseData!!.driverInfo =
                                response.body()!!.data!!.driverInfo!!

                            /////// update current order status here in share preference
                            /// value is not coming from server hence taken as hard coded
                            preferenceHelper!!.orderStatus = OGoConstant.DRIVER_ACCEPTED

                            preferenceHelper!!.busyStatus = OGoConstant.BUSY

                            ///....below value is pushing in fir base database on request of backend developer
                            preferenceHelper!!.fireBase_e5 = response.body()!!.data!!.cid

                            LogUtils.error(
                                LogUtils.TAG,
                                "postAcceptOrdersRequest response.body()!!.data!!.cid=>" + response.body()!!.data!!.cid
                            )

                            UpdateRealTimeDriverOrderStatus.updateRealTimeDriverOrderStatus(
                                this@ArrivedOrderActivity,
                                OGoConstant.DRIVER_ACCEPTED
                            )

                            Looper.myLooper()?.let {
                                Handler(it).postDelayed({

                                    // YOUR CODE after duration finished
                                    ////here order accepted
                                    val i =
                                        Intent(this@ArrivedOrderActivity, MainActivity::class.java)
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    //i.putExtra("ACCEPTED_ORDER", notificationOrdersResponseData)
                                    startActivity(i)


                                    /////finish activity after 1 sec.
                                    finish()


                                }, OGoConstant.ARRIVED_ORDER_DELAY!!.toLong())
                            }


                        } else {

                            Toast.makeText(
                                this@ArrivedOrderActivity,
                                response.body()!!.message,
                                Toast.LENGTH_LONG
                            ).show()
                            finish()

                        }


                    }
                }

                override fun onFailure(call: Call<AcceptedOrderResponse>, t: Throwable) {

                    if (t is IOException) {
                        Toast.makeText(
                            this@ArrivedOrderActivity,
                            "this is an actual network failure :( inform the user and possibly retry",
                            Toast.LENGTH_SHORT
                        ).show();
                        // logging probably not necessary
                    } else {
                        Toast.makeText(
                            this@ArrivedOrderActivity,
                            "conversion issue! big problems :(",
                            Toast.LENGTH_SHORT
                        ).show();
                        // todo log to some central bug tracking service
                        Toast.makeText(
                            this@ArrivedOrderActivity,
                            " Unable to submit postAcceptOrdersRequest to API.!",
                            Toast.LENGTH_LONG
                        ).show();
                    }

                    LogUtils.error(
                        "TAG",
                        "Unable to submit postAcceptOrdersRequest to API." + t.printStackTrace()
                    )
                    LogUtils.error("TAG", "Unable to submit postAcceptOrdersRequest to API.")


                    // showProgress(false)


                }
            })


    }

    fun showAcceptedOrderResponseonse(response: String) {
        LogUtils.error("TAG>>>>RESPONSE>>>>", response)
    }


    /*
   * web service call via retrofit
   *  google direction api call via retrofit
   * */


    private fun distanceAndDurationFromDriverToSenderPost(
        lastLAT: String,
        lastLONG: String,
        serderLAT: String,
        serderLONG: String,
        sensor: String,
        units: String,
        mode: String,
        key: String
    ) {

        directionApiPostService = DirectionApiPostUtils.directionApiPostService


        directionApiPostService!!.getDirectionsJson(
            lastLAT + "," + lastLONG,
            serderLAT + "," + serderLONG,
            sensor,
            units,
            mode,
            key
        ).enqueue(object : Callback<DirectionApiResponse> {
            override fun onResponse(
                call: Call<DirectionApiResponse>,
                response: Response<DirectionApiResponse>
            ) {

                //LogUtils.error(LogUtils.TAG, "response.raw().toString() =>" + response.raw().toString())

                //showDirectionApiResponse(response.body()!!.toString())


                if (response.isSuccessful) {

                    if (response!!.body()!! != null && !response.body()!!.toString().isEmpty()) {
                        var directionApiResponse = response.body()!!

                        ////How to get distance and duration
                        //// rout -> 0th position -> legs -> 0 th position (steps) -> distance / duration -> text

                        var distance = directionApiResponse.routes.get(0).legs.get(0).distance.text
                        var duration = directionApiResponse.routes.get(0).legs.get(0).duration.text

                        LogUtils.error(
                            LogUtils.TAG,
                            "distance in distanceAndDurationFromDriverToSenderPost() =>" + distance
                        )
                        LogUtils.error(
                            LogUtils.TAG,
                            "duration in distanceAndDurationFromDriverToSenderPost() =>" + duration
                        )


                        //  distance_value_sender.setText(distance)
                        //  duration_value_sender.setText(duration)


                    }


                } else {
                    LogUtils.error(LogUtils.TAG, "getDirectionsJson FAIL!! in ArrivedOrderActivity")

                }


            }

            override fun onFailure(call: Call<DirectionApiResponse>, t: Throwable) {

                LogUtils.error(
                    "TAG",
                    "Unable to submit getDirectionsJson to API. in ArrivedOrderActivity"
                )
                Toast.makeText(
                    this@ArrivedOrderActivity,
                    " Unable to submit getDirectionsJson to API.!",
                    Toast.LENGTH_LONG
                ).show();


            }
        })


    }


}




















