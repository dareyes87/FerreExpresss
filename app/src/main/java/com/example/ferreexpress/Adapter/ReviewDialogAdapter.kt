package com.example.ferreexpress.Adapter

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.example.ferreexpress.R
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.database.database

class ReviewDialogAdapter(@NonNull val activity: AppCompatActivity, var keyProduct: String, var idStore: String) : Dialog(activity) {

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.review_dialog)

        val ratingBar = findViewById<RatingBar>(R.id.ratingBarReview)
        val imageRatingBar = findViewById<ImageView>(R.id.ratingBarImage)

        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            when {
                rating <= 1 -> imageRatingBar.setImageResource(R.drawable.one_star)
                rating <= 2 -> imageRatingBar.setImageResource(R.drawable.two_star)
                rating <= 3 -> imageRatingBar.setImageResource(R.drawable.three_star)
                rating <= 4 -> imageRatingBar.setImageResource(R.drawable.four_star)
                else -> imageRatingBar.setImageResource(R.drawable.five_star)
            }
            animateImage(imageRatingBar)
        }

        //Datos del Usuario para los comentarios
        sharedPref = activity.getSharedPreferences(
            activity.getString(R.string.prefs_file),
            AppCompatActivity.MODE_PRIVATE
        )
        val nameUser = sharedPref.getString("name", null)
        val picUser = sharedPref.getString("profileImageUrl", null)

        //PUBLICAR COMENTARIO DEL PRODUCTO
        publicarComentario(keyProduct.toString(), nameUser.toString(), picUser.toString(), idStore.toString())

    }

    private fun animateImage(ratingImage: ImageView) {
        val scaleAnimation = ScaleAnimation(0f, 1f, 0f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        scaleAnimation.fillAfter = true
        scaleAnimation.duration = 200
        ratingImage.startAnimation(scaleAnimation)
    }

    private fun publicarComentario(productId: String, nameUser: String, picUser: String, idStore: String) {
        val database = Firebase.database
        val userRef = database.getReference("Users")

        val barRating: RatingBar = findViewById(R.id.ratingBarReview)
        val textComentario: EditText = findViewById(R.id.editTextComentario)

        //Accion para agregar un comentario al producto
        val buttonAddComentario: Button = findViewById(R.id.publicarReviewBtn)
        buttonAddComentario.setOnClickListener{

            val comentario = textComentario.text.toString()
            val rating = barRating.rating.toDouble()

            //Objeto del Comentario
            val newComentary = mapOf(
                "comentary" to comentario,
                "rating" to rating,
                "nameUser" to nameUser,
                "urlUser" to picUser
            )

            //Agregarlo a la base de datos
            val userComentaryRef = userRef.child(idStore).child("products").child(productId).child("reviews")
            val newComentaryRef = userComentaryRef.push()
            newComentaryRef.setValue(newComentary)
                .addOnCompleteListener{

                }
                .addOnFailureListener{e->

                }
        }
    }

}