package com.example.ferreexpress.Activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ferreexpress.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class StoreFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_store, container, false)
        val fab: FloatingActionButton = view.findViewById(R.id.floatBtnAddProduct)
        fab.setOnClickListener{
            val intent = Intent(requireContext(), AddProductActivity::class.java)
            startActivity(intent)
        }
        // Inflate the layout for this fragment
        return view
    }

}