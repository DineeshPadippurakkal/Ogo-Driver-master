package webtech.com.courierdriver.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
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
import pl.droidsonroids.gif.GifImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import webtech.com.courierdriver.R
import webtech.com.courierdriver.adapter.MyOrdersAdapter
import webtech.com.courierdriver.communication.ApiPostService
import webtech.com.courierdriver.communication.ApiPostUtils
import webtech.com.courierdriver.communication.response.OrderHistoryResponse
import webtech.com.courierdriver.communication.response.OrderHistoryResponseData
import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.databinding.FragmentIndividualOrderHistoryBinding
import webtech.com.courierdriver.databinding.FragmentInvoiceBinding
import webtech.com.courierdriver.utilities.LogUtils
import java.io.IOException
import java.util.*


//import android.widget.ProgressBar;


class IndividualOrderHistoryFragment : Fragment() {

    var mActivity: Activity? = null

    //  @Bind(R.id.progressbar)
    // ProgressBar progressBar;

    internal lateinit var gifImageView: GifImageView

    private var driverId: String? = null
    private var driverUserId: String? = null
    private var paymentType: String? = null
    private var startYear: Int = 0
    private var startMonth = -1
    private var startDay: Int = 0
    private var endYear: Int = 0
    private var endMonth = -1
    private var endDay: Int = 0
    private var calendar: Calendar? = null
    private var paymentTypeString = OGoConstant.PAYMENT_TYPE_ALL
    private var orderStatusString = OGoConstant.ALL_STRING


    private var ordersAdapter: MyOrdersAdapter? = null
    private val orderHistoryEntities = ArrayList<OrderHistoryResponseData>()
    private var apiPostService: ApiPostService? = null


    private lateinit var binding: FragmentIndividualOrderHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            // driverId = arguments!!.getString(DRIVER_ID)
            driverUserId = requireArguments().getString(DRIVER_EMAIL_ID)

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_individual_order_history, container, false)



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentIndividualOrderHistoryBinding.bind(view)

        binding.txtTotal!!.visibility = View.GONE
        //set default as all
        binding.tvPaymentType.text = getString(R.string.my_orders_payment_types_all_display)
        paymentTypeString = OGoConstant.PAYMENT_TYPE_ALL

        binding.tvOrderStatusType.text = getString(R.string.my_orders_order_status_all_display)
        orderStatusString = OGoConstant.ALL_STRING


        // orderRecyclerView!!.layoutManager = LinearLayoutManager(mActivity)

        val rollingLayoutManager = RollingLayoutManager(mActivity)
        binding.orderRecyclerView!!.layoutManager = rollingLayoutManager!!
        ordersAdapter = MyOrdersAdapter(orderHistoryEntities)
        binding.orderRecyclerView!!.adapter = ordersAdapter

        showTotalOrdersDetails()


        // progressBar.setVisibility(View.GONE);
        //gifImageView.setVisibility(View.GONE);

        calendar = Calendar.getInstance()

