package com.example.ferreexpress.Helper

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ItemsRepository {

    private val database: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().reference
    }

    fun getProductId(userId: String, callback: (String?) -> Unit) {
        val userProductsRef = database.child("Users").child(userId).child("products")

        userProductsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var productId: String? = null
                for (productSnapshot in dataSnapshot.children) {
                    productId = productSnapshot.key
                    // Solo necesitamos el primer ID de producto, así que salimos del bucle
                    break
                }
                callback(productId)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejo de errores
                callback(null)
            }
        })
    }
}
