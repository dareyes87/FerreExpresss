package com.example.ferreexpress.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ferreexpress.Adapter.ProductAdapter
import com.example.ferreexpress.Domain.ReviewDomain
import com.example.ferreexpress.Domain.itemsDomain
import com.example.ferreexpress.databinding.FragmentStoreBinding
import com.google.firebase.database.*
import java.util.Locale

class StoreFragment : Fragment() {

    private lateinit var binding: FragmentStoreBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var allProducts: ArrayList<itemsDomain>
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        database = FirebaseDatabase.getInstance()

        // Configurar RecyclerView
        binding.recyclerMyStore.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        productAdapter = ProductAdapter(ArrayList(), true)
        binding.recyclerMyStore.adapter = productAdapter

        // Configurar botón flotante
        binding.floatBtnAddProduct.setOnClickListener {
            // Agregar lógica para agregar un nuevo producto
            val intent = Intent(requireContext(), AddProductActivity::class.java)
            startActivity(intent)
        }

        // Inicializar productos
        initProducts()

        // Configurar búsqueda de productos
        binding.editTextBuscarEnStore.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No es necesario implementar esta función
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No es necesario implementar esta función
            }

            override fun afterTextChanged(s: Editable?) {
                // Filtrar productos según el texto ingresado por el usuario
                val searchText = s.toString().toLowerCase(Locale.getDefault())
                val filteredProducts = ArrayList<itemsDomain>()

                for (product in allProducts) {
                    if (product.title.toLowerCase(Locale.getDefault()).contains(searchText)) {
                        filteredProducts.add(product)
                    }
                }

                // Actualizar el RecyclerView con los productos filtrados
                productAdapter.setItems(filteredProducts)
                productAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun initProducts() {
        val myRef: DatabaseReference = database.reference.child("Users").child("UserID_1").child("products")
        val items: ArrayList<itemsDomain> = ArrayList()

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (issue in snapshot.children) {
                        val keyItem = issue.key.toString()
                        val itemData = issue.value as HashMap<*, *>

                        val title = itemData["title"] as String
                        val category = itemData["category"] as String
                        val description = itemData["description"] as String
                        val price = (itemData["price"] as Number?)?.toDouble() ?: 0.0
                        val oldPrice = (itemData["oldPrice"] as Number?)?.toDouble() ?: 0.0
                        val reviewCount = (itemData["review"] as Long?)?.toInt() ?: 0
                        val rating = (itemData["rating"] as Number?)?.toDouble() ?: 0.0
                        val numberinCart = (itemData["numberinCart"] as Long?)?.toInt() ?: 0
                        val picUrlList = itemData["picUrl"] as ArrayList<String>? ?: ArrayList()
                        val reviewsList = itemData["reviews"] as HashMap<String, Any>? ?: HashMap()
                        val reviews: ArrayList<ReviewDomain> = ArrayList()

                        for ((_, reviewData) in reviewsList) {
                            if (reviewData is HashMap<*, *>) {
                                val nameUser = reviewData["nameUser"] as String
                                val comentary = reviewData["comentary"] as String
                                val rating = (reviewData["rating"] as Double?) ?: 0.0
                                val review = ReviewDomain(nameUser, comentary, "", rating)
                                reviews.add(review)
                            }
                        }

                        val item = itemsDomain(
                            keyItem,
                            title,
                            category,
                            description,
                            picUrlList,
                            price,
                            oldPrice,
                            reviewCount,
                            rating,
                            numberinCart,
                            reviews
                        )
                        items.add(item)
                    }

                    allProducts = items
                    productAdapter.setItems(items)
                    productAdapter.notifyDataSetChanged()
                    binding.progressBarStore.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar error
            }
        })
    }
}