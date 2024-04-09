package com.example.ferreexpress.Activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.ferreexpress.Adapter.CategoryAdapter
import com.example.ferreexpress.Adapter.PopularAdapter
import com.example.ferreexpress.Domain.CategoryDomain
import com.example.ferreexpress.Domain.itemsDomain
import com.example.ferreexpress.R
import com.example.ferreexpress.Activity.CartActivity
import com.example.ferreexpress.Activity.ProfileActivity
import com.example.ferreexpress.Adapter.SliderAdapter
import com.example.ferreexpress.Domain.SliderItems
import com.example.ferreexpress.databinding.FragmentHomeBinding
import com.google.firebase.database.*

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

        ViewCompat.setOnApplyWindowInsetsListener(requireView().findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initBanner()
        initCategory()
        initPopular()
    }

    private fun initPopular() {
        val myRef: DatabaseReference = database.getReference("Items")
        binding.progressBarPopular.visibility = View.VISIBLE
        val items: ArrayList<itemsDomain> = ArrayList()
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (issue in snapshot.children) {
                        val itemsDomain = issue.getValue(itemsDomain::class.java)
                        itemsDomain?.let { items.add(it) }
                    }
                    if (items.isNotEmpty()) {
                        binding.recyclerViewPopular.layoutManager =
                            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        binding.recyclerViewPopular.adapter = PopularAdapter(items)
                    }
                    binding.progressBarPopular.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
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
                        binding.recyclerViewCategory.adapter = CategoryAdapter(items)
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