package com.example.ferreexpress.Activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ferreexpress.Adapter.CartAdapter
import com.example.ferreexpress.Helper.ChangeNumberItemsListener
import com.example.ferreexpress.Helper.ManagmentCart
import com.example.ferreexpress.databinding.FragmentCartBinding

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var managmentCart: ManagmentCart
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        managmentCart = ManagmentCart(requireContext())
        initCartList()
        calculatorCart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initCartList() {
        if (managmentCart.getListCart().isEmpty()) {
            binding.emptyTxt.visibility = View.VISIBLE
            binding.scrollViewCart.visibility = View.GONE
        } else {
            binding.emptyTxt.visibility = View.GONE
            binding.scrollViewCart.visibility = View.VISIBLE
        }

        binding.cartView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.cartView.adapter =
            CartAdapter(managmentCart.getListCart(), requireContext(), object : ChangeNumberItemsListener {
                override fun changed() {
                    calculatorCart()
                }
            })
    }

    private fun calculatorCart() {
        val percentTax = 0.12
        val delivery = 10.0
        var tax = Math.round(managmentCart.totalFee * percentTax * 100.0) / 100.0

        val total =
            Math.round((managmentCart.totalFee + tax + delivery) * 100.0) / 100.0
        val itemTotal = Math.round(managmentCart.totalFee * 100.0) / 100.0

        binding.subtotalText.text = "Q$itemTotal"
        binding.ivaText.text = "Q$tax"
        binding.deliveryText.text = "Q$delivery"
        binding.totalText.text = "Q$total"
    }
}
