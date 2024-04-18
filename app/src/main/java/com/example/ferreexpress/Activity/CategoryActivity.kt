package com.example.ferreexpress.Activity

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ferreexpress.Adapter.ProductAdapter
import com.example.ferreexpress.Domain.itemsDomain
import com.example.ferreexpress.R
import com.example.ferreexpress.databinding.ActivityCategoryBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CategoryActivity : AppCompatActivity() {
    var database:FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var binding: ActivityCategoryBinding

    companion object {
        const val CATEGORY_EXTRA = "category_extra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var w: Window = window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val category = intent.getStringExtra(CATEGORY_EXTRA)
        category?.let { initProducts(it) }

    }

    private fun initProducts(category: String){
        val items: ArrayList<itemsDomain> = ArrayList()
        val databaseReference = database.reference.child("Users")
        binding.progressBarSelectCategory.visibility = View.VISIBLE

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val userProductsRef = userSnapshot.child("products")
                        for (productSnapshot in userProductsRef.children) {
                            val product = productSnapshot.getValue(itemsDomain::class.java)
                            // Verificar si el producto pertenece a la categoría deseada
                            if (product?.category == category) {
                                product?.let { items.add(it) }
                            }
                        }
                    }
                    if (items.isNotEmpty()) {
                        binding.recyclerSelectCategory.layoutManager =
                            GridLayoutManager(this@CategoryActivity, 2, GridLayoutManager.VERTICAL, false)
                        binding.recyclerSelectCategory.adapter = ProductAdapter(items, false)
                    }
                    binding.progressBarSelectCategory.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar error
            }
        })
    }

}