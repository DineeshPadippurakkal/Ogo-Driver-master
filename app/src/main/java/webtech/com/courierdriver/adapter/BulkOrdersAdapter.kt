package webtech.com.courierdriver.adapter

/*
Created by ​
Hannure Abdulrahim


on 4/20/2020.
 */


import android.graphics.Color

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import org.greenrobot.eventbus.EventBus
import webtech.com.courierdriver.R
import webtech.com.courierdriver.communication.response.BulkOrderHistoryResponseData
import webtech.com.courierdriver.communication.response.OrderHistoryResponseData
import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.events.OrderHistoryDetailEvent
import webtech.com.courierdriver.utilities.LogUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by darshanz on 10/9/16.
 */

class BulkOrdersAdapter(orderEntities: ArrayList<BulkOrderHistoryResponseData>) : RecyclerView.Adapter<BulkOrdersAdapter.OrderViewHolder>() {


    private var orderEntities = ArrayList<BulkOrderHistoryResponseData>()

    init {
        this.orderEntities = orderEntities
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_bulk_order_list_item, parent, false)

        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val orderEntity = orderEntities[position]


        //OrderRequest orderRequest = new Gson().fromJson(orderEntity.getJsonOrderDetails(), OrderRequest.class);


        var orderID = holder.orderId.getContext().getString(R.string.order_id)
        var total =holder.totalAmount.getContext().getString(R.string.order_history_total_amt)

        var orderStatusCancelled = holder.orderStatusColor.getContext().getString(R.string.order_history_cancelled)
        var orderStatusCompleted = holder.orderStatusColor.getContext().getString(R.string.order_history_completed)
        var orderStatusOngoing = holder.orderStatusColor.getContext().getString(R.string.order_history_ongoing)



        holder.orderId.setText(orderEntity.bulkOrderId!!)

        holder.totalAmount.setText(String.format("%.3f", java.lang.Double.parseDouble(orderEntity.totalFare!!.toString())) + " KD")
        holder.orderNoOfParcel.setText(orderEntity.noOfParcel)


        if(orderEntity.orderStatusInt !=null)
        {
            if(orderEntity.orderStatusInt.toString().equals(OGoConstant.CANCEL,true) )
            {



                //holder.orderStatus.setTextColor(Color.parseColor("#ff0000"))
                holder.orderStatusColor!!.setBackgroundResource(R.drawable.cancelled_my_order_list_item)
                holder.orderStatusColor.setText(orderStatusCancelled)



            }else if((orderEntity.orderStatusInt.toString().equals(OGoConstant.COMPLETED,true)) || orderEntity.orderStatusInt.toString().equals(OGoConstant.RETURN_ORDER_COMPLETED,true))
            {



                //holder.orderStatus.setTextColor(Color.parseColor("#3CB371"))
                holder.orderStatusColor.setBackgroundResource(R.drawable.completed_my_order_list_item)
                holder.orderStatusColor.setText(orderStatusCompleted)

            }else
            {



               // holder.orderStatus.setTextColor(Color.parseColor("#F4C700"))

                holder.orderStatusColor.setBackgroundResource(R.drawable.ongoing_my_order_list_item)
                holder.orderStatusColor.setText(orderStatusOngoing)


            }
        }





        /////format from server is MM/dd/yyyy
        //holder.orderDate.text = orderEntity.dateOfOrder
        //LogUtils.error(LogUtils.TAG, "orderEntity.dateOfOrder###=>" + orderEntity.dateOfOrder)

        ///////////////////////////////////
        //String dob = "05/02/1989";  //its in MM/dd/yyyy
        var newFormattedOrderDate: String? = null
        val dtDob = Date(orderEntity.dateOfOrder)
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss aa")

        try {
            //new Formatted Order Date
            newFormattedOrderDate = sdf.format(dtDob)
            // LogUtils.error(LogUtils.TAG, "newFormattedOrderDate ###=>" + newFormattedOrderDate)

            holder.orderDate.setText(newFormattedOrderDate)

        } catch (e: Throwable) {
            LogUtils.error(LogUtils.TAG, "DATE is in wrong format ### =>" + orderEntity.dateOfOrder)

        }
        ///////////////////////////////////










        holder.row.setOnClickListener {
           // EventBus.getDefault().post( OrderHistoryDetailEvent(orderEntity))
        }
    }

    override fun getItemCount(): Int {
        return orderEntities.size
    }

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        internal var orderId: EditText
        internal var orderNoOfParcel: EditText

        internal var orderDate: EditText
        internal var totalAmount: EditText

        internal var row: FrameLayout
        internal var orderStatusColor: TextView




        init {
            orderId = itemView.findViewById<View>(R.id.orderId) as EditText
            orderNoOfParcel = itemView.findViewById<View>(R.id.orderNoOfParcel) as EditText
            orderDate = itemView.findViewById<View>(R.id.orderDate) as EditText
            totalAmount = itemView.findViewById<View>(R.id.totalAmount) as EditText

            row = itemView.findViewById<View>(R.id.row) as FrameLayout
            orderStatusColor   = itemView.findViewById<View>(R.id.orderStatusColor) as TextView


        }
    }
}
