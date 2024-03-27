package com.example.ferreexpress.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.ferreexpress.Domain.itemsDomain
import com.example.ferreexpress.Helper.ChangeNumberItemsListener
import com.example.ferreexpress.Helper.ManagmentCart
import com.example.ferreexpress.databinding.ViewholderCartBinding

class CartAdapter(
    private val listItemSelected: ArrayList<itemsDomain>,
    private val context: Context,
    private val changeNumberItemsListener: ChangeNumberItemsListener
) : RecyclerView.Adapter<CartAdapter.Viewholder>() {

    private val managmentCart: ManagmentCart = ManagmentCart(context)
    //private lateinit var contxt: Context

    inner class Viewholder(private val binding: ViewholderCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cart: itemsDomain) {
            binding.titleTxt.text = cart.title
            binding.feeEachItem.text = "Q${cart.price}"
            binding.totalEachItem.text = "Q${Math.round(cart.numberinCart * cart.price)}"
            binding.numberItemTxt.text = cart.numberinCart.toString()

            val requestOptions = RequestOptions().transform(CenterCrop())

            Glide.with(context)
                .load(cart.picUrl[0])
                .apply(requestOptions)
                .into(binding.pic)

            val changeNumberItemsListener = object : ChangeNumberItemsListener {
                override fun changed() {
                    notifyDataSetChanged()
                    changeNumberItemsListener.changed()
                }
            }

            binding.plusCartBtn.setOnClickListener {
                managmentCart.plusItem(listItemSelected, adapterPosition, changeNumberItemsListener)
            }

            binding.minusCartBtn.setOnClickListener{
                managmentCart.minusItem(listItemSelected, adapterPosition, changeNumberItemsListener)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartAdapter.Viewholder {
        val binding = ViewholderCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: CartAdapter.Viewholder, position: Int) {
        holder.bind(listItemSelected[position])
    }

    override fun getItemCount(): Int {
        return listItemSelected.size
    }

}