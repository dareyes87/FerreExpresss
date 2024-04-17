package com.example.ferreexpress.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.ferreexpress.Activity.DetailActivity
import com.example.ferreexpress.Domain.itemsDomain
import com.example.ferreexpress.databinding.ViewholderBestnoteListBinding

class PopularAdapter(
    private val items: ArrayList<itemsDomain>
): RecyclerView.Adapter<PopularAdapter.Viewholder>() {

    private lateinit var context: Context

    inner class Viewholder (private val binding: ViewholderBestnoteListBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(popular: itemsDomain){
            binding.titulotxt.text = items.get(position).title
            binding.reviewstxt.text = ""+items.get(position).review
            binding.pricetxt.text = "Q"+items.get(position).price
            binding.ratingtxt.text = "("+items.get(position).rating+")"
            binding.oldPricetxt.text = "Q"+items.get(position).oldPrice
            binding.oldPricetxt.paintFlags = binding.oldPricetxt.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            binding.ratingBar.rating = items.get(position).rating.toFloat()

            val requestOptions = RequestOptions().transform(CenterCrop())

            Glide.with(context)
                .load(items.get(position).picUrl.get(0))
                .apply(requestOptions)
                .into(binding.picture)

            binding.view.setOnClickListener(){
                var intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("object", items.get(position))//intent.putExtra("object", items[position])
                context.startActivity(intent)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularAdapter.Viewholder {
        context = parent.context
        val binding = ViewholderBestnoteListBinding.inflate(LayoutInflater.from(context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: PopularAdapter.Viewholder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

}