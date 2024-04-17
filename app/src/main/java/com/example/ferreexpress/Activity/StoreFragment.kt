package com.example.ferreexpress.Activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ferreexpress.Adapter.ProductAdapter
import com.example.ferreexpress.Domain.itemsDomain
import com.example.ferreexpress.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StoreFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productList: MutableList<itemsDomain>

    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_store, container, false)

        recyclerView = view.findViewById(R.id.recyclerMyStore)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        productList = mutableListOf()
        productAdapter = ProductAdapter(productList)
        recyclerView.adapter = productAdapter

        // Inicializa la referencia a la base de datos
        databaseReference = FirebaseDatabase.getInstance().reference.child("Users").child("UserID_1").child("products")

        // Escucha los cambios en la lista de productos del primer usuario
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tempList: MutableList<itemsDomain> = mutableListOf()
                for (productSnapshot in dataSnapshot.children) {
                    val product = productSnapshot.getValue(itemsDomain::class.java)
                    product?.let {
                        tempList.add(it)
                    }
                }
                productList.clear()
                productList.addAll(tempList)
                productAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejo de errores
            }
        })

        val fab: FloatingActionButton = view.findViewById(R.id.floatBtnAddProduct)
        fab.setOnClickListener{
            val intent = Intent(requireContext(), AddProductActivity::class.java)
            startActivity(intent)
        }
        // Inflate the layout for this fragment
        return view
    }

}