package webtech.com.courierdriver.fragments

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle

import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import webtech.com.courierdriver.R
import webtech.com.courierdriver.adapter.OrdersAdapter
import webtech.com.courierdriver.communication.response.ReceiveCurrentOrderResponseData
import webtech.com.courierdriver.databinding.FragmentOrdersBinding
import webtech.com.courierdriver.databinding.FragmentOrdersRatingBinding
import webtech.com.courierdriver.utilities.LogUtils
import webtech.com.courierdriver.utilities.PreferenceHelper
import java.util.*


class OrdersFragment : Fragment() {


    private lateinit var binding: FragmentOrdersBinding

    private var ordersAdapter: OrdersAdapter? = null
    private val orderEntities = ArrayList<ReceiveCurrentOrderResponseData>()
    internal lateinit var acceptedOrderResponse: ReceiveCurrentOrderResponseData
    internal var oldOrderEntitiesSize: Int = 0

    //  private ProgressDialog dialog;
    private var preferenceHelper: PreferenceHelper? = null
    private var driverId: String? = null
    internal var oldItemcount: Int = 0
    //////////////////////s

    //////////////E

    var mActivity: Activity? = null
    private var acceptedOrder: ReceiveCurrentOrderResponseData? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        if (arguments != null) {
            acceptedOrder = requireArguments().getSerializable("DISPLAY_ACCEPTED_ORDER") as ReceiveCurrentOrderResponseData
            if (acceptedOrder != null)
                LogUtils.error(LogUtils.TAG, "OrdersFragment received order=>OrderId>:  " + acceptedOrder!!.orderId!!)


        }


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_orders, container, false)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding=FragmentOrdersBinding.bind(view)

        preferenceHelper = PreferenceHelper(mActivity!!)
        driverId = preferenceHelper!!.loggedInUser!!.id

        binding.recyclerViewOrder!!.layoutManager = LinearLayoutManager(mActivity!!)
        ordersAdapter = OrdersAdapter(orderEntities)
        oldItemcount = ordersAdapter!!.itemCount
        LogUtils.debug(LogUtils.TAG, "oldItemcount:" + Integer.toString(oldItemcount))

        binding.recyclerViewOrder!!.adapter = ordersAdapter
        //fetchOrders(false);
        if (acceptedOrder != null) {


            displayOrder(acceptedOrder!!)
        }


        //Record time every minute
        /* final Handler handler = new Handler();
       Runnable runnable = new Runnable() {
           @Override
           public void run() {
               if (isAdded()) {
                  fetchOrders(true);
                   handler.postDelayed(this, REFRESH_INTERVAL * 1000);
               }
           }
       };
       handler.postDelayed(runnable, REFRESH_INTERVAL * 1000);
        */



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

    override fun onDestroy() {
        super.onDestroy()
        if (activity != null)
            requireActivity().finish()
    }

    override fun onDetach() {
        super.onDetach()
        (mActivity as AppCompatActivity).getSupportActionBar()!!.setTitle("")
    }

    /////////////////////////////////////S
    /*//Error If i rotate screen from portrait to landscape then it was getting crash hence restricted to portrait mode
    below two method will work*/

    override fun onResume() {
        super.onResume()
        mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        (mActivity as AppCompatActivity).getSupportActionBar()!!.setTitle(R.string.orders)


    }

    override fun onPause() {
        super.onPause()
        mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

    }
    /////////////////////////////////////E


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        /* switch (item.getItemId()) {
           case R.id.action_settings:
               //fetchOrders(false);

               break;
       }*/

        return super.onOptionsItemSelected(item)
    }

    private fun displayOrder(acceptedOrderResponseParam: ReceiveCurrentOrderResponseData) {

        run {
            val mPlayer = MediaPlayer.create(mActivity, R.raw.store_door_one)
            mPlayer.start()

            LogUtils.error("MakanDriver:", "Status => mPlayer.start(); called : order already exist")
        }

        oldOrderEntitiesSize = orderEntities.size

        acceptedOrderResponse = acceptedOrderResponseParam

        if (orderEntities.size > 0) {

            /////here check order id with exiting order id,  if same dont add if not then only add
            if (orderEntities[0].orderId === acceptedOrderResponse.orderId) {
                ////ignore dont add same order
            } else {
                ////add different order only
                //orderEntities.add(acceptedOrderResponse);

            }

        } else {
            ///add here directly first order
            orderEntities.add(acceptedOrderResponse)

        }

        Collections.sort(orderEntities, CustomComparator())
        ordersAdapter!!.notifyDataSetChanged()


    }





    inner class CustomComparator : Comparator<ReceiveCurrentOrderResponseData> {
        override fun compare(o1: ReceiveCurrentOrderResponseData, o2: ReceiveCurrentOrderResponseData): Int {

            return o2.orderId!!.compareTo(o1.orderId!!)
        }
    }

    companion object {

        // public final static int REFRESH_INTERVAL = 5; //SECONDS
        val REFRESH_INTERVAL = 60 //60 sec

        ////////////////////////e

        //////////////S
        internal var isNewOrder = false

        fun newInstance(): OrdersFragment {
            return OrdersFragment()
        }
    }


}// Required empty public constructor
