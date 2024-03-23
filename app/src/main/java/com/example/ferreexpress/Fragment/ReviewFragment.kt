package com.example.ferreexpress.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ferreexpress.Adapter.ReviewAdapter
import com.example.ferreexpress.Domain.ReviewDomain
import com.example.ferreexpress.R
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
    }

    fun initList(view: View){
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()

        val myRef: DatabaseReference = database.getReference("Review")
        val list: ArrayList<ReviewDomain> = ArrayList()
        val query: Query = myRef.orderByChild("ItemId").equalTo(4.0)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(issueSnapshot in snapshot.children){
                        val review: ReviewDomain? = issueSnapshot.getValue(ReviewDomain::class.java)
                        review?.let {
                            list.add(it)
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
                TODO("Not yet implemented")
            }
        })

        }
    }
