package com.example.ferreexpress.Adapter

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.RatingBar
import androidx.annotation.NonNull
import com.example.ferreexpress.R

class ReviewDialogAdapter(@NonNull context: Context) : Dialog(context) {

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
    }

    private fun animateImage(ratingImage: ImageView) {
        val scaleAnimation = ScaleAnimation(0f, 1f, 0f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        scaleAnimation.fillAfter = true
        scaleAnimation.duration = 200
        ratingImage.startAnimation(scaleAnimation)
    }
}
