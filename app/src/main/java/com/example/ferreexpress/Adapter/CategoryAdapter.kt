package com.example.ferreexpress.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.ferreexpress.Domain.CategoryDomain
import com.example.ferreexpress.databinding.ViewholderCategoryBinding


class CategoryAdapter(private val items: ArrayList<CategoryDomain>) : RecyclerView.Adapter<CategoryAdapter.Viewholder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): Viewholder {
        context = parent.context
        val binding = ViewholderCategoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class Viewholder(private val binding: ViewholderCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: CategoryDomain) {
            //binding.title.text = items.get(position).titulo

            val requestOptions = RequestOptions().transform(CenterCrop())

            Glide.with(context)
                .load(items.get(position).picUrl)
                .apply(requestOptions)
                .into(binding.pic)
        }
    }

}