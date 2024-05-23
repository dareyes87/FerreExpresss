package com.example.ferreexpress.Fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ferreexpress.Adapter.ReviewAdapter
import com.example.ferreexpress.Adapter.ReviewDialogAdapter
import com.example.ferreexpress.Domain.ReviewDomain
import com.example.ferreexpress.R
import com.example.ferreexpress.databinding.FragmentReviewBinding
import com.example.ferreexpress.databinding.FragmentStoreBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class ReviewFragment(var key: String, var idStore: String) : Fragment() {

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var binding: FragmentReviewBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Publicar Comentarios
        pushReview(view, key, idStore)
        //Mostrar los comentarios
        initComentarios(key, idStore)
    }

    private fun pushReview(view: View, keyP: String, idS: String){
        val pushReview: FloatingActionButton = view.findViewById(R.id.floatingButtonReview)
        pushReview.setOnClickListener{
            //MOSTRAR EL DIALOGO PARA PUBLICAR UN COMENTARIO
            val dialog = ReviewDialogAdapter(requireActivity() as AppCompatActivity, keyP, idS)
            dialog.window?.setBackgroundDrawable(ColorDrawable(resources.getColor(android.R.color.transparent)))
            dialog.show()
        }
    }

    private fun initComentarios(key: String, idStore: String){
        //OBTIENE UNA REFERENCIA A LA BASE DE DATOS FIREBASE
        val myRef: DatabaseReference = database.reference
            .child("Users").child(idStore)
            .child("products").child(key).child("reviews")

        val comentarys: ArrayList<ReviewDomain> = ArrayList()

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(issue in snapshot.children){
                        val comentaryData = issue.value as HashMap<*, *>

                        val nombre = comentaryData["nameUser"] as String
                        val cometario = comentaryData["comentary"] as String
                        val picUrl = comentaryData["urlUser"] as String
                        val puntuacion = comentaryData["rating"] as Double

                        val comentarioItem = ReviewDomain(
                            nombre,
                            cometario,
                            picUrl,
                            puntuacion
                        )
                        comentarys.add(comentarioItem)
                    }

                    if(comentarys.isNotEmpty()){
                        binding.reviewView.layoutManager =
                            GridLayoutManager(requireContext(), 1, GridLayoutManager.VERTICAL, false)
                        binding.reviewView.adapter = ReviewAdapter(comentarys)
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}