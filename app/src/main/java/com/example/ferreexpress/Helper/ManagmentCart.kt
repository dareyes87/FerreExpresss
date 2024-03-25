package com.example.ferreexpress.Helper

import android.content.Context
import android.widget.Toast

import com.example.ferreexpress.Domain.itemsDomain
class ManagmentCart(private val context: Context) {
    private val tinyDB: TinyDB = TinyDB(context)

    fun insertFood(item: itemsDomain) {
        val listfood = getListCart()
        var existAlready = false
        var n = 0
        for (y in listfood.indices) {
            if (listfood[y].title == item.title) {
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

    fun getListCart(): ArrayList<itemsDomain> {
        return tinyDB.getListObject("CartList")
    }

    fun minusItem(listfood: ArrayList<itemsDomain>, position: Int, changeNumberItemsListener: () -> Unit) {
        if (listfood[position].numberinCart == 1) {
            listfood.removeAt(position)
        } else {
            listfood[position].numberinCart--
        }
        tinyDB.putListObject("CartList", listfood)
        changeNumberItemsListener()
    }

    fun plusItem(listfood: ArrayList<itemsDomain>, position: Int, changeNumberItemsListener: () -> Unit) {
        listfood[position].numberinCart++
        tinyDB.putListObject("CartList", listfood)
        changeNumberItemsListener()
    }

    fun getTotalFee(): Double {
        val listfood2 = getListCart()
        var fee = 0.0
        for (i in listfood2.indices) {
            fee += listfood2[i].price * listfood2[i].numberinCart
        }
        return fee
    }
}