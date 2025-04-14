package webtech.com.courierdriver.adapter

import android.content.Context
import android.os.Vibrator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import ng.max.slideview.SlideView
import org.greenrobot.eventbus.EventBus
import webtech.com.courierdriver.R
import webtech.com.courierdriver.communication.response.ReceiveCurrentOrderResponseData
import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.events.GoSenderEvent
import webtech.com.courierdriver.events.OrderDetailsEvent
import webtech.com.courierdriver.utilities.PreferenceHelper
import java.util.*

/**
 * Created by Abdulrahim Hannure on 02/Dec/2017.
 */

class OrdersAdapter(orderEntities: ArrayList<ReceiveCurrentOrderResponseData>) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    private var orderEntities = ArrayList<ReceiveCurrentOrderResponseData>()
    private var preferenceHelper: PreferenceHelper?=null
    private var ctx:Context?=null


    init {
        this.orderEntities = orderEntities
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        this.ctx = parent.context;
        preferenceHelper = PreferenceHelper(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_order_list_item, parent, false)

        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val ordersResponseData = orderEntities[position]



        ///Here we are getting order request
        // OrderRequest orderRequest = new Gson().fromJson(ordersResponseData.getJsonOrderDetails(), OrderRequest.class);


        holder.editOrderId.setText(ordersResponseData.orderId)

        holder.editSenderName.setText(ordersResponseData.senderName)
        holder.editSenderAddress.setText(ordersResponseData.senderLocation)
        //holder.editSenderPhone.setText(ordersResponseData.senderPhone)


       // if(ordersResponseData!!.typeOfOrder!!.equals(OGoConstant.SINGLE_TRIP_COMP_TO_CLIENT)|| ordersResponseData!!.typeOfOrder!!.equals(OGoConstant.SINGLE_TRIP_CLIENT_TO_COMP) )


        if (ordersResponseData!!.typeOfOrder!!.equals(OGoConstant.RETURN_TRIP_COMP_TO_CLIENT)|| ordersResponseData!!.typeOfOrder!!.equals(OGoConstant.RETURN_TRIP_CLIENT_TO_COMP) )
        {
            /// image is not coming from server ,use local assets
            Glide.with(holder.ivOrderType.context)
                    .load(R.drawable.trip_return)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)///don't cache
                    //.placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_image_preview)///if url exist but images not available on server
                    .into(holder.ivOrderType)

            holder.tvOrderType.text = holder.tvOrderType.context.getString(R.string.return_trip)
        } else  {
            /// image is not coming from server ,use local assets
            Glide.with(holder.ivOrderType.context)
                    .load(R.drawable.trip_single_en)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)///don't cache
                    //.placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_image_preview)///if url exist but images not available on server
                    .into(holder.ivOrderType)

            holder.tvOrderType.text = holder.tvOrderType.context.getString(R.string.single_trip)



        }








        holder.editReceiverName.setText(ordersResponseData.receiverName)
        holder.editReceiverAddress.setText(ordersResponseData.receiverLocation)
       // holder.editReceiverPhone.setText(ordersResponseData.receiverPhone)




       /* holder.editOrderId.setOnClickListener { EventBus.getDefault().post(OrderDetailsEvent(ordersResponseData)) }

        holder.editSenderName.setOnClickListener { EventBus.getDefault().post(OrderDetailsEvent(ordersResponseData)) }

        holder.editSenderAddress.setOnClickListener { EventBus.getDefault().post(OrderDetailsEvent(ordersResponseData)) }

       // holder.editSenderPhone.setOnClickListener { EventBus.getDefault().post(OrderDetailsEvent(ordersResponseData)) }


        holder.editReceiverName.setOnClickListener { EventBus.getDefault().post(OrderDetailsEvent(ordersResponseData)) }

        holder.editReceiverAddress.setOnClickListener { EventBus.getDefault().post(OrderDetailsEvent(ordersResponseData)) }

      //  holder.editReceiverPhone.setOnClickListener { EventBus.getDefault().post(OrderDetailsEvent(ordersResponseData)) }*/

        holder.transparentOverlaySender.setOnClickListener { EventBus.getDefault().post(OrderDetailsEvent(ordersResponseData)) }
        holder.transparentOverlayReceiver.setOnClickListener { EventBus.getDefault().post(OrderDetailsEvent(ordersResponseData)) }




        /* if(ordersResponseData.getStatus().equalsIgnoreCase(OrderStatusConstant.ORDER_BEING_COLLECTED)){
            Animation startAnimation = AnimationUtils.loadAnimation(holder.row.getContext(), R.anim.blinking_animation);
            holder.content_sender_layout.startAnimation(startAnimation);

            holder.btnGoRestaurant.setText("Click to resume the order");
            holder.btnGoRestaurant.setBackgroundColor(Color.RED);
            holder.btnGoRestaurant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new GoSenderEvent(ordersResponseData));
                }
            });
        } else  if(ordersResponseData.getStatus().equalsIgnoreCase(OrderStatusConstant.OUT_FOR_DELIVERY)) {

            Animation startAnimation = AnimationUtils.loadAnimation(holder.row.getContext(), R.anim.blinking_animation);
            holder.content_sender_layout.startAnimation(startAnimation);

            holder.btnGoRestaurant.setText("Click to resume the order");
            holder.btnGoRestaurant.setBackgroundColor(Color.RED);
            holder.btnGoRestaurant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new GoSenderEvent(ordersResponseData));
                }
            });
        }else if(ordersResponseData.getStatus().equalsIgnoreCase(OrderStatusConstant.ORDER_DELIVERD) ){
            Animation startAnimation = AnimationUtils.loadAnimation(holder.row.getContext(), R.anim.blinking_animation);
            holder.content_sender_layout.startAnimation(startAnimation);

            holder.btnGoRestaurant.setText("Click to resume the order");
            holder.btnGoRestaurant.setBackgroundColor(Color.RED);
            holder.btnGoRestaurant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new GoSenderEvent(ordersResponseData));
                }
            });
        }else
            {

            holder.btnGoRestaurant.setText("Click to go to Sender");
            holder.btnGoRestaurant.setBackgroundColor(holder.btnGoRestaurant.getContext().getResources().getColor(R.color.colorGreenButton));
            holder.btnGoRestaurant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //EventBus.getDefault().post(new ShowDirectionEvent(ordersResponseData.getData().getId(), ordersResponseData.getBranchId()));
                }
            });
        }
        */


        /* Animation startAnimation = AnimationUtils.loadAnimation(holder.row.getContext(), R.anim.blinking_animation);
        holder.goToRestaurantTV.startAnimation(startAnimation);

        holder.goToRestaurantTV.setText(R.string.go_to_sender);
        holder.goToRestaurantTV.setBackgroundColor(holder.goToRestaurantTV.getContext().getResources().getColor(R.color.colorGreenButton));
        holder.goToRestaurantTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //EventBus.getDefault().post(new ShowDirectionEvent(ordersResponseData.getData().getId(), ordersResponseData.getBranchId()));
            }
        });*/


        val startAnimation = AnimationUtils.loadAnimation(holder.row.context, R.anim.blinking_animation)
        holder.goToSenderSlideView.startAnimation(startAnimation)

        holder.goToSenderSlideView.setOnSlideCompleteListener { slideView ->
            ///// slideView.getContext().getSystemService ... in adapter
            ///// Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); /// in activity

            // vibrate the device
            val vibrator = slideView.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(100)

            EventBus.getDefault().post(GoSenderEvent(ordersResponseData))


        }





    }

    override fun getItemCount(): Int {
        return orderEntities.size
    }

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var editOrderId: EditText
        internal var editSenderName: EditText
        internal var editSenderAddress: EditText
       // internal var editSenderPhone: EditText

        internal var ivOrderType : ImageView
        internal var tvOrderType : TextView


        internal var editReceiverName: EditText
        internal var editReceiverAddress: EditText
       // internal var editReceiverPhone: EditText

        // TextView goToRestaurantTV;
        internal var goToSenderSlideView: SlideView
       // internal var finishOrderSlideView: SlideView

        internal var row: RelativeLayout
        internal var transparentOverlaySender: RelativeLayout
        internal var transparentOverlayReceiver: RelativeLayout



        init {

            row = itemView.findViewById<View>(R.id.row) as RelativeLayout
            transparentOverlaySender = itemView.findViewById<View>(R.id.transparentOverlaySender) as RelativeLayout
            transparentOverlayReceiver = itemView.findViewById<View>(R.id.transparentOverlayReceiver) as RelativeLayout


            editOrderId = itemView.findViewById<View>(R.id.editOrderId) as EditText

            editSenderName = itemView.findViewById<View>(R.id.editSenderName) as EditText
            editSenderAddress = itemView.findViewById<View>(R.id.editSenderAddress) as EditText

            ivOrderType = itemView.findViewById<View>(R.id.ivOrderType) as ImageView
            tvOrderType = itemView.findViewById<View>(R.id.tvOrderType) as TextView



           // editSenderPhone = itemView.findViewById<View>(R.id.editSenderPhone) as EditText

            editReceiverName = itemView.findViewById<View>(R.id.editReceiverName) as EditText
            editReceiverAddress = itemView.findViewById<View>(R.id.editReceiverAddress) as EditText
           // editReceiverPhone = itemView.findViewById<View>(R.id.editReceiverPhone) as EditText

            //goToRestaurantTV = (TextView) itemView.findViewById(R.id.GoToRestaurantTV);
            goToSenderSlideView = itemView.findViewById<View>(R.id.goToSenderSlideView) as SlideView
           // finishOrderSlideView = itemView.findViewById<View>(R.id.finishOrderSlideView) as SlideView
        }
    }



}
