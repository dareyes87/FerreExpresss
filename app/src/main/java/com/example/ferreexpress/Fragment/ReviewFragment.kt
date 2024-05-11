package com.example.ferreexpress.Fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ferreexpress.Adapter.ReviewAdapter
import com.example.ferreexpress.Adapter.ReviewDialogAdapter
import com.example.ferreexpress.Domain.ReviewDomain
import com.example.ferreexpress.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class ReviewFragment(var comentariosList: List<ReviewDomain>) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.reviewView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Crear el adaptador con la lista de comentarios y asignarlo al RecyclerView
        val adapter = ReviewAdapter(requireContext(), comentariosList)
        recyclerView.adapter = adapter

        val pushReview: FloatingActionButton = view.findViewById(R.id.floatingButtonReview)
        pushReview.setOnClickListener{
            //MOSTRAR EL DIALOGO PARA PUBLICAR UN COMENTARIO
            var key = arguments?.getString("keyProduct", "")
            val dialog = ReviewDialogAdapter(requireContext(), key.toString())
            dialog.window?.setBackgroundDrawable(ColorDrawable(resources.getColor(android.R.color.transparent)))
            dialog.show()
        }
    }
}