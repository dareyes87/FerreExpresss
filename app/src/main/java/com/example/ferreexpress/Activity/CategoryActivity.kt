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
import com.example.ferreexpress.Domain.ReviewDomain
import com.example.ferreexpress.Domain.itemsDomain
import com.example.ferreexpress.Helper.ItemsRepository
import com.example.ferreexpress.R
import com.example.ferreexpress.databinding.ActivityCategoryBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
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
        // Obtiene una referencia a la base de datos de Firebase
        val myRef: DatabaseReference = database.reference.child("Users")

        // Muestra la barra de progreso mientras se cargan los productos
        binding.progressBarSelectCategory.visibility = View.VISIBLE

        val items: ArrayList<itemsDomain> = ArrayList()

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val userProductsRef = userSnapshot.child("products")

                        // Itera sobre cada producto del usuario
                        for (productSnapshot in userProductsRef.children) {
                            val keyItem = productSnapshot.key.toString()
                            val itemData = productSnapshot.value as HashMap<*, *> // Cast a HashMap

                            val title = itemData["title"] as String
                            val productCategory = itemData["category"] as String // Cambio aquí
                            val description = itemData["description"] as String
                            val price = (itemData["price"] as Number?)?.toDouble() ?: 0.0
                            val oldPrice = (itemData["oldPrice"] as Number?)?.toDouble() ?: 0.0
                            val reviewCount = (itemData["review"] as Long?)?.toInt() ?: 0
                            val rating = (itemData["rating"] as Number?)?.toDouble() ?: 0.0
                            val numberinCart = (itemData["numberinCart"] as Long?)?.toInt() ?: 0

                            // Si los campos son de tipo ArrayList en tu objeto itemsDomain, asegúrate de manejarlos correctamente.
                            val picUrlList = itemData["picUrl"] as ArrayList<String>? ?: ArrayList()
                            val reviewsList = itemData["reviews"] as HashMap<String, Any>? ?: HashMap()

                            val reviews: ArrayList<ReviewDomain> = ArrayList()

                            // Iteramos sobre cada elemento del HashMap de reviews
                            for ((_, reviewData) in reviewsList) {
                                // Extraemos los datos de la revisión del HashMap
                                if (reviewData is HashMap<*, *>) {
                                    val nameUser = reviewData["nameUser"] as String
                                    val comentary = reviewData["comentary"] as String
                                    val rating = (reviewData["rating"] as Double?) ?: 0.0

                                    // ReviewDomain lo agregamos al ArrayList de revisiones
                                    val review = ReviewDomain(nameUser, comentary, "", rating)
                                    reviews.add(review)
                                }
                            }

                            // El ArrayList de revisiones al constructor de itemsDomain
                            val item = itemsDomain(
                                keyItem,
                                title,
                                productCategory, // Cambio aquí
                                description,
                                picUrlList,
                                price,
                                oldPrice,
                                reviewCount,
                                rating,
                                numberinCart,
                                reviews
                            )

                            // Verificar si el producto pertenece a la categoría deseada
                            if (productCategory == category) { // Cambio aquí
                                items.add(item)
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