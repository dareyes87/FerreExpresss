package com.example.ferreexpress.Activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDateTime
import com.example.ferreexpress.R

class pedidos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedidos)

    }


    data class Pedido(
        val id: Int,
        val fecha: LocalDateTime,
        val descripcion: String
    )

    data class Usuario(
        val id: Int,
        val nombre: String,
        val pedidos: List<Pedido>
    )


    fun obtenerPedidosRecientes(usuarios: List<Usuario>, limite: Int): List<Pedido> {
        val pedidosRecientes = usuarios.flatMap { it.pedidos }
            .sortedByDescending { it.fecha }
            .take(limite)
        return pedidosRecientes
    }

    fun main() {

        val pedidosUsuario1 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            listOf(
                Pedido(1, LocalDateTime.now().minusDays(1), "Pedido 1 de Usuario 1"),
                Pedido(2, LocalDateTime.now().minusDays(2), "Pedido 2 de Usuario 1")
            )
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        val pedidosUsuario2 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            listOf(
                Pedido(3, LocalDateTime.now().minusHours(5), "Pedido 1 de Usuario 2"),
                Pedido(4, LocalDateTime.now().minusDays(3), "Pedido 2 de Usuario 2")
            )
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        val usuario1 = Usuario(1, "Usuario 1", pedidosUsuario1)
        val usuario2 = Usuario(2, "Usuario 2", pedidosUsuario2)

        val usuarios = listOf(usuario1, usuario2)


        val pedidosRecientes = obtenerPedidosRecientes(usuarios, 3)


        pedidosRecientes.forEach { pedido ->
            println("Pedido ID: ${pedido.id}, Fecha: ${pedido.fecha}, Descripción: ${pedido.descripcion}")
        }
    }

}