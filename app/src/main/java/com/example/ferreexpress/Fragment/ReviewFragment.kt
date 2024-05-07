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
import com.example.ferreexpress.Domain.itemsDomain
import com.example.ferreexpress.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class ReviewFragment : Fragment() {

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

        initList(view)

        val pushReview: FloatingActionButton = view.findViewById(R.id.floatingButtonReview)
        pushReview.setOnClickListener{
            //MOSTRAR EL DIALOGO PARA PUBLICAR UN COMENTARIO
            val dialog = ReviewDialogAdapter(requireContext())
            dialog.window?.setBackgroundDrawable(ColorDrawable(resources.getColor(android.R.color.transparent)))
            dialog.show()
        }

    }

    fun initList(view: View){
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()

        val myRef: DatabaseReference = database.getReference("Users").child("UserID_1").child("products")
        val list: ArrayList<ReviewDomain> = ArrayList()
        val query: Query = myRef

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(productSnapshot in snapshot.children){
                        val product = productSnapshot.getValue(itemsDomain::class.java)
                        val reviewsSnapshot = productSnapshot.child("reviews")

                        for(reviewSnapshot in reviewsSnapshot.children){
                            val review = reviewSnapshot.getValue(ReviewDomain::class.java)
                            review?.let {
                                list.add(it)
                            }
                        }
                    }

                    val descTxt: RecyclerView = view.findViewById(R.id.reviewView)
                    if(list.size > 0){
                        val adapter = ReviewAdapter(list)
                        descTxt.adapter = adapter
                        descTxt.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de error
            }
        })

    }
}