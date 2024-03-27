package com.example.ferreexpress.Activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ferreexpress.Adapter.CartAdapter
import com.example.ferreexpress.Helper.ChangeNumberItemsListener
import com.example.ferreexpress.Helper.ManagmentCart
import com.example.ferreexpress.databinding.ActivityCartBinding

class CartActivity : Home() {
    private lateinit var binding: ActivityCartBinding
    var tax: Double = 0.0
    lateinit var managmentCart: ManagmentCart
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)


        calculatorCart()
        setVariable()
        initCartList()
    }

    // Inicializa la lista de elementos en el carrito
    private fun initCartList() {
        if(managmentCart.getListCart().isEmpty()){
            binding.emptyTxt.visibility = View.VISIBLE
            binding.scrollViewCart.visibility = View.GONE
        }else {
            binding.emptyTxt.visibility = View.GONE
            binding.scrollViewCart.visibility = View.VISIBLE
        }

            binding.cartView.layoutManager =
                LinearLayoutManager(this@CartActivity, LinearLayoutManager.VERTICAL, false)
            binding.cartView.adapter =
                CartAdapter(managmentCart.getListCart(), this@CartActivity, object : ChangeNumberItemsListener {
                    override fun changed() {
                        calculatorCart()
                    }
                })

    }

    private fun setVariable() {
        binding.backBtn.setOnClickListener({v -> finish()})
    }

    private fun calculatorCart() {
        var percentTax: Double = 0.12
        var delivery: Double = 10.0
        tax=Math.round((managmentCart.totalFee *percentTax*100.0))/100.0

        var total: Double = Math.round((managmentCart.totalFee +tax+delivery)*100.0)/100.0
        var itemTotal: Double = Math.round(managmentCart.totalFee *100.0)/100.0

        binding.subtotalText.text = "Q"+itemTotal
        binding.ivaText.text = "Q"+tax
        binding.deliveryText.text = "Q"+delivery
        binding.totalText.text = "Q"+total
    }
}