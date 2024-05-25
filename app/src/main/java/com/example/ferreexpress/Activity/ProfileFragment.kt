package com.example.ferreexpress.Activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.example.ferreexpress.R
import org.checkerframework.common.subtyping.qual.Bottom

class ProfileFragment : Fragment() {

    private lateinit var imageViewFavorites: ImageView
    private lateinit var btnComprobantesPago: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageViewFavorites = view.findViewById(R.id.imageFav)
        btnComprobantesPago = view.findViewById(R.id.btnComprobantesPago)

        imageViewFavorites.setOnClickListener {
            // Acción a realizar cuando se hace clic en el ImageView de favoritos
            val homeIntent = Intent(requireContext(), Pedidos::class.java).apply {
            }
            startActivity(homeIntent)
        }

        btnComprobantesPago.setOnClickListener{

            val homeIntent = Intent(requireContext(), FacturasActivity::class.java).apply {
            }
            startActivity(homeIntent)

        }

    }
}
