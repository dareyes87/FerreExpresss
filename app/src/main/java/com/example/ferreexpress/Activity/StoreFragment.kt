package com.example.ferreexpress.Activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.ferreexpress.Adapter.ProductAdapter
import com.example.ferreexpress.Domain.itemsDomain
import com.example.ferreexpress.Helper.ItemsRepository
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
        // Configura la pantalla para que utilice la barra de estado transparente
        val w: Window = requireActivity().window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        // Aplica los márgenes según los cambios en la barra de estado
        ViewCompat.setOnApplyWindowInsetsListener(requireView().findViewById(R.id.mainStore)){ v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        floatButtom()
        initProduct()
        initInfoNegocio()
    }

    private fun floatButtom(){
        val fab: FloatingActionButton = binding.floatBtnAddProduct
        fab.setOnClickListener{
            val intent = Intent(requireContext(), AddProductActivity::class.java)
            startActivity(intent)

        }
    }

    private fun initProduct(){
        // Obtiene una referencia a la base de datos de Firebase
        val myRef: DatabaseReference = database.reference.child("Users").child("UserID_1").child("products")
        // Muestra la barra de progreso mientras se cargan los productos
        binding.progressBarStore.visibility = View.VISIBLE
        val items: ArrayList<itemsDomain> = ArrayList()
        var keyItem: String
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Itera sobre los productos obtenidos de la base de datos
                    for (issue in snapshot.children) {
                        val keyItem = issue.key.toString() // Obtiene la clave del elemento actual
                        val itemsDomain = issue.getValue(itemsDomain::class.java)

                        // Verifica si itemsDomain no es nulo y luego asigna la clave al campo id
                        itemsDomain?.let {
                            it.keyProduct = keyItem
                            items.add(it)
                        }
                    }

                    if (items.isNotEmpty()) {
                        val repository = ItemsRepository()
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

    private fun initInfoNegocio() {
        // Obtiene una referencia a la base de datos de Firebase para el usuario 1
        val userRef: DatabaseReference = database.reference.child("Users").child("UserID_1")

        // Agrega un listener para leer los datos del usuario
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Aquí puedes leer los datos del usuario y hacer lo que necesites con ellos
                    val name = snapshot.child("name").getValue(String::class.java)
                    val description = snapshot.child("descriptionN").getValue(String::class.java)
                    val followersCount = snapshot.child("seguidores").childrenCount // Obtiene el número de seguidores
                    val ratingSnapshot = snapshot.child("rating")
                    var totalRating = 0.0
                    var ratingCount = 0
                    // Calcula el rating promedio
                    for (rating in ratingSnapshot.children) {
                        val valrating = rating.child("valrating").getValue(Double::class.java)
                        if (valrating != null) {
                            totalRating += valrating
                            ratingCount++
                        }
                    }
                    val averageRating = if (ratingCount > 0) totalRating / ratingCount else 0.0

                    // Obtiene la URL de la imagen de perfil
                    val picUrl = snapshot.child("picUser").getValue(String::class.java)

                    //SE LLENAN LOS CAMPOS CON LA INFO
                    binding.textViewNameNegocio.text = name.toString()
                    binding.textViewDesNegocio.text = description.toString()
                    binding.textViewSeguidores.text = followersCount.toString()
                    binding.textViewRating.text = String.format("%.1f", averageRating)
                    binding.ratingBarNegocio.rating = averageRating.toFloat()
                    Glide.with(requireContext())
                        .load(picUrl)
                        .into(binding.imageViewNegocio)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Maneja el error si la lectura de datos se cancela
            }
        })
    }

}