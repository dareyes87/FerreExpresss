package com.example.ferreexpress.Domain

import com.example.ferreexpress.model.Producto

data class Factura(
    val date: String,
    val deliveryLocation: String,
    val products: List<Producto>,
    val userId: String
)
