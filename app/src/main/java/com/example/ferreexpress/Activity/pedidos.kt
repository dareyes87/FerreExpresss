package com.example.ferreexpress.Activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ferreexpress.Adapter.ProductAdapter
import com.example.ferreexpress.Domain.ReviewDomain
import com.example.ferreexpress.Domain.itemsDomain
import com.example.ferreexpress.databinding.ActivityPedidosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Pedidos : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var allProducts: ArrayList<itemsDomain>
    private lateinit var productAdapter: ProductAdapter
    private lateinit var binding: ActivityPedidosBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPedidosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        allProducts = ArrayList()

        userId = auth.currentUser?.uid ?: ""

        // Inicializar el adaptador antes de llamar a initProducts()
        productAdapter = ProductAdapter(allProducts, true)

        binding.recyclerFavoritos.layoutManager = LinearLayoutManager(this)
        binding.recyclerFavoritos.adapter = productAdapter

        // Aquí se configura el adaptador después de cargar los productos favoritos
        initProducts()
    }

    private fun initProducts() {
        val favoritesRef: DatabaseReference = database.reference.child("Users").child(userId).child("favorites").child("products")
        val items: ArrayList<itemsDomain> = ArrayList()

        favoritesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val favoriteProductIds = snapshot.children.mapNotNull { it.key }

                    loadFavoriteProducts(favoriteProductIds)
                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@Pedidos, "No hay productos favoritos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@Pedidos, "Error al cargar favoritos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadFavoriteProducts(favoriteProductIds: List<String>) {
        val items: ArrayList<itemsDomain> = ArrayList()

        favoriteProductIds.forEach { productId ->
            val productRef: DatabaseReference = database.reference.child("Products").child(productId)

            productRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(productSnapshot: DataSnapshot) {
                    if (productSnapshot.exists()) {
                        val itemData = productSnapshot.value as HashMap<*, *>

                        val title = itemData["title"] as String
                        val category = itemData["category"] as String
                        val description = itemData["description"] as String
                        val price = (itemData["price"] as Number?)?.toDouble() ?: 0.0
                        val oldPrice = (itemData["oldPrice"] as Number?)?.toDouble() ?: 0.0
                        val reviewCount = (itemData["review"] as Long?)?.toInt() ?: 0
                        val rating = (itemData["rating"] as Number?)?.toDouble() ?: 0.0
                        val numberinCart = (itemData["numberinCart"] as Long?)?.toInt() ?: 0
                        val picUrlList = itemData["picUrl"] as ArrayList<String>? ?: ArrayList()
                        val reviewsList = itemData["reviews"] as HashMap<String, Any>? ?: HashMap()
                        val reviews: ArrayList<ReviewDomain> = ArrayList()

                        for ((_, reviewData) in reviewsList) {
                            if (reviewData is HashMap<*, *>) {
                                val nameUser = reviewData["nameUser"] as String
                                val comentary = reviewData["comentary"] as String
                                val rating = (reviewData["rating"] as Double?) ?: 0.0
                                val review = ReviewDomain(nameUser, comentary, "", rating)
                                reviews.add(review)
                            }
                        }

                        val item = itemsDomain(
                            productId,
                            title,
                            category,
                            description,
                            picUrlList,
                            price,
                            oldPrice,
                            reviewCount,
                            rating,
                            numberinCart,
                            reviews
                        )
                        items.add(item)
                    }

                    allProducts = items
                    productAdapter.setItems(items)
                    productAdapter.notifyDataSetChanged()
                    binding.progressBar.visibility = View.GONE
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@Pedidos, "Error al cargar producto: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

}


