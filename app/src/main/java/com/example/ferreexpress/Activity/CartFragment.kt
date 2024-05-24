package com.example.ferreexpress.Activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ferreexpress.Adapter.CartAdapter
import com.example.ferreexpress.Helper.ChangeNumberItemsListener
import com.example.ferreexpress.Helper.ManagmentCart
import com.example.ferreexpress.R
import com.example.ferreexpress.databinding.FragmentCartBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.ferreexpress.Domain.itemsDomain

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var managmentCart: ManagmentCart
    private lateinit var database: DatabaseReference

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
        database = FirebaseDatabase.getInstance().reference

        initCartList()
        calculatorCart()

        val sharedPref = requireActivity().getSharedPreferences(
            getString(R.string.prefs_file),
            AppCompatActivity.MODE_PRIVATE
        )
        val userID = sharedPref.getString("usuario", null)

        binding.checkOutBtn.setOnClickListener {
            processOrder(userID.toString())
        }
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
        val tax = Math.round(managmentCart.totalFee * percentTax * 100.0) / 100.0

        val total = Math.round((managmentCart.totalFee + tax + delivery) * 100.0) / 100.0
        val itemTotal = Math.round(managmentCart.totalFee * 100.0) / 100.0

        binding.subtotalText.text = "Q$itemTotal"
        binding.ivaText.text = "Q$tax"
        binding.deliveryText.text = "Q$delivery"
        binding.totalText.text = "Q$total"
    }

    private fun processOrder(userID: String) {
        // Datos de la orden
        val orderDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val deliveryLocation = "Dirección de entrega definida por el usuario" // Puedes cambiar esto según sea necesario

        // Crear una lista de productos comprados
        val cartList = managmentCart.getListCart()
        val products = cartList.map { cartItem ->
            mapOf(
                "productId" to cartItem.keyProduct,
                "title" to cartItem.title,
                "price" to cartItem.price,
                "quantity" to cartItem.numberinCart
            )
        }

        // Crear un objeto de la orden
        val order = mapOf(
            "userId" to userID,
            "date" to orderDate,
            "deliveryLocation" to deliveryLocation,
            "products" to products
        )

        // Obtener una nueva referencia para la orden
        val orderRef = database.child("Facturas").push()

        // Guardar la orden en la base de datos
        orderRef.setValue(order).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // La orden se guardó correctamente
                // Vaciar el carrito
                managmentCart.clearCart()
                // Actualizar la interfaz de usuario
                initCartList()
                calculatorCart()
                // Mostrar un mensaje de confirmación
                // Puedes usar un Toast, Snackbar, o cualquier otra forma de notificación
            } else {
                // Hubo un error al guardar la orden
                // Aquí puedes manejar el error, como mostrar un mensaje de error
            }
        }
    }

}
