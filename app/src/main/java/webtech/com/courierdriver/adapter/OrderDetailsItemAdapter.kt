package webtech.com.courierdriver.adapter

import android.os.Build

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import webtech.com.courierdriver.R
import java.util.ArrayList




/*
Created by ​
Hannure Abdulrahim


on 11/5/2018.
 */

class OrderDetailsItemAdapter( imagesItems: ArrayList<String>): RecyclerView.Adapter<OrderDetailsItemAdapter.OrderItemViewHolder>() {

    val imagesItems: ArrayList<String>
    init {
        this.imagesItems = imagesItems
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_order_details_item_row, parent, false)
        return OrderItemViewHolder(view)
    }



    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {

        //OrderRequestEntity orderRequestEntity = items.get(position);

        var imageURL: String? = null

        imageURL = imagesItems[position]


        if (imageURL != null && imageURL.length > 0) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.roundedImageView.setClipToOutline(true)
            }
            Glide.with(holder.roundedImageView.context)
                    .load(imageURL)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)///don't cache
                    //.placeholder(R.drawable.ic_image_preview)
                    .error(R.drawable.ic_image_preview)///if url exist but images not available on server display this preview
                    .into(holder.roundedImageView)

        } else {

            /* Glide.with(holder.ivIcon.getContext())
                    .load(R.drawable.ic_image_preview)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .thumbnail(0.1f)
                    .into(holder.ivIcon);*/

        }


    }

    override fun getItemCount(): Int {
        //return items.size();
        return imagesItems.size
    }

    inner class OrderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var roundedImageView: ImageView

        init {

            roundedImageView = itemView.findViewById<View>(R.id.roundedImageView) as ImageView


        }
    }

}