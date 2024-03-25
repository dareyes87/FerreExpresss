package com.example.ferreexpress.Activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ferreexpress.Adapter.CartAdapter
import com.example.ferreexpress.Helper.ChangeNumberItemsListener
import com.example.ferreexpress.Helper.ManagmentCart
import com.example.ferreexpress.R
import com.example.ferreexpress.databinding.ActivityCartBinding

class CartActivity : Home() {
    lateinit var binding: ActivityCartBinding
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

    private fun initCartList() {
        if(managmentCart.getListCart().isEmpty()){
            binding.emptyTxt.visibility = View.VISIBLE
            binding.scrollViewCart.visibility = View.GONE
        }else{
            binding.emptyTxt.visibility = View.GONE
            binding.scrollViewCart.visibility = View.VISIBLE
        }
        binding.cartView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.cartView.adapter = CartAdapter(managmentCart.getListCart(), this, object : ChangeNumberItemsListener {
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
        tax=Math.round((managmentCart.getTotalFee()*percentTax*100.0))/100.0

        var total: Double = Math.round((managmentCart.getTotalFee()+tax+delivery)*100.0)/100.0
        var itemTotal: Double = Math.round(managmentCart.getTotalFee()*100.0)/100.0

        binding.subtotalText.text = "Q"+itemTotal
        binding.ivaText.text = "Q"+tax
        binding.deliveryText.text = "Q"+delivery
        binding.totalText.text = "Q"+total

    }
}