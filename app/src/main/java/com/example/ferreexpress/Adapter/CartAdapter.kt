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

    inner class Viewholder(private val binding: ViewholderCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cart: itemsDomain) {
            binding.titleTxt.text = listItemSelected.get(position).title
            binding.feeEachItem.text = "Q"+listItemSelected.get(position).price
            binding.totalEachItem.text = "Q"+Math.round(listItemSelected.get(position).numberinCart*listItemSelected.get(position).price)
            binding.numberItemTxt.text = listItemSelected[position].numberinCart.toString()

            val requestOptions = RequestOptions().transform(CenterCrop())

            Glide.with(itemView.context)
                .load(listItemSelected.get(position).picUrl.get(0))
                .apply(requestOptions)
                .into(binding.pic)

            binding.plusCartBtn.setOnClickListener {
                managmentCart.plusItem(listItemSelected, position) {
                    notifyDataSetChanged()
                    changeNumberItemsListener.changed()
                }
            }

            binding.minusCartBtn.setOnClickListener{
                managmentCart.minusItem(listItemSelected, position){
                    notifyDataSetChanged()
                    changeNumberItemsListener.changed()
                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartAdapter.Viewholder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ViewholderCartBinding.inflate(inflater, parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: CartAdapter.Viewholder, position: Int) {
        holder.bind(listItemSelected[position])
    }

    override fun getItemCount(): Int {
        return listItemSelected.size
    }


}