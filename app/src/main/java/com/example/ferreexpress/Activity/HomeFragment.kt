package com.example.ferreexpress.Activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.ferreexpress.Adapter.CategoryAdapter
import com.example.ferreexpress.Adapter.ProductAdapter
import com.example.ferreexpress.Adapter.SliderAdapter
import com.example.ferreexpress.Domain.CategoryDomain
import com.example.ferreexpress.Domain.SliderItems
import com.example.ferreexpress.Domain.itemsDomain
import com.example.ferreexpress.Helper.ItemsRepository
import com.example.ferreexpress.R
import com.example.ferreexpress.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        val w: Window = requireActivity().window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        ViewCompat.setOnApplyWindowInsetsListener(requireView().findViewById(R.id.mainHome)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initBanner()
        initCategory()
        //initPopular()
    }

    private fun initPopular() {
        //Obtiene una referencia a la base de datos
        val myRef: DatabaseReference = database.reference.child("Users")
        binding.progressBarPopular.visibility = View.VISIBLE
        val popularItems: ArrayList<itemsDomain> = ArrayList()

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(usersSnapshot: DataSnapshot) {
                if (usersSnapshot.exists()) {
                    for (userSnapshot in usersSnapshot.children) {
                        val productsRef = userSnapshot.child("products")

                        // Recorre los productos de cada usuario
                        for (productSnapshot in productsRef.children) {
                            val product = productSnapshot.getValue(itemsDomain::class.java)

                            // Verifica si el producto tiene una calificación de 4.0 o superior
                            product?.let {
                                if (it.rating >= 4.0) {
                                    popularItems.add(it)
                                }
                            }
                        }
                    }

                    if (popularItems.isNotEmpty()) {
                        // Mostrar los productos populares en el RecyclerView
                        binding.recyclerViewPopular.layoutManager =
                            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
                        binding.recyclerViewPopular.adapter = ProductAdapter(popularItems, false)
                    }
                    binding.progressBarPopular.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error
            }
        })
    }



    private fun initCategory() {
        val myRef: DatabaseReference = database.getReference("Category")
        binding.progressBarCategory.visibility = View.VISIBLE
        val items: ArrayList<CategoryDomain> = ArrayList()
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (issue in snapshot.children) {
                        val categoryDomain = issue.getValue(CategoryDomain::class.java)
                        categoryDomain?.let { items.add(it) }
                    }
                    if (items.isNotEmpty()) {
                        binding.recyclerViewCategory.layoutManager =
                            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        binding.recyclerViewCategory.adapter = CategoryAdapter(items, this@HomeFragment)
                    }
                    binding.progressBarCategory.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun initBanner() {
        val myRef: DatabaseReference = database.getReference("Banner")
        binding.progressBarBanner.visibility = View.VISIBLE
        val items: ArrayList<SliderItems> = ArrayList()
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (issueSnapshot in snapshot.children) {
                    val url = issueSnapshot.child("url").getValue(String::class.java)
                    url?.let {
                        val sliderItem = SliderItems().apply { setUrl(url) }
                        items.add(sliderItem)
                    }
                }
                banner(items)
                binding.progressBarBanner.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun banner(items: ArrayList<SliderItems>) {
        binding.viewpagerSlider.adapter = SliderAdapter(items, binding.viewpagerSlider)
        binding.viewpagerSlider.clipToPadding = false
        binding.viewpagerSlider.clipChildren = false
        binding.viewpagerSlider.offscreenPageLimit = 3
        binding.viewpagerSlider.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))

        binding.viewpagerSlider.setPageTransformer(compositePageTransformer)
    }

}