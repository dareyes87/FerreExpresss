package com.example.ferreexpress.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ferreexpress.Adapter.ImageAdapter
import com.example.ferreexpress.Domain.itemsDomain
import com.example.ferreexpress.R
import com.example.ferreexpress.databinding.ActivityAddProductBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.UUID

class AddProductActivity : AppCompatActivity() {
    private var images: MutableList<ByteArray> = mutableListOf()
    private var storageRef: StorageReference? = null
    private lateinit var binding: ActivityAddProductBinding
    private val opciones = arrayOf(
        "Herramientas", "Tornillos", "Seguridad",
        "Herramientas Electricas", "Maquinaria Pesada", "Servicios"
    )

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa la referencia al almacenamiento de Firebase
        storageRef = FirebaseStorage.getInstance().reference

        //DATOS EXTRAS NECESARIOS PARA AGREGAR O ACTUALIZAR PRODUCTO
        val isEdit = intent.getBooleanExtra("isEdit", false)
        var key = intent.getStringExtra("keyProduct")

        //Configuracion del Spinner para la categoria del producto
        val spinner: Spinner = findViewById(R.id.spinnerCategory)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Abre el selector de imágenes cuando se hace clic en el botón de agregar imagen
        val btnImage: ImageView = findViewById(R.id.btnAddImage)
        btnImage.setOnClickListener{
            openImageSelector()
        }

        //SE MODIFICA LA VISTA DEPENDIENDO SI ES PARA AGREGAR O ACTUALIZAR EL PRODUCTO
        binding.btnUpdateProduct.visibility = View.GONE
        if(isEdit){
            binding.btnUpdateProduct.visibility = View.VISIBLE
            binding.btnPushProduct.visibility = View.GONE
            binding.textView21.text = "Editar Producto"
            //Accion para agregar el nuevo producto
            updateProduct(key.toString())
        } else {
            pushProduct()
        }

