package com.example.ferreexpress.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.ferreexpress.Activity.DetailActivity
import com.example.ferreexpress.Domain.itemsDomain
import com.example.ferreexpress.R
import com.example.ferreexpress.databinding.ViewholderProductBinding

class ProductAdapter(private val productList: List<itemsDomain>) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(private val binding: ViewholderProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: itemsDomain) {
            binding.textName.text = product.title
            binding.reviews.text = "" + product.review
            binding.price.text = "Q" + product.price
            binding.rating.text = "(" + product.rating + ")"

            val requestOptions = RequestOptions().transform(CenterCrop())

            if (product.picUrl.isNotEmpty()) {
                Glide.with(context)
                    .load(product.picUrl[0])
                    .apply(requestOptions)
                    .into(binding.productImage)
            } else {
                // Si la lista de URLs de imagen está vacía, muestra una imagen de error
                binding.productImage.setImageResource(R.drawable.noimage)
            }

            binding.root.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("object", product)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding = ViewholderProductBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}