/*
        spinnerPaymentType!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                val paymentTypes = resources.getStringArray(R.array.payment_types)
                if (i == 0) {
                    paymentType = null
                } else if (i == 1) {
                    paymentType = "0"
                } else {
                    paymentType = paymentTypes[i]
                }

            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }*/

        binding.PaymentTypeLayout.setOnClickListener {
            alertSingleChoiceItems()

        }

        binding.OrderStatusTypeLayout.setOnClickListener {
            alertSingleChoiceOrderStatus()

        }




        binding.StartDateLayout!!.setOnClickListener {
            resetSearchFeature()
            if (startYear == 0) startYear = calendar!!.get(Calendar.YEAR)
            if (startMonth == -1) startMonth = calendar!!.get(Calendar.MONTH)
            if (startDay == 0) startDay = calendar!!.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(mActivity!!, R.style.DatePickerDialogTheme, DatePickerDialog.OnDateSetListener { datePicker, i, i1, i2 ->
                startYear = i
                startMonth = i1
                startDay = i2
                binding.tvStartDate!!.text = i2.toString() + "/" + (i1 + 1) + "/" + i.toString()

                if (i2.toString().length < 2) {
                    ////conver single digit day to 2 digit day  (like 1,2,3 ...into 01,02,03...)
                    val formatedDay = "0" + i2.toString()
                    binding.tvStartDate!!.text = formatedDay + "/" + (i1 + 1) + "/" + i.toString()

                } else {
                    binding.tvStartDate!!.text = i2.toString() + "/" + (i1 + 1) + "/" + i.toString()
                }

            }, startYear, startMonth, startDay)

            datePickerDialog.show()
        }






        binding.EndDateLayout!!.setOnClickListener {

            resetSearchFeature()

            if (endYear == 0) endYear = calendar!!.get(Calendar.YEAR)
            if (endMonth == -1) endMonth = calendar!!.get(Calendar.MONTH)
            if (endDay == 0) endDay = calendar!!.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(mActivity!!, R.style.DatePickerDialogTheme, DatePickerDialog.OnDateSetListener { datePicker, i, i1, i2 ->
                endYear = i
                endMonth = i1
                endDay = i2

                if (i2.toString().length < 2) {
                    ////convert single digit day to 2 digit day  (like 1,2,3 ...into 01,02,03...)
                    val formatedDay = "0" + i2.toString()
                    binding.tvEndDate!!.text = formatedDay + "/" + (i1 + 1) + "/" + i.toString()

                } else {
                    binding.tvEndDate!!.text = i2.toString() + "/" + (i1 + 1) + "/" + i.toString()
                }


            }, endYear, endMonth, endDay)
            datePickerDialog.show()
        }



        binding.SearchLayout!!.setOnClickListener {

                /// date formate is
            ordersHistoryPost(driverUserId!!, binding.tvStartDate!!.text!!.toString(), binding.tvEndDate!!.text!!.toString(), paymentTypeString.toString(), orderStatusString)


            /* ///////////////////////////S   Graph view
            // val series = LineGraphSeries<DataPoint>(arrayOf<DataPoint>(DataPoint(0.0, 1.0), DataPoint(1.0, 5.0), DataPoint(2.0, 3.0), DataPoint(3.0, 2.0), DataPoint(4.0, 6.0)))
            // graph.addSeries(series)



             //////--------------------


             //val series = PointsGraphSeries(arrayOf<DataPoint>(DataPoint(0.toDouble(), -2.toDouble()), DataPoint(1.toDouble(), 5.toDouble()), DataPoint(2.toDouble(), 3.toDouble()), DataPoint(3.toDouble(), 2.toDouble()), DataPoint(4.toDouble(), 6.toDouble())))
            // graph.addSeries(series)
            // series.setShape(PointsGraphSeries.Shape.POINT)
             //////--------------------
             //val series2 = PointsGraphSeries<DataPoint>(arrayOf<DataPoint>(DataPoint(0.toDouble(), 1.toDouble()), DataPoint(1.toDouble(), 4.toDouble()), DataPoint(2.toDouble(), 2.toDouble()), DataPoint(3.toDouble(), 1.toDouble()), DataPoint(4.toDouble(), 5.toDouble())))
            // graph.addSeries(series2)
            // series2.setShape(PointsGraphSeries.Shape.RECTANGLE)
            // series2.setColor(Color.BLUE)

             //////--------------------
             val series = BarGraphSeries(arrayOf<DataPoint>(DataPoint(0.toDouble(), 3.toDouble()), DataPoint(1.toDouble(), 5.toDouble()), DataPoint(2.toDouble(), 3.toDouble()), DataPoint(3.toDouble(), 2.toDouble()), DataPoint(4.toDouble(), 6.toDouble()),DataPoint(5.toDouble(), 2.toDouble()),DataPoint(6.toDouble(), 1.toDouble()),DataPoint(7.toDouble(), 2.toDouble()),DataPoint(8.toDouble(), 2.toDouble()),DataPoint(9.toDouble(), 2.toDouble()),DataPoint(10.toDouble(), 2.toDouble()),DataPoint(11.toDouble(), 2.toDouble())))
             graph.addSeries(series)
             series.setValueDependentColor(object: ValueDependentColor<DataPoint> {
                 override fun get(data:DataPoint):Int {
                     return Color.rgb(data.getX().toInt() * 255 / 4, Math.abs(data.getY().toInt() * 255 / 6), 100)
                 }
             })
             //series.setSpacing(50)
             series.setDrawValuesOnTop(true)
             series.setValuesOnTopColor(Color.BLUE)
             //////--------------------


             val points = arrayOfNulls<DataPoint>(100)
             for (i in points.indices)
             {
                 points[i] = DataPoint(i.toDouble(), Math.sin(i * 0.5) * 20.0 * (Math.random() * 10 + 1))
             }
          val series = BarGraphSeries(points!!)
             graph.getViewport().setYAxisBoundsManual(true)
             graph.getViewport().setMinY(-150.toDouble())
             graph.getViewport().setMaxY(150.toDouble())
             graph.getViewport().setXAxisBoundsManual(true)
             graph.getViewport().setMinX(4.toDouble())
             graph.getViewport().setMaxX(80.toDouble())
             graph.getViewport().setScalable(true)
             graph.getViewport().setScalableY(true)
             graph.addSeries(series)

             graph.getViewport().setScrollable(true);
             //////--------------------
             //////--------------------
             //////--------------------
             //////--------------------
             //////--------------------

             ///////////////////////////E   Graph view*/


        }


    }

    override fun onResume() {
        super.onResume()
        mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        ///check on detach also removed title
        (mActivity as AppCompatActivity).getSupportActionBar()!!.setTitle(R.string.individual_orders)

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
        (mActivity as AppCompatActivity).getSupportActionBar()!!.setTitle("")

    }

    override fun onDestroy() {
        super.onDestroy()


    }

    companion object {
        //private val DRIVER_ID = "driver_id"
        private val DRIVER_EMAIL_ID = "driver_email_id"


        fun newInstance(driverId: String): IndividualOrderHistoryFragment {
            val fragment = IndividualOrderHistoryFragment()
            val args = Bundle()
            args.putString(DRIVER_EMAIL_ID, driverId)
            fragment.arguments = args
            return fragment
        }
    }


    ///////Web Service call to get order history
    fun ordersHistoryPost(userNameStr: String, fromDate: String, toDate: String, paymentType: String, orderStatusString: String) {
        LogUtils.error(LogUtils.TAG, "OrdersHistoryPost userNameStr=>" + userNameStr)
        LogUtils.error(LogUtils.TAG, "OrdersHistoryPost fromDate=>" + fromDate)
        LogUtils.error(LogUtils.TAG, "OrdersHistoryPost toDate=>" + toDate)
        LogUtils.error(LogUtils.TAG, "OrdersHistoryPost paymentType=>" + paymentType)
        LogUtils.error(LogUtils.TAG, "OrdersHistoryPost orderStatusString=>" + orderStatusString)


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
                            ///clear order from here also
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


                            showTotalOrdersDetails()


                        } else {

                            binding.row4!!.visibility = View.GONE
                            ///clear oder from here also
                            orderHistoryEntities.clear()
                            ordersAdapter!!.notifyDataSetChanged()
                            Toast.makeText(mActivity, "No order found.", Toast.LENGTH_LONG).show()


                        }


                    } else {

                        binding.row4!!.visibility = View.GONE
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


    fun alertSingleChoiceItems() {

        resetSearchFeature()


        val builder = AlertDialog.Builder(mActivity)
        // Set the dialog title
        builder.setTitle(getString(R.string.payment_types_title))
                // specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive call backs when items are selected
                // again, R.array.choices were set in the resources res/values/strings.xml
                .setSingleChoiceItems(R.array.choices, 0, object : DialogInterface.OnClickListener {

                    override fun onClick(arg0: DialogInterface, arg1: Int) {

                        //Toast.makeText(mActivity, "Some actions maybe? Selected index: " + arg1, Toast.LENGTH_SHORT).show()
                    }
                })
                // Set the action buttons
                .setPositiveButton(getString(R.string.login_ok), object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, id: Int) {
                        // user clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog

                        val selectedPosition = (dialog as AlertDialog).getListView().getCheckedItemPosition()
                        // Toast.makeText(mActivity, "selectedPosition: " + selectedPosition, Toast.LENGTH_SHORT).show()

                        when (selectedPosition) {

                            0 ->
                                if (selectedPosition == 0) {
                                    binding.tvPaymentType.text = getString(R.string.my_orders_payment_types_all_display)
                                    paymentTypeString = OGoConstant.PAYMENT_TYPE_ALL
                                }


                            1 ->
                                if (selectedPosition == 1) {
                                    binding.tvPaymentType.setText(getString(R.string.my_orders_payment_types_cod_display))
                                    paymentTypeString = OGoConstant.PAYMENT_TYPE_COD

                                }

                            2 ->
                                if (selectedPosition == 2) {
                                    binding.tvPaymentType.setText(getString(R.string.my_orders_payment_types_knet_display))
                                    paymentTypeString = OGoConstant.PAYMENT_TYPE_KNET
                                }

                            3 ->
                                if (selectedPosition == 3) {
                                    binding.tvPaymentType.setText(getString(R.string.my_orders_payment_types_master_display))
                                    paymentTypeString = OGoConstant.PAYMENT_TYPE_MASTER_CARD
                                }

                            4 ->
                                if (selectedPosition == 4) {
                                    binding.tvPaymentType.setText(getString(R.string.my_orders_payment_types_visa_card_display))
                                    paymentTypeString = OGoConstant.PAYMENT_TYPE_VISA_CARD
                                }

                            5 ->
                                if (selectedPosition == 5) {
                                    binding.tvPaymentType.setText(getString(R.string.my_orders_payment_types_credit_card_display))
                                    paymentTypeString = OGoConstant.PAYMENT_TYPE_CREDIT_CARD
                                }

//
//                            0->
//                                tvPaymentType.setText("ALL")
//                            1->
//                                tvPaymentType.setText("COD")
//                            2->
//                                tvPaymentType.setText("KNET")
//                            3->
//                                tvPaymentType.setText("MASTER_CARD")
//                            4->
//                                tvPaymentType.setText("VISA_CARD")
//
//                            5-> tvPaymentType.setText("CREDIT_CARD")

                        }

                    }
                })

                /*.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                    override    fun onClick(dialog: DialogInterface, id: Int) {
                        // removes the dialog from the screen

                    }
                })*/
                .show()
    }


    fun alertSingleChoiceOrderStatus() {

        resetSearchFeature()

        val builder = AlertDialog.Builder(mActivity)
        // Set the dialog title
        builder.setTitle(getString(R.string.payment_types_order_status))
                // specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive call backs when items are selected
                // again, R.array.choices were set in the resources res/values/strings.xml
                .setSingleChoiceItems(R.array.order_status_choices, 0, object : DialogInterface.OnClickListener {

                    override fun onClick(arg0: DialogInterface, arg1: Int) {

                        //Toast.makeText(mActivity, "Some actions maybe? Selected index: " + arg1, Toast.LENGTH_SHORT).show()
                    }
                })
                // Set the action buttons
                .setPositiveButton(getString(R.string.login_ok), object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, id: Int) {
                        // user clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog

                        val selectedPosition = (dialog as AlertDialog).getListView().getCheckedItemPosition()
                        // Toast.makeText(mActivity, "selectedPosition: " + selectedPosition, Toast.LENGTH_SHORT).show()

                        when (selectedPosition) {

                            0 ->
                                if (selectedPosition == 0) {
                                    binding.tvOrderStatusType.text = getString(R.string.my_orders_order_status_all_display)
                                    orderStatusString = OGoConstant.ALL_STRING
                                }


                            1 ->
                                if (selectedPosition == 1) {
                                    binding.tvOrderStatusType.setText(getString(R.string.my_orders_order_status_completed_display))
                                    orderStatusString = OGoConstant.COMPLETED_ORDER_STRING

                                }

                            2 ->
                                if (selectedPosition == 2) {
                                    binding.tvOrderStatusType.setText(getString(R.string.my_orders_order_status_cancelled_display))
                                    orderStatusString = OGoConstant.CANCEL_ORDER_STRING
                                }


                        }

                    }
                })

                /*.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                    override    fun onClick(dialog: DialogInterface, id: Int) {
                        // removes the dialog from the screen

                    }
                })*/
                .show()
    }


    fun showTotalOrdersDetails() {

        if (orderHistoryEntities!!.size > 0) {
            binding.row4!!.visibility = View.VISIBLE
            var totalOrdersAmount = 0.0
            LogUtils.error(LogUtils.TAG, "orderHistoryEntities total orders  =>" + orderHistoryEntities.size)
            // tvPaymentCount.text = orderHistoryEntities.size.toString()
            var totalCompletedOrdersCount = 0

            for (order in orderHistoryEntities) {
                if (order.order_status_int.equals(OGoConstant.COMPLETED) || order.order_status_int.equals(OGoConstant.RETURN_ORDER_COMPLETED)) {
                    totalOrdersAmount = totalOrdersAmount + order.totalFare!!.toFloatOrNull()!!
                    totalCompletedOrdersCount++

                }


            }

            binding.tvPaymentCount.text = totalCompletedOrdersCount.toString()


            LogUtils.error(LogUtils.TAG, "orderHistoryEntities Total Orders Amount =>" + totalOrdersAmount)
            binding.tvPaymentTotalAmount.text = String.format("%.3f", java.lang.Double.parseDouble(totalOrdersAmount.toString())) + " KD"
        } else {
            binding.row4!!.visibility = View.GONE

        }



    }


   private fun  resetSearchFeature()
   {
       binding.row4!!.visibility = View.GONE
       orderHistoryEntities.clear()
       ordersAdapter!!.notifyDataSetChanged()

   }



}


