package com.example.ferreexpress.Activity



import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.ferreexpress.Adapter.ProductAdapter
import com.example.ferreexpress.Domain.ReviewDomain
import com.example.ferreexpress.Domain.itemsDomain

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.*
import com.example.ferreexpress.databinding.ActivityPedidosBinding
class pedidos : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var allProducts: ArrayList<itemsDomain>
    private lateinit var productAdapter: ProductAdapter
    private lateinit var binding: ActivityPedidosBinding
    private fun initProducts() {
        val myRef: DatabaseReference = database.reference.child("Users").child("UserID_1").child("favorites")
        val items: ArrayList<itemsDomain> = ArrayList()

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (issue in snapshot.children) {
                        val keyItem = issue.key.toString()
                        val itemData = issue.value as HashMap<*, *>

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
                            keyItem,
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
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar error
            }
        })
    }

}