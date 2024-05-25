package com.example.ferreexpress.Helper

import android.content.Context
import com.google.gson.Gson
import android.widget.Toast
import com.example.ferreexpress.Domain.itemsDomain

class ManagmentCart(private val context: Context) {
    private val tinyDB: TinyDB

    init {
        tinyDB = TinyDB(context)
    }

    fun insertFood(item: itemsDomain) {
        val listfood = getListCart()
        var existAlready = false
        var n = 0
        for (y in listfood.indices) {
            if (listfood[y].title == item.title) { // No es necesario verificar si es nulo aquí
                existAlready = true
                n = y
                break
            }
        }
        if (existAlready) {
            listfood[n].numberinCart = item.numberinCart
        } else {
            listfood.add(item)
        }
        tinyDB.putListObject("CartList", listfood)
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show()
    }

    fun plusItem(
        listfood: ArrayList<itemsDomain>,
        position: Int,
        changeNumberItemsListener: ChangeNumberItemsListener
    ) {
        listfood[position].numberinCart = listfood[position].numberinCart + 1
        tinyDB.putListObject("CartList", listfood)
        changeNumberItemsListener.changed()
    }

    fun minusItem(
        listfood: ArrayList<itemsDomain>,
        position: Int,
        changeNumberItemsListener: ChangeNumberItemsListener
    ) {
        if (listfood[position].numberinCart == 1) {
            listfood.removeAt(position)
        } else {
            listfood[position].numberinCart = listfood[position].numberinCart - 1
        }
        tinyDB.putListObject("CartList", listfood)
        changeNumberItemsListener.changed()
    }

    val totalFee: Double
        get() {
            val listfood2 = getListCart()
            var fee = 0.0
            for (i in listfood2.indices) {
                fee += listfood2[i].price * listfood2[i].numberinCart
            }
            return fee
        }

    fun getListCart(): ArrayList<itemsDomain> {
        try {
            return tinyDB.getListObject("CartList")
        } catch (e: Exception) {
            e.printStackTrace()
            // Manejar la excepción aquí, como mostrar un mensaje de error o devolver una lista vacía
            return ArrayList()
        }
    }

    fun clearCart() {
        val cartList = ArrayList<itemsDomain>()
        val editor = context.getSharedPreferences("CART", Context.MODE_PRIVATE).edit()
        editor.putString("CART_LIST", Gson().toJson(cartList))
        editor.apply()
    }

}