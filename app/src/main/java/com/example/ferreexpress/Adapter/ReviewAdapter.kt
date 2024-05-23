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
import com.example.ferreexpress.R
import com.example.ferreexpress.databinding.ViewholderProductBinding
import com.example.ferreexpress.databinding.ViewholderReviewBinding

class ReviewAdapter(private val comentaryList: ArrayList<ReviewDomain>): RecyclerView.Adapter<ReviewAdapter.Viewholder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        context = parent.context
        val binding = ViewholderReviewBinding.inflate(LayoutInflater.from(context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        holder.bind(comentaryList[position])
    }

    override fun getItemCount(): Int {
        return comentaryList.size
    }

    inner class Viewholder(private val binding: ViewholderReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(review: ReviewDomain) {
            binding.nameTxt.text = review.nameUser
            binding.descTxt.text = review.comentary
            binding.ratingTxt.text = review.rating.toString()

            // Manejo seguro de la imagen del usuario
            val picUrl = review.picUrl
            if (!picUrl.isNullOrEmpty()) {
                Glide.with(context)
                    .load(picUrl)
                    .apply(RequestOptions().transform(CenterCrop(), GranularRoundedCorners(8f, 8f, 8f, 8f)))
                    .into(binding.pic)
            } else {
                // Muestra una imagen predeterminada si picUrl es nulo o vacío
                binding.pic.setImageResource(R.drawable.account_icon)
            }
        }
    }

}