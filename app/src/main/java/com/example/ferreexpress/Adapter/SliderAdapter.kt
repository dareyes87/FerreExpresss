package com.example.ferreexpress.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.ferreexpress.Domain.SliderItems
import com.example.ferreexpress.R

class SliderAdapter(private val sliderItems: ArrayList<SliderItems>, private val viewPager2: ViewPager2) : RecyclerView.Adapter<SliderAdapter.SliderViewholder>() {

    private lateinit var context:Context
    private val runnable:Runnable = Runnable{
        sliderItems.addAll(sliderItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        @NonNull parent: ViewGroup,
        viewType: Int
    ): SliderViewholder {
        context=parent.context

        return SliderViewholder(LayoutInflater.from(context).inflate(R.layout.slide_item_container,parent,false))
    }

    override fun onBindViewHolder(holder: SliderViewholder, position: Int) {
        holder.setImage(sliderItems.get(position))
        if(position==sliderItems.size-2){
            viewPager2.post(runnable)
        }
    }

    override fun getItemCount(): Int {
        return sliderItems.size
    }

    inner class SliderViewholder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageSlide)
        fun setImage(sliderItems: SliderItems) {
            val requestOptions = RequestOptions().transform(CenterCrop())
            Glide.with(itemView.context)
                .load(sliderItems.getUrl())
                .apply(requestOptions)
                .into(imageView)
        }
    }

}