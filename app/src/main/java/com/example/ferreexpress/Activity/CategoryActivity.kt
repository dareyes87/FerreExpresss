package com.example.ferreexpress.Activity

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ferreexpress.Adapter.ProductAdapter
import com.example.ferreexpress.Domain.ReviewDomain
import com.example.ferreexpress.Domain.itemsDomain
import com.example.ferreexpress.R
import com.example.ferreexpress.databinding.ActivityCategoryBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale
import android.text.Editable
import android.text.TextWatcher

class CategoryActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var binding: ActivityCategoryBinding
    private lateinit var allProducts: ArrayList<itemsDomain>

    companion object {
        const val CATEGORY_EXTRA = "Default"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Infla el layout de la actividad
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Inicializa la instancia de Firebase
        database = FirebaseDatabase.getInstance()

        //Configura la ventana para que la barra de estado y navegacion sean transparentes
        val w: Window = window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        // Configura el relleno para el contenido principal para evitar que se superponga con la barra de estado y de navegación
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Obtiene la categoria seleccionada de la actividad anterior
        val category = intent.getStringExtra(CATEGORY_EXTRA)
        initProducts(category.toString())

        //Agrega un TextWatcher al EditText para filtrar los productos mientras el usuario escribe
        binding.editTextBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No es necesario implementar esta función
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No es necesario implementar esta función
            }

            override fun afterTextChanged(s: Editable?) {
                // Filtrar productos según el texto ingresado por el usuario
                val searchText = s.toString().toLowerCase(Locale.getDefault())
                val filteredProducts = ArrayList<itemsDomain>()

                for (product in allProducts) {
                    if (product.title.toLowerCase(Locale.getDefault()).contains(searchText)) {
                        filteredProducts.add(product)
                    }
                }

                // Actualizar el RecyclerView con los productos filtrados
                (binding.recyclerSelectCategory.adapter as ProductAdapter).apply {
                    setItems(filteredProducts)
                    notifyDataSetChanged()
                }
            }
        })
    }

    private fun initProducts(category: String){
        // Obtiene una referencia a la base de datos de Firebase
        val myRef: DatabaseReference = database.reference.child("Users")

        // Muestra la barra de progreso mientras se cargan los productos
        binding.progressBarSelectCategory.visibility = View.VISIBLE

        val items: ArrayList<itemsDomain> = ArrayList()

        var refStore: String = ""
        //Obtenemos los datos de la pase de datos
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val userProductsRef = userSnapshot.child("products")

                        // Itera sobre cada producto del usuario
                        for (productSnapshot in userProductsRef.children) {
                            val keyItem = productSnapshot.key.toString()
                            val itemData = productSnapshot.value as HashMap<*, *> // Cast a HashMap

                            //Obtenemos los detalles del producto
                            refStore = itemData["refStore"] as String
                            val title = itemData["title"] as String
                            val productCategory = itemData["category"] as String
                            val description = itemData["description"] as String
                            val price = (itemData["price"] as Number?)?.toDouble() ?: 0.0
                            val oldPrice = (itemData["oldPrice"] as Number?)?.toDouble() ?: 0.0
                            val reviewCount = (itemData["review"] as Long?)?.toInt() ?: 0
                            val rating = (itemData["rating"] as Number?)?.toDouble() ?: 0.0
                            val numberinCart = (itemData["numberinCart"] as Long?)?.toInt() ?: 0
                            val off = (itemData["off"] as Number?)?.toDouble() ?: 0.0

                            // Obtiene la lista de URL de las imagenes del producto
                            val picUrlList = itemData["picUrl"] as ArrayList<String>? ?: ArrayList()
                            val reviewsList = itemData["reviews"] as HashMap<String, Any>? ?: HashMap()

                            val reviews: ArrayList<ReviewDomain> = ArrayList()

                            // Iteramos sobre cada elemento del HashMap de reviews
                            for ((_, reviewData) in reviewsList) {
                                // Obtenemos los detalles de la revision
                                if (reviewData is HashMap<*, *>) {
                                    val nameUser = reviewData["nameUser"] as String
                                    val comentary = reviewData["comentary"] as String
                                    val rating = (reviewData["rating"] as Double?) ?: 0.0

                                    // Creamos un objeto ReviewDomain y lo agrega a la lista de Reviews
                                    val review = ReviewDomain(nameUser, comentary, "", rating)
                                    reviews.add(review)
                                }
                            }

                            // Crea un objeto itemsDomain con los detalles del producto
                            val item = itemsDomain(
                                keyItem,
                                title,
                                productCategory,
                                description,
                                picUrlList,
                                price,
                                oldPrice,
                                reviewCount,
                                rating,
                                numberinCart,
                                reviews,
                                off
                            )

                            // Verificar si el producto pertenece a la característica deseada
                            if (category == "Oferta 10") {
                                val productDiscount = item.off
                                // Filtrar por descuento si la categoría es "Oferta 10"
                                if (productDiscount in 7.00..12.99) {
                                    items.add(item)
                                }
                            } else if (category == "Oferta 5") {
                                val productDiscount = item.off
                                // Filtrar por descuento si la categoría es "Oferta 5"
                                if (productDiscount in 2.00..6.99) {
                                    items.add(item)
                                }
                            } else if (category == item.category) {
                                // Filtrar por categoría si la categoría no es "Oferta"
                                items.add(item)
                            }

                        }
                    }

                    //Guardamos todos los productos obtenidos
                    allProducts = items
                    if (items.isNotEmpty()) {
                        //Configura el RecyclerView para mostrar los productos
                        binding.recyclerSelectCategory.layoutManager =
                            GridLayoutManager(this@CategoryActivity, 2, GridLayoutManager.VERTICAL, false)
                        val adapter = ProductAdapter(items, false)
                        adapter.setStore(refStore)
                        binding.recyclerSelectCategory.adapter = adapter
                    }
                    //Ocultamos la barra de progreso
                    binding.progressBarSelectCategory.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar error con la base de datos
            }
        })
    }

}