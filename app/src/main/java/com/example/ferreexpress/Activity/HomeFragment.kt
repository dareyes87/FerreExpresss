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
    private lateinit var productAdapter: ProductAdapter
    private var isLoading = false
    private val itemsPerPage = 2
    private var lastVisibleItem: Int = 0

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
        binding.progressBarPopular.visibility = View.VISIBLE
        allProducts = ArrayList()

        productAdapter = ProductAdapter(allProducts, false)
        binding.recyclerViewPopular.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewPopular.adapter = productAdapter

        loadInitialProducts() // Load initial products

        binding.recyclerViewPopular.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                if (!isLoading && layoutManager.findLastCompletelyVisibleItemPosition() == allProducts.size - 1) {
                    loadMoreProducts(allProducts.size)
                }
            }
        })
    }

    private fun loadInitialProducts() {
        loadMoreProducts(lastVisibleItem, itemsPerPage) // Load initial 2 products
    }

    private fun loadMoreProducts(startIndex: Int, count: Int = itemsPerPage) {
        isLoading = true
        val myRef: DatabaseReference = database.reference.child("Users")
        var refStore: String = ""
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val items: ArrayList<itemsDomain> = ArrayList()
                    var currentCount = 0

                    outer@ for (userSnapshot in snapshot.children) {
                        val userProductsRef = userSnapshot.child("products")
                        val productList = userProductsRef.children.toList()
                        for (i in startIndex until productList.size) {
                            val productSnapshot = productList[i]
                            val keyItem = productSnapshot.key.toString()
                            val itemData = productSnapshot.value as HashMap<*, *>

                            refStore = itemData["refStore"] as String
                            val title = itemData["title"] as String
                            val productCategory = itemData["category"] as String
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
                                    val reviewRating = (reviewData["rating"] as Double?) ?: 0.0
                                    val review = ReviewDomain(nameUser, comentary, "", reviewRating)
                                    reviews.add(review)
                                }
                            }

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

                            if (rating >= 4.0) {
                                items.add(item)
                                currentCount++
                                if (currentCount >= count) {
                                    lastVisibleItem = i + 1
                                    break@outer
                                }
                            }
                        }
                    }

                    allProducts.addAll(items)
                    productAdapter.setItems(items)
                    productAdapter.setStore(refStore)
                    productAdapter.notifyDataSetChanged()
                    binding.progressBarPopular.visibility = View.GONE
                    isLoading = false
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
                    val id = issueSnapshot.key
                    val url = issueSnapshot.child("url").getValue(String::class.java) ?: ""
                    val sliderItem = SliderItems(id.toString(), url)
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