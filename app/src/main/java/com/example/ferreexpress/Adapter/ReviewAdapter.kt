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

class ReviewAdapter(private val context: Context, private val items: List<ReviewDomain>): RecyclerView.Adapter<ReviewAdapter.Viewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val binding = ViewholderReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val review = items[position]
        holder.bind(review)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class Viewholder(private val binding: ViewholderReviewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(review: ReviewDomain) {
            binding.nameTxt.text = review.nameUser
            binding.descTxt.text = review.comentary
            binding.ratingTxt.text = review.rating.toString()

            Glide.with(context)
                .load(review.PicUrl)
                .transform(GranularRoundedCorners(100F, 100F, 100F, 100F))
                .into(binding.pic)
        }
    }

}