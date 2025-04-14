package webtech.com.courierdriver.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import webtech.com.courierdriver.R

import webtech.com.courierdriver.communication.NotificationResponseData.NotificationNearByOrdersResponseData
import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.utilities.LogUtils
import java.text.SimpleDateFormat
import java.util.*

/*
Created by ​
Hannure Abdulrahim


on 11/4/2019.
 */


class NearByOrdersAdapter(orderEntities: ArrayList<NotificationNearByOrdersResponseData>?) : RecyclerView.Adapter<NearByOrdersAdapter.OrderViewHolder>() {

    private var nearByorderEntities = ArrayList<NotificationNearByOrdersResponseData>()
    init {
        this.nearByorderEntities = orderEntities!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearByOrdersAdapter.OrderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_near_by_order_list_item, parent, false)

        return OrderViewHolder(view)
    }



    override fun onBindViewHolder(holder: NearByOrdersAdapter.OrderViewHolder, position: Int) {
        val orderEntity = nearByorderEntities[position]

        var orderID = holder.orderId.getContext().getString(R.string.order_id)

        var orderStatusPending =holder.orderStatus.getContext().getString(R.string.near_by_orders_status)
        var sendeName = holder.orderId.getContext().getString(R.string.sender)

//        var total =holder.totalAmount.getContext().getString(R.string.order_history_total_amt)
//
//        var paymentType =holder.paymentType.getContext().getString(R.string.order_history_payment_type)


        holder.orderId.text =  orderID + orderEntity.orderId!!




        if(orderEntity.status !=null) {

            holder.orderStatus.text = orderStatusPending
            holder.orderStatus.setTextColor(Color.parseColor("#ff0000"))

        }




        holder.source.text = orderEntity!!.source
        holder.senderName.text = sendeName + orderEntity!!.senderName



//        holder.totalAmount.text = total + String.format("%.3f", java.lang.Double.parseDouble(orderEntity.totalFare!!.toString())) + " KD"
//        holder.paymentType.text = paymentType + orderEntity.gatewayType!!

        holder.orderArea.text = orderEntity!!.goToArea








}



    override fun getItemCount(): Int {
        return nearByorderEntities.size
    }


    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

      //  internal var orderDate: TextView
      internal var orderId: TextView
        internal var source: TextView
        internal var senderName: TextView

        internal var orderStatus: TextView
//        internal var totalAmount: TextView
//        internal var paymentType: TextView
        internal var orderArea: TextView



        init {
          //  orderDate = itemView.findViewById<View>(R.id.orderDate) as TextView
            orderId = itemView.findViewById<View>(R.id.orderId) as TextView
            source = itemView.findViewById<View>(R.id.source) as TextView
            senderName = itemView.findViewById<View>(R.id.senderName) as TextView

            orderStatus = itemView.findViewById<View>(R.id.orderStatus) as TextView
//            totalAmount = itemView.findViewById<View>(R.id.totalAmount) as TextView
//            paymentType = itemView.findViewById<View>(R.id.paymentType) as TextView
            orderArea = itemView.findViewById<View>(R.id.orderArea) as TextView

        }

    }


}