        // Configura el RecyclerView para mostrar las imágenes seleccionadas
        val recyclerImage: RecyclerView = findViewById(R.id.recyclerImages)
        recyclerImage.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun uriToJPEGBytes(uri: Uri): ByteArray {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream) // Ajusta la calidad según sea necesario
        return outputStream.toByteArray()
    }

    fun pushProduct(){
        // Obtiene una referencia a la base de datos de Firebase
        val database = Firebase.database
        val usersRef = database.getReference("Users")

        // Referencias a los campos de entrada de texto y el spinner
        val textTitle: EditText = findViewById(R.id.textTitle)
        val textPrice: EditText = findViewById(R.id.textPrice)
        val spinnerCategory: Spinner = findViewById(R.id.spinnerCategory)
        val textDescription: EditText = findViewById(R.id.textDescripcion)

        //Accion para agregar el nuevo producto
        val buttonAddProduct: Button = findViewById(R.id.btnPushProduct)
        buttonAddProduct.setOnClickListener{
            Toast.makeText(
                this,
                "Publicando...",
                Toast.LENGTH_SHORT
            ).show()
            val title = textTitle.text.toString()
            val price = textPrice.text.toString().toDoubleOrNull() ?: 0.00
            val category = spinnerCategory.selectedItem.toString()
            val description = textDescription.text.toString()

            val tasks: MutableList<Task<Uri>> = mutableListOf()
            // Itera sobre las imágenes seleccionadas para subirlas al almacenamiento de Firebase
            for(imageBytes in images){
                val imageName = "image_" + UUID.randomUUID().toString() + ".jpg"
                val imageRef = storageRef?.child("images/$imageName")
                val uploadTask = imageRef?.putBytes(imageBytes)
                uploadTask?.let { task ->
                    tasks.add(task.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        imageRef.downloadUrl
                    })
                }
            }

            // Espera a que se completen todas las tareas de carga de imágenes
            Tasks.whenAllComplete(tasks)
                .addOnCompleteListener { taskList ->
                    val downloadUrls: MutableList<String> = mutableListOf()
                    for (task in taskList.result!!) {
                        if (task.isSuccessful) {
                            val uri = task.result as Uri
                            downloadUrls.add(uri.toString())
                        } else {
                            // Maneja errores de carga de imágenes
                            Toast.makeText(
                                this,
                                "Error al subir una o más imágenes",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    //DATOS INICIALES DEL PRODUCTO
                    val oldPrice: Double = price
                    val review: Int = 0
                    val rating: Double = 0.0

                    //Objeto del Producto
                    val newProduct = mapOf(
                        "title" to title,
                        "price" to price,
                        "oldPrice" to oldPrice,
                        "review" to review,
                        "rating" to rating,
                        "category" to category,
                        "description" to description,
                        "picUrl" to downloadUrls
                    )

                    //Agregarlo a la base de datos
                    val userId = "UserID_1"
                    val userProductsRef = usersRef.child(userId).child("products")
                    val newProductRef = userProductsRef.push()
                    newProductRef.setValue(newProduct)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Producto agregado a tu comercio",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al agregar el producto", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
        }
    }

    fun updateProduct(key: String) {
        // Obtiene una referencia a la base de datos de Firebase
        val database = Firebase.database
        val usersRef = database.getReference("Users")
        val userId = "UserID_1" //ID DE LA TIENDA AL CUAL PERTENECE EL PRODUCTO

        //REFERENCIA AL PRODUCTO EN ESPECIFICO
        val productRef = usersRef.child(userId).child("products").child(key)

        //REFERENCIAS A LOS CAMPOS DE ENTRADA
        val textTitle: EditText = findViewById(R.id.textTitle)
        val textPrice: EditText = findViewById(R.id.textPrice)
        val spinnerCategory: Spinner = findViewById(R.id.spinnerCategory)
        val textDescription: EditText = findViewById(R.id.textDescripcion)
        var newOldPrice: Double = 0.0

        productRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val currentProduct = snapshot.getValue(itemsDomain::class.java)
                currentProduct?.let { existingProduct ->
                    // Establecer los valores actuales en los campos de entrada
                    textTitle.setText(existingProduct.title)
                    textPrice.setText(existingProduct.price.toString())
                    val categoryIndex = opciones.indexOf(existingProduct.category)
                    spinnerCategory.setSelection(categoryIndex)
                    textDescription.setText(existingProduct.description)
                    newOldPrice = existingProduct.price

                    // Cargar imágenes actuales del producto en el RecyclerView
                    val recyclerImage: RecyclerView = findViewById(R.id.recyclerImages)
                    recyclerImage.layoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                    existingProduct.picUrl?.let { picUrls ->
                        recyclerImage.adapter =
                            ImageAdapter(images.map { BitmapFactory.decodeByteArray(it, 0, it.size) })
                    }
                }
            }
        }

        //BOTON PARA ACTUALIZAR EL PRODUCTO
        val buttonUpdateProduct: Button = findViewById(R.id.btnUpdateProduct)
        buttonUpdateProduct.setOnClickListener {
            // Obtiene los nuevos valores del producto desde los campos de entrada
            val newTitle = textTitle.text.toString()
            val newPrice = textPrice.text.toString().toDoubleOrNull() ?: 0.00
            val newCategory = spinnerCategory.selectedItem.toString()
            val newDescription = textDescription.text.toString()
            val tasks: MutableList<Task<Uri>> = mutableListOf()
            // Itera sobre las imágenes seleccionadas para subirlas al almacenamiento de Firebase
            for(imageBytes in images){
                val imageName = "image_" + UUID.randomUUID().toString() + ".jpg"
                val imageRef = storageRef?.child("images/$imageName")
                val uploadTask = imageRef?.putBytes(imageBytes)
                uploadTask?.let { task ->
                    tasks.add(task.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        imageRef.downloadUrl
                    })
                }
            }

            // Espera a que se completen todas las tareas de carga de imágenes
            Tasks.whenAllComplete(tasks)
                .addOnCompleteListener { taskList ->
                    val downloadUrls: MutableList<String> = mutableListOf()
                    for (task in taskList.result!!) {
                        if (task.isSuccessful) {
                            val uri = task.result as Uri
                            downloadUrls.add(uri.toString())
                        } else {
                            // Maneja errores de carga de imágenes
                            Toast.makeText(
                                this,
                                "Error al subir una o más imágenes",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    // Actualiza los valores del producto
                    val updatedProductValues = mapOf(
                        "title" to newTitle,
                        "price" to newPrice,
                        "oldPrice" to newOldPrice,
                        "category" to newCategory,
                        "description" to newDescription,
                        "picUrl" to downloadUrls
                    )

                    // Actualiza los valores del producto en la base de datos
                    productRef.updateChildren(updatedProductValues)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Producto actualizado correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "Error al actualizar el producto: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
        }
    }


    fun openImageSelector(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        resultLauncher.launch(intent)
    }

    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            images.clear()
            data?.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    val imageBytes = uriToJPEGBytes(uri)
                    this.images.add(imageBytes)
                }
            } ?: data?.data?.let { uri ->
                val imageBytes = uriToJPEGBytes(uri)
                this.images.add(imageBytes)
            }

            val recyclerImage: RecyclerView = findViewById(R.id.recyclerImages)
            recyclerImage.adapter = ImageAdapter(images.map { BitmapFactory.decodeByteArray(it, 0, it.size) }.toMutableList())
        }
    }
}
