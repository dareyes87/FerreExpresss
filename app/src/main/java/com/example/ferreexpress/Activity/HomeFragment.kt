package com.example.ferreexpress.Activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.size
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
import com.example.ferreexpress.Domain.ReviewDomain
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
    private lateinit var allProducts: ArrayList<itemsDomain>
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
        initPopular()
    }

    private fun initPopular() {
        //Obtiene una referencia a la base de datos
        val myRef: DatabaseReference = database.reference.child("Users")

        //Muestra la barra de progreso mientras se cargan los productos
        binding.progressBarPopular.visibility = View.VISIBLE

        val items: ArrayList<itemsDomain> = ArrayList()

        var lastVisibleItem = 0

        //Obtenemos los datos de la base de datos
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val userProductsRef = userSnapshot.child("products")

                        // Itera sobre cada producto del usuario
                        for (productSnapshot in userProductsRef.children) {
                            val keyItem = productSnapshot.key.toString()
                            val itemData = productSnapshot.value as HashMap<*, *> // Cast a HashMap

                            //Obtenemos los detalles del producto
                            val title = itemData["title"] as String
                            val productCategory = itemData["category"] as String
                            val description = itemData["description"] as String
                            val price = (itemData["price"] as Number?)?.toDouble() ?: 0.0
                            val oldPrice = (itemData["oldPrice"] as Number?)?.toDouble() ?: 0.0
                            val reviewCount = (itemData["review"] as Long?)?.toInt() ?: 0
                            val rating = (itemData["rating"] as Number?)?.toDouble() ?: 0.0
                            val numberinCart = (itemData["numberinCart"] as Long?)?.toInt() ?: 0

                            // Obtiene la lista de URL de las imagenes del producto
                            val picUrlList = itemData["picUrl"] as ArrayList<String>? ?: ArrayList()
                            val reviewsList = itemData["reviews"] as HashMap<String, Any>? ?: HashMap()

                            val reviews: ArrayList<ReviewDomain> = ArrayList()

                            // Iteramos sobre cada elemento del HashMap de reviews
                            for ((_, reviewData) in reviewsList) {
                                // Obtenemos los detalles de la revision
                                if (reviewData is HashMap<*, *>) {
                                    val nameUser = reviewData["nameUser"] as String
                                    val comentary = reviewData["comentary"] as String
                                    val rating = (reviewData["rating"] as Double?) ?: 0.0

                                    // Creamos un objeto ReviewDomain y lo agrega a la lista de Reviews
                                    val review = ReviewDomain(nameUser, comentary, "", rating)
                                    reviews.add(review)
                                }
                            }

                            // Crea un objeto itemsDomain con los detalles del producto
                            val item = itemsDomain(
                                keyItem,
                                title,
                                productCategory,
                                description,
                                picUrlList,
                                price,
                                oldPrice,
                                reviewCount,
                                rating,
                                numberinCart,
                                reviews
                            )

                            // Verificar si el producto tiene una calificacion mayor a 4
                            if (rating >= 1.0) {
                                items.add(item)
                            }
                        }
                    }

                    //Guardamos todos los productos obtenidos
                    allProducts = items

                    // Configurar el RecyclerView
                    if (items.isNotEmpty()) {
                        val productAdapter = ProductAdapter(items, false)
                        binding.recyclerViewPopular.layoutManager = GridLayoutManager(requireContext(), 2)
                        binding.recyclerViewPopular.adapter = productAdapter
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
                    val id = issueSnapshot.key // Obtener el ID del nodo
                    val url = issueSnapshot.child("url").getValue(String::class.java) ?: ""
                    val sliderItem = SliderItems(id.toString(), url) // Crear un objeto SliderItems con ID y URL
                    items.add(sliderItem)
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