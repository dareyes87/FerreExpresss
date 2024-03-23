package com.example.ferreexpress.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.annotation.NonNull
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.ferreexpress.Activity.DetailActivity
import com.example.ferreexpress.Domain.ReviewDomain
import com.example.ferreexpress.Domain.itemsDomain
import com.example.ferreexpress.databinding.ViewholderReviewBinding

class ReviewAdapter(private val items: ArrayList<ReviewDomain>): RecyclerView.Adapter<ReviewAdapter.Viewholder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewAdapter.Viewholder {
        context = parent.context
        val binding = ViewholderReviewBinding.inflate(LayoutInflater.from(context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: ReviewAdapter.Viewholder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class Viewholder(private val binding: ViewholderReviewBinding):
        RecyclerView.ViewHolder(binding.root){

        fun bind(review: ReviewDomain){
            binding.nameTxt.text = items.get(position).Name
            binding.descTxt.text = items.get(position).Description
            binding.ratingTxt.text = ""+items.get(position).rating

            Glide.with(context)
                .load(items.get(position).PicUrl)
                .transform(GranularRoundedCorners(100F, 100F, 100F, 100F))
                .into(binding.pic)
        }

    }

}