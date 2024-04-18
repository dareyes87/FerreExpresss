package com.example.ferreexpress.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ferreexpress.Activity.CategoryActivity
import com.example.ferreexpress.Domain.CategoryDomain
import com.example.ferreexpress.databinding.ViewholderCategoryBinding


class CategoryAdapter(private val items: ArrayList<CategoryDomain>, private val fragment: Fragment) : RecyclerView.Adapter<CategoryAdapter.Viewholder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val binding = ViewholderCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

            binding.categoryNameTextView.text = category.title

            Glide.with(binding.root.context)
                .load(category.picUrl) // Aquí se carga la imagen desde la URL
                .into(binding.pic) // Se coloca la imagen en el ImageView

            binding.root.setOnClickListener{
                val intent = Intent(fragment.requireContext(), CategoryActivity::class.java)
                when(category.id) {
                    0 -> {
                        Toast.makeText(fragment.requireContext(), "Herramientas", Toast.LENGTH_SHORT).show()
                        intent.putExtra(CategoryActivity.CATEGORY_EXTRA, "Herramientas")
                    }
                    1 -> {
                        Toast.makeText(fragment.requireContext(), "Tornillos", Toast.LENGTH_SHORT).show()
                        intent.putExtra(CategoryActivity.CATEGORY_EXTRA, "Tornillos")
                    }
                    2 -> {
                        Toast.makeText(fragment.requireContext(), "Seguridad", Toast.LENGTH_SHORT).show()
                        intent.putExtra(CategoryActivity.CATEGORY_EXTRA, "Seguridad")
                    }
                    3 -> {
                        Toast.makeText(fragment.requireContext(), "Herramientas Electricas", Toast.LENGTH_SHORT).show()
                        intent.putExtra(CategoryActivity.CATEGORY_EXTRA, "Herramientas Electricas")
                    }
                    4 -> {
                        Toast.makeText(fragment.requireContext(), "Maquinaria Pesada", Toast.LENGTH_SHORT).show()
                        intent.putExtra(CategoryActivity.CATEGORY_EXTRA, "Maquinaria Pesada")
                    }
                    5 -> {
                        Toast.makeText(fragment.requireContext(), "Servicios", Toast.LENGTH_SHORT).show()
                        intent.putExtra(CategoryActivity.CATEGORY_EXTRA, "Servicios")
                    }
                }
                fragment.startActivity(intent)
            }
        }
    }
}