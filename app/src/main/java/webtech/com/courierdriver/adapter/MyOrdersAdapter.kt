package webtech.com.courierdriver.adapter

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
import webtech.com.courierdriver.communication.response.OrderHistoryResponseData
import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.events.OrderHistoryDetailEvent
import webtech.com.courierdriver.utilities.LogUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by darshanz on 10/9/16.
 */

class MyOrdersAdapter(orderEntities: ArrayList<OrderHistoryResponseData>) : RecyclerView.Adapter<MyOrdersAdapter.OrderViewHolder>() {

    private var orderEntities = ArrayList<OrderHistoryResponseData>()

    init {
        this.orderEntities = orderEntities
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_my_order_list_item, parent, false)

        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val orderEntity = orderEntities[position]


        //OrderRequest orderRequest = new Gson().fromJson(orderEntity.getJsonOrderDetails(), OrderRequest.class);


        var orderID = holder.orderId.getContext().getString(R.string.order_id)
        var total =holder.totalAmount.getContext().getString(R.string.order_history_total_amt)

        var orderStatusCancelled =holder.orderStatus.getContext().getString(R.string.order_history_cancelled)
        var orderStatusCompleted =holder.orderStatus.getContext().getString(R.string.order_history_completed)
        var orderStatusOngoing =holder.orderStatus.getContext().getString(R.string.order_history_ongoing)



        holder.orderId.setText(orderEntity.orderId!!)
        holder.source.setText(orderEntity!!.source)
        holder.totalAmount.setText(String.format("%.3f", java.lang.Double.parseDouble(orderEntity.totalFare!!.toString())) + " KD")

        if(orderEntity.order_status_int !=null)
        {
            if(orderEntity.order_status_int.toString().equals(OGoConstant.CANCEL,true) )
            {

                holder.orderStatus.setText(orderStatusCancelled)
                holder.orderStatus.setTextColor(Color.parseColor("#ff0000"))
                holder.orderStatusColor!!.setBackgroundResource(R.drawable.cancelled_my_order_list_item)



            }else if((orderEntity.order_status_int.toString().equals(OGoConstant.COMPLETED,true)) || orderEntity.order_status_int.toString().equals(OGoConstant.RETURN_ORDER_COMPLETED,true))
            {
                holder.orderStatus.setTextColor(Color.parseColor("#3CB371"))

                holder.orderStatus.setText(orderStatusCompleted)
                holder.orderStatusColor.setBackgroundResource(R.drawable.completed_my_order_list_item)

            }else
            {

                holder.orderStatus.setTextColor(Color.parseColor("#F4C700"))
                holder.orderStatus.setText(orderStatusOngoing)
                holder.orderStatusColor.setBackgroundResource(R.drawable.ongoing_my_order_list_item)


            }
        }



        //  holder.senderNameTV.setText(orderEntity.getSenderName());
        //  holder.receiverNameTV.setText(orderEntity.getReceiverName());


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

        var paymentType =holder.paymentType.getContext().getString(R.string.order_history_payment_type)

        holder.paymentType.setText(orderEntity.gatewayType!!)



        //if(orderEntity!!.typeOfOrder!!.equals(OGoConstant.SINGLE_TRIP_COMP_TO_CLIENT)|| orderEntity!!.typeOfOrder!!.equals(OGoConstant.SINGLE_TRIP_CLIENT_TO_COMP) )


        if (orderEntity!!.typeOfOrder!!.equals(OGoConstant.RETURN_TRIP_COMP_TO_CLIENT)|| orderEntity!!.typeOfOrder!!.equals(OGoConstant.RETURN_TRIP_CLIENT_TO_COMP) )
        {
            /// image is not coming from server ,use local assets
            Glide.with(holder.ivOrderTrip.context)
                    .load(R.drawable.trip_return)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)///don't cache
                    //.placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_image_preview)///if url exist but images not available on server
                    .into(holder.ivOrderTrip)


        }else{

                /// image is not coming from server ,use local assets
                Glide.with(holder.ivOrderTrip.context)
                        .load(R.drawable.trip_single_en)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)///don't cache
                        //.placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_image_preview)///if url exist but images not available on server
                        .into(holder.ivOrderTrip)



        }


        holder.row.setOnClickListener {
             EventBus.getDefault().post( OrderHistoryDetailEvent(orderEntity));
        }
    }

    override fun getItemCount(): Int {
        return orderEntities.size
    }

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // TextView senderNameTV;
        //  TextView receiverNameTV;

        internal var orderDate: EditText
        internal var source: EditText
        internal var orderId: EditText
        internal var orderStatus: EditText
        internal var totalAmount: EditText
        internal var paymentType: EditText

        internal var row: FrameLayout
        internal var orderStatusColor: TextView
        internal var ivOrderTrip: ImageView



        init {

            orderDate = itemView.findViewById<View>(R.id.orderDate) as EditText
            source = itemView.findViewById<View>(R.id.source) as EditText
            orderId = itemView.findViewById<View>(R.id.orderId) as EditText
            orderStatus = itemView.findViewById<View>(R.id.orderStatus) as EditText
            totalAmount = itemView.findViewById<View>(R.id.totalAmount) as EditText
            paymentType = itemView.findViewById<View>(R.id.paymentType) as EditText

            row = itemView.findViewById<View>(R.id.row) as FrameLayout
            // senderNameTV = (TextView) itemView.findViewById(R.id.senderNameTV);
            // receiverNameTV = (TextView) itemView.findViewById(R.id.receiverNameTV);


            orderStatusColor   = itemView.findViewById<View>(R.id.orderStatusColor) as TextView
            ivOrderTrip   = itemView.findViewById<View>(R.id.ivOrderTrip) as ImageView

        }
    }
}
