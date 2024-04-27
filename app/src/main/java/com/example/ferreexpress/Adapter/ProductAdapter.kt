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
import com.example.ferreexpress.Helper.OnProductClickListener
import com.example.ferreexpress.R
import com.example.ferreexpress.databinding.ViewholderProductBinding

class ProductAdapter(private val productList: ArrayList<itemsDomain>, private val isSeller: Boolean) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    private lateinit var context: Context
    private var productClickListener: OnProductClickListener? = null


    // ViewHolder para cada elemento de la lista
    inner class ViewHolder(private val binding: ViewholderProductBinding) : RecyclerView.ViewHolder(binding.root) {
        // Función para asignar datos a las vistas del ViewHolder
        fun bind(product: itemsDomain) {
            // Asigna los datos del producto a las vistas correspondientes


            binding.textName.text = product.title
            binding.reviews.text = "" + product.review
            binding.price.text = "Q" + product.price
            binding.rating.text = "(" + product.rating + ")"

            // Configuración de opciones de carga de imágenes con Glide
            val requestOptions = RequestOptions().transform(CenterCrop())

            // Carga la imagen del producto utilizando Glide
            if (product.picUrl.isNotEmpty()) {
                Glide.with(context)
                    .load(product.picUrl.get(0))
                    .apply(requestOptions)
                    .into(binding.productImage)
            } else {
                // Si la lista de URLs de imagen está vacía, muestra una imagen de error
                binding.productImage.setImageResource(R.drawable.noimage)
            }

            // Listener para el click en el elemento de la lista
            binding.root.setOnClickListener {
                // Abre la actividad de detalle del producto
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("object", product)
                intent.putExtra("isSeller", isSeller)
                intent.putExtra("keyProduct", product.keyProduct)
                context.startActivity(intent)
            }
        }
    }

    // Crea nuevos ViewHolders según sea necesario
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding = ViewholderProductBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    // Llena el ViewHolder con datos según la posición del elemento en la lista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }

}
