package webtech.com.courierdriver.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import webtech.com.courierdriver.R
import webtech.com.courierdriver.communication.response.InvoiceOrderRespData
import webtech.com.courierdriver.utilities.LogUtils
import java.text.SimpleDateFormat
import java.util.*

/*
Created by ​
Hannure Abdulrahim


on 12/3/2019.
 */



class InvoiceOrdersAdapter(orderEntities: ArrayList<InvoiceOrderRespData>) : RecyclerView.Adapter<InvoiceOrdersAdapter.OrderViewHolder>() {

    private var orderEntities = ArrayList<InvoiceOrderRespData>()

    init {
        this.orderEntities = orderEntities
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_invoice_list_item, parent, false)

        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val orderEntity = orderEntities[position]




        var orderIDStr = holder.orderId.getContext().getString(R.string.order_id)
        holder.orderId.text =  orderIDStr + orderEntity.orderId!!

        var orderDateStr = holder.orderDate.getContext().getString(R.string.invoice_pick_date)
        holder.orderDate.text =  orderDateStr + orderEntity.pickUpDate!!

        var senderNameStr = holder.senderName.getContext().getString(R.string.sender)
        holder.senderName.text =  senderNameStr + orderEntity.senderName!!

        var senderAddressStr = holder.senderAddress.getContext().getString(R.string.sender_address)
        holder.senderAddress.text =  senderAddressStr + orderEntity.senderLocation!!

        var invoiceAmtStr = holder.invoiceAmt.getContext().getString(R.string.invoice_amt)
        //holder.invoiceAmt.text =  invoiceAmt + orderEntity.totalInvoiceAmount!!
        holder.invoiceAmt.text = invoiceAmtStr + String.format("%.3f", java.lang.Double.parseDouble(orderEntity.totalInvoiceAmount!!.toString())) + " KD"








    }

    override fun getItemCount(): Int {
        return orderEntities.size
    }

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {




        internal var orderId: TextView
        internal var orderDate: TextView

        internal var senderName: TextView
        internal var senderAddress: TextView
        internal var invoiceAmt: TextView







        init {

            orderDate = itemView.findViewById<View>(R.id.orderDate) as TextView
            orderId = itemView.findViewById<View>(R.id.orderId) as TextView
            senderName = itemView.findViewById<View>(R.id.senderName) as TextView

            senderAddress = itemView.findViewById<View>(R.id.senderAddress) as TextView
            invoiceAmt = itemView.findViewById<View>(R.id.invoiceAmt) as TextView




        }
    }
}




