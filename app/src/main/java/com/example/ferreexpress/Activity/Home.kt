package com.example.ferreexpress.Activity

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.GridLayout
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.ferreexpress.Adapter.SliderAdapter
import com.example.ferreexpress.Adapter.CategoryAdapter
import com.example.ferreexpress.Adapter.PopularAdapter
import com.example.ferreexpress.Domain.CategoryDomain
import com.example.ferreexpress.Domain.SliderItems
import com.example.ferreexpress.Domain.itemsDomain
import com.example.ferreexpress.R
import com.example.ferreexpress.databinding.ActivityHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

open class Home : AppCompatActivity() {
    var database:FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var w:Window = window
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initBanner()
        initCategory()
        initPopular()
    }

    private fun initPopular() {
        val myref: DatabaseReference = database.getReference("Items")
        binding.progressBarPopular.visibility = View.VISIBLE
        val items:ArrayList<itemsDomain> = ArrayList()
        myref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for (issue in snapshot.children) {
                        val itemsDomain = issue.getValue(itemsDomain::class.java)
                        if (itemsDomain != null) {
                            items.add(itemsDomain)
                        }
                    }
                    if(!items.isEmpty()){
                        binding.recyclerViewPopular.layoutManager = LinearLayoutManager(this@Home, LinearLayoutManager.HORIZONTAL, false)
                        binding.recyclerViewPopular.adapter = PopularAdapter(items)
                    }
                    binding.progressBarPopular.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun initCategory() {
        val myref: DatabaseReference = database.getReference("Category")
        binding.progressBarCategory.visibility = View.VISIBLE
        val items:ArrayList<CategoryDomain> = ArrayList()
        myref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for (issue in snapshot.children) {
                        val categoryDomain = issue.getValue(CategoryDomain::class.java)
                        if (categoryDomain != null) {
                            items.add(categoryDomain)
                        }
                    }
                    if(!items.isEmpty()){
                        binding.recyclerViewCategory.layoutManager = LinearLayoutManager(this@Home, LinearLayoutManager.HORIZONTAL, false)
                        binding.recyclerViewCategory.adapter = CategoryAdapter(items)
                    }
                    binding.progressBarCategory.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }

    private fun initBanner() {
        val myRef: DatabaseReference = database.getReference("Banner")
        binding.progressBarBanner.visibility = View.VISIBLE
        val items:ArrayList<SliderItems> = ArrayList()
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(@NonNull snapshot: DataSnapshot) {
                for (issueSnapshot in snapshot.children) {
                    val url = issueSnapshot.child("url").getValue(String::class.java)
                    if (url != null) {
                        // Crea un objeto SliderItems solo con la URL
                        val sliderItem = SliderItems()
                        sliderItem.setUrl(url)
                        items.add(sliderItem)
                    }
                }
                banner(items)
                binding.progressBarBanner.visibility = View.GONE

            }

            override fun onCancelled(@NonNull error: DatabaseError) {

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