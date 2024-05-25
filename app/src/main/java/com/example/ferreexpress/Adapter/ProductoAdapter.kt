package com.example.ferreexpress.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ferreexpress.R
import com.example.ferreexpress.model.Producto

class ProductoAdapter(private val productos: List<Producto>) :
    RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    inner class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cantidad: TextView = itemView.findViewById(R.id.tvCantidad)
        val descripcion: TextView = itemView.findViewById(R.id.tvDescrip)
        val subtotal: TextView = itemView.findViewById(R.id.tvSubTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_facturaproduc, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.cantidad.text = producto.quantity.toString()
        holder.descripcion.text = producto.title
        holder.subtotal.text = (producto.price * producto.quantity).toString()
    }

    override fun getItemCount(): Int = productos.size
}
