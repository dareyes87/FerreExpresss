package com.example.ferreexpress.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ferreexpress.Domain.Factura
import com.example.ferreexpress.R

class FacturaAdapter(private val facturas: List<Factura>) :
    RecyclerView.Adapter<FacturaAdapter.FacturaViewHolder>() {

    inner class FacturaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val comprobante: TextView = itemView.findViewById(R.id.txtcomprobante)
        val productosRecyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView)
        val total: TextView = itemView.findViewById(R.id.tvTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacturaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_factura, parent, false)
        return FacturaViewHolder(view)
    }

    override fun onBindViewHolder(holder: FacturaViewHolder, position: Int) {
        val factura = facturas[position]
        holder.comprobante.text = "Fecha: ${factura.date}\nEntrega: ${factura.deliveryLocation}"

        val productoAdapter = ProductoAdapter(factura.products)
        holder.productosRecyclerView.apply {
            layoutManager = LinearLayoutManager(holder.itemView.context)
            adapter = productoAdapter
        }

        val totalAmount = factura.products.sumBy { it.price * it.quantity }
        holder.total.text = "Total: $${totalAmount}"
    }

    override fun getItemCount(): Int = facturas.size
}
