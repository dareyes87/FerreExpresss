package com.example.ferreexpress.Activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ferreexpress.Adapter.FacturaAdapter
import com.example.ferreexpress.Domain.Factura
import com.example.ferreexpress.R
import com.example.ferreexpress.model.Producto
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FacturasActivity : AppCompatActivity() {

    private lateinit var facturasRecyclerView: RecyclerView
    private lateinit var facturaAdapter: FacturaAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facturas)

        val sharedPref = this.getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val userID = sharedPref.getString("usuario", null)

        facturasRecyclerView = findViewById(R.id.recyclerFacturasUser)
        facturasRecyclerView.layoutManager = LinearLayoutManager(this)

        database = FirebaseDatabase.getInstance().reference.child("Facturas")

        cargarFacturas(userID.toString())
    }

    // Método de ejemplo para cargar facturas
    private fun cargarFacturas(userID: String) {
        database.orderByChild("userId").equalTo(userID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val facturasList = mutableListOf<Factura>()
                    for (facturaSnapshot in dataSnapshot.children) {
                        val factura = facturaSnapshot.getValue(Factura::class.java)
                        factura?.let { facturasList.add(it) }
                    }
                    facturaAdapter = FacturaAdapter(facturasList)
                    facturasRecyclerView.adapter = facturaAdapter
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle possible errors
                }
            })
    }
}