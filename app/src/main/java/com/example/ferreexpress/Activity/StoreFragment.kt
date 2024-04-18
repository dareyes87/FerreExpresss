package com.example.ferreexpress.Activity

import android.content.Intent
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
import com.example.ferreexpress.Adapter.ProductAdapter
import com.example.ferreexpress.Domain.itemsDomain
import com.example.ferreexpress.R
import com.example.ferreexpress.databinding.FragmentStoreBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StoreFragment : Fragment() {

    private lateinit var binding: FragmentStoreBinding
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()

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

    private fun initUI(){
        val w: Window = requireActivity().window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        ViewCompat.setOnApplyWindowInsetsListener(requireView().findViewById(R.id.mainStore)){ v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        floatButtom()
        initProduct()
    }

    private fun floatButtom(){
        val fab: FloatingActionButton = binding.floatBtnAddProduct
        fab.setOnClickListener{
            val intent = Intent(requireContext(), AddProductActivity::class.java)
            startActivity(intent)

        }
    }


    private fun initProduct(){
        val myRef: DatabaseReference = database.reference.child("Users").child("UserID_1").child("products")
        binding.progressBarStore.visibility = View.VISIBLE
        val items: ArrayList<itemsDomain> = ArrayList()
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (issue in snapshot.children) {
                        val itemsDomain = issue.getValue(itemsDomain::class.java)
                        itemsDomain?.let { items.add(it) }
                    }
                    if (items.isNotEmpty()) {
                        binding.recyclerMyStore.layoutManager =
                            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
                        binding.recyclerMyStore.adapter = ProductAdapter(items, true)
                    }
                    binding.progressBarStore.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }


}