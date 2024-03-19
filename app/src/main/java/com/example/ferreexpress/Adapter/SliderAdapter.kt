package com.example.ferreexpress.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.ferreexpress.Domain.SliderItems
import org.jetbrains.annotations.NotNull
import com.example.ferreexpress.R
class SliderAdapter(private val sliderItems: ArrayList<SliderItems>, private val viewPager2: ViewPager2) : RecyclerView.Adapter<SliderAdapter.SliderViewholder>() {

    private lateinit var context:Context
    private var runnable:Runnable = Runnable(){
        sliderItems.addAll(sliderItems)
        notifyDataSetChanged()
    }

    inner class SliderViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageSlide)
        fun setImage(sliderItems: SliderItems){

        }
    }

    override fun onCreateViewHolder(
        @NotNull parent: ViewGroup,
        viewType: Int
    ): SliderAdapter.SliderViewholder {
        context=parent.context

        return SliderViewholder(LayoutInflater.from(context).inflate(R.layout.slide_item_container,parent,false))
    }

    override fun onBindViewHolder(holder: SliderAdapter.SliderViewholder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return sliderItems.size
    }

}