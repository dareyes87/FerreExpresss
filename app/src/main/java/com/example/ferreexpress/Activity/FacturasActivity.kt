package com.example.ferreexpress.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ferreexpress.Adapter.FacturaAdapter
import com.example.ferreexpress.Domain.Factura
import com.example.ferreexpress.R
import com.example.ferreexpress.model.Producto

class FacturasActivity : AppCompatActivity() {

    private lateinit var facturasRecyclerView: RecyclerView
    private lateinit var facturaAdapter: FacturaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facturas)

        facturasRecyclerView = findViewById(R.id.recyclerFacturasUser)

        // Suponiendo que ya tienes una lista de facturas cargada
        val facturas: List<Factura> = cargarFacturas()

        facturaAdapter = FacturaAdapter(facturas)
        facturasRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@FacturasActivity)
            adapter = facturaAdapter
        }
    }

    // Método de ejemplo para cargar facturas
    private fun cargarFacturas(): List<Factura> {
        // Aquí deberías obtener las facturas de tu base de datos
        return listOf(
            Factura(
                "2024-05-24 16:19:28",
                "Dirección de entrega definida por el usuario",
                listOf(
                    Producto(525, "-Nyb-S07Fa7HqfU9cIgc", 3, "Botas Odin Armor")
                ),
                "GvftfEGB21ewuYR380GefxAidAo1"
            )
        )
    }
}
