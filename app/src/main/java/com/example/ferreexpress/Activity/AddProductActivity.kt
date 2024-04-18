package com.example.ferreexpress.Activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ferreexpress.Adapter.ImageAdapter
import com.example.ferreexpress.R
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class AddProductActivity : AppCompatActivity() {
    private var imageUri: Uri? = null
    private var images: MutableList<Uri> = mutableListOf()
    private var storageRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        //Configuracion del Spiner para la categoria del producto
        val spinner: Spinner = findViewById(R.id.spinnerCategory)
        val opciones = arrayOf("Tornillos", "Seguridad", "Herramientas")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val btnImage: ImageView = findViewById(R.id.btnAddImage)
        btnImage.setOnClickListener{
            openImageSelector()
        }

        storageRef = FirebaseStorage.getInstance().reference

        val recyclerImage: RecyclerView = findViewById(R.id.recyclerImages)
        recyclerImage.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        pushProduct()
    }
    fun pushProduct(){
        val database = Firebase.database
        val usersRef = database.getReference("Users")

        //Referencia a los campos
        val textTitle: EditText = findViewById(R.id.textTitle)
        val textPrice: EditText = findViewById(R.id.textPrice)
        val spinnerCategory: Spinner = findViewById(R.id.spinnerCategory)
        val textDescription: EditText = findViewById(R.id.textDescripcion)

        //Accion para agregar el nuevo producto
        val buttonAddProduct: Button = findViewById(R.id.btnPushProduct)
        buttonAddProduct.setOnClickListener{
            val tite = textTitle.text.toString()
            val price = textPrice.text.toString().toDoubleOrNull() ?: 0.0
            val category = spinnerCategory.selectedItem.toString()
            val descripcion = textDescription.text.toString()

            val tasks: MutableList<Task<Uri>> = mutableListOf()
            for(imageUri in images){
                val imageName = "image_" + UUID.randomUUID().toString()
                val imageRef = storageRef?.child("images/$imageName")
                val uploadTask = imageRef?.putFile(imageUri)
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

                    //Objeto del Producto
                    val newProduct = mapOf(
                        "title" to tite,
                        "price" to price,
                        "category" to category,
                        "descripcion" to descripcion,
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

    private fun openImageSelector(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        resultLauncher.launch(intent)
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            images.clear()
            data?.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    this.images.add(uri)
                }
            } ?: data?.data?.let { uri ->
                this.images.add(uri)
            }

            val recyclerImage: RecyclerView = findViewById(R.id.recyclerImages)
            recyclerImage.adapter = ImageAdapter(images)
        }
    }

}