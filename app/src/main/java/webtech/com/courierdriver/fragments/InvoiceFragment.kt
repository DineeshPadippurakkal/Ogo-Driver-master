package webtech.com.courierdriver.fragments

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment



import net.vrgsoft.layoutmanager.RollingLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import webtech.com.courierdriver.R
import webtech.com.courierdriver.adapter.InvoiceOrdersAdapter
import webtech.com.courierdriver.communication.ApiPostService
import webtech.com.courierdriver.communication.ApiPostUtils
import webtech.com.courierdriver.communication.response.InvoiceOrderResp
import webtech.com.courierdriver.communication.response.InvoiceOrderRespData
import webtech.com.courierdriver.databinding.FragmentInvoiceBinding
import webtech.com.courierdriver.databinding.FragmentNearByOrdersBinding
import webtech.com.courierdriver.utilities.LogUtils
import webtech.com.courierdriver.utilities.PreferenceHelper
import java.io.IOException

/*
Created by ​
Hannure Abdulrahim


on 03/Dec/2019.
 */


class InvoiceFragment : Fragment() {


    var mActivity: Activity? = null
    private var preferenceHelper: PreferenceHelper? = null
    private var language: Int = 0
    private var driverUserId: String? = null
    private var apiPostService: ApiPostService? = null

    private var ordersAdapter: InvoiceOrdersAdapter? = null
    private val orderInvoiceEntities = ArrayList<InvoiceOrderRespData>()


    private lateinit var binding: FragmentInvoiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        if (arguments != null) {
            driverUserId = requireArguments().getString(DRIVER_EMAIL_ID)

        }



    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_invoice, container, false)


        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding= FragmentInvoiceBinding.bind(view)

        preferenceHelper = PreferenceHelper(mActivity!!)
        language = preferenceHelper!!.language



        val rollingLayoutManager =  RollingLayoutManager(mActivity)
        binding.invoiceRecyclerView!!.layoutManager =rollingLayoutManager!!
        ordersAdapter = InvoiceOrdersAdapter(orderInvoiceEntities)
        binding.invoiceRecyclerView!!.adapter = ordersAdapter

        /// call here invoice web api
        //ordersInVoicePost(driverUserId!!)
        ordersInVoicePost(preferenceHelper!!.loggedInUser!!.emailId!!)







    }


    /////////////////////////////////////S
    /*//Error If i rotate screen from portrait to landscape then it was getting crash hence restricted to portrait mode
     below two method will work*/

    override fun onResume() {
        super.onResume()
        mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        (mActivity as AppCompatActivity).getSupportActionBar()!!.setTitle(R.string.invoice)


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
        (mActivity as AppCompatActivity).getSupportActionBar()!!.setTitle("")



    }

    override fun onDestroy() {

        super.onDestroy()
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

        private val DRIVER_EMAIL_ID = "driver_email_id"



        fun newInstance(driverId: String): InvoiceFragment {
            val fragment = InvoiceFragment()
            val args = Bundle()
            args.putString(DRIVER_EMAIL_ID, driverId)

            fragment.arguments = args
            return InvoiceFragment()
        }
    }




    ///////Web Service call to get invoice
    fun ordersInVoicePost(userNameStr: String) {



        apiPostService = ApiPostUtils.apiPostService

        apiPostService!!.postInvoiceOrder(userNameStr).enqueue(object : Callback<InvoiceOrderResp> {

            override fun onResponse(call: Call<InvoiceOrderResp>, response: Response<InvoiceOrderResp>) {

                LogUtils.error(LogUtils.TAG, "response.raw().toString() =>" + response.raw().toString())

                if (response.isSuccessful) {

                    showInvoiceOrderResponse(response.body()!!.toString())

                    if (response.body()!!.status.toString().equals("true", true)) {
                        LogUtils.error(LogUtils.TAG, "postInvoiceOrder response.body()!!.message.toString() =>" + response.body()!!.message.toString())
                        //Toast.makeText(ctx, response.body()!!.message, Toast.LENGTH_LONG).show()


                        if(response.body()!!.data!!.isNotEmpty())
                        {
                            ///clear oder from here also

                            ///clear oder from here also
                            orderInvoiceEntities.clear()
                            orderInvoiceEntities.addAll(response.body()!!.data!!)
                            ordersAdapter!!.notifyDataSetChanged()

                            showTotalInvoiceAmount()



                        }else{

                            ///clear oder from here also
                            orderInvoiceEntities.clear()
                            ordersAdapter!!.notifyDataSetChanged()
                            binding.txtInvoiceTotal.visibility=View.GONE

                            Toast.makeText(mActivity, "No order invoice found.", Toast.LENGTH_LONG).show()


                        }



                    } else {

                        ///clear order from here also
                        orderInvoiceEntities.clear()
                        ordersAdapter!!.notifyDataSetChanged()

                        Toast.makeText(mActivity, response.body()!!.message, Toast.LENGTH_LONG).show()

                    }


                }
            }

            override fun onFailure(call: Call<InvoiceOrderResp>, t: Throwable) {

                if (t is IOException) {
                    Toast.makeText(mActivity, "this is an actual network failure :( inform the user and possibly retry", Toast.LENGTH_SHORT).show();
                    // logging probably not necessary
                }
                else {
                    Toast.makeText(mActivity, "conversion issue! big problems :(", Toast.LENGTH_SHORT).show();
                    // todo log to some central bug tracking service
                    Toast.makeText(mActivity, " Unable to submit postInvoiceOrder to API.!", Toast.LENGTH_LONG).show();
                }

                LogUtils.error("TAG", "Unable to submit postInvoiceOrder to API."+t.printStackTrace())
                LogUtils.error("TAG", "Unable to submit postInvoiceOrder to API.")

                // showProgress(false)

            }
        })


    }


    fun showInvoiceOrderResponse(response: String) {
        LogUtils.error("TAG>>>>RESPONSE>>>>", response)
    }




    fun showTotalInvoiceAmount() {

        if(orderInvoiceEntities!!.size>0)
        {
            binding.txtInvoiceTotal.visibility=View.VISIBLE

            var totalOrdersAmount = 0.0
            LogUtils.error(LogUtils.TAG, "orderInvoiceEntities total orders  =>" + orderInvoiceEntities.size)
            // tvPaymentCount.text = orderInvoiceEntities.size.toString()


            for(order in orderInvoiceEntities)
            {
                totalOrdersAmount = totalOrdersAmount + order.totalInvoiceAmount!!.toFloatOrNull()!!




            }




            LogUtils.error(LogUtils.TAG, "orderInvoiceEntities Total Orders Invoice Amount =>" + totalOrdersAmount)
            binding.txtInvoiceTotal.text = getString(R.string.total_invoice_amt) + String.format("%.3f", java.lang.Double.parseDouble(totalOrdersAmount.toString())) + " KD"



        }



    }









}