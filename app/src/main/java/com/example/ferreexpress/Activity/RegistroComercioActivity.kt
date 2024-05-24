package com.example.ferreexpress.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ferreexpress.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.UUID

class RegistroComercioActivity : AppCompatActivity() {

    //Usuario actual en la aplicacion
    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    private val sharedPref by lazy {
        getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
    }
    val userId by lazy {
        sharedPref.getString("usuario", null)
    }

    //Campos del formulario para registrar el comercio
    private lateinit var imageComercio: ImageView
    private lateinit var nombreComercio: EditText
    private lateinit var descripComercio: EditText
    private lateinit var correoComercio: EditText
    private lateinit var telComercio: EditText
    private lateinit var registroComercio: Button
    private var imageUri: Uri? = null

    //Referencia a la base de datos
    private val db = FirebaseDatabase.getInstance().reference
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_comercio)

        imageComercio = findViewById(R.id.imageLogoComercio)
        nombreComercio = findViewById(R.id.editTxtNameStore)
        descripComercio = findViewById(R.id.editTxtDescriptionStore)
        correoComercio = findViewById(R.id.editTxtCorreoContacto)
        telComercio = findViewById(R.id.editTxtPhoneContacto)
        registroComercio = findViewById(R.id.btnRegistrarComercio)

        imageComercio.setOnClickListener {
            openGallery()
        }

        registroComercio.setOnClickListener {
            registrarComercio(userId)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                imageComercio.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registrarComercio(userId: String?) {
        if (userId == null) {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        val nombre = nombreComercio.text.toString()
        val descripcion = descripComercio.text.toString()
        val correo = correoComercio.text.toString()
        val telefono = telComercio.text.toString()

        if (imageUri != null) {
            uploadImageToStorage(userId, nombre, descripcion, correo, telefono)
        } else {
            saveComercioToRealtimeDatabase(userId, nombre, descripcion, correo, telefono, null)
        }
    }

    private fun uploadImageToStorage(userId: String, nombre: String, descripcion: String, correo: String, telefono: String) {
        val storageRef = storage.reference
        val logoRef = storageRef.child("logos/${UUID.randomUUID()}.jpg")

        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        logoRef.putBytes(data)
            .addOnSuccessListener { taskSnapshot ->
                logoRef.downloadUrl.addOnSuccessListener { uri ->
                    saveComercioToRealtimeDatabase(userId, nombre, descripcion, correo, telefono, uri.toString())
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveComercioToRealtimeDatabase(userId: String, nombre: String, descripcion: String, correo: String, telefono: String, logoUrl: String?) {
        val comercio = mapOf(
            "nombreComercio" to nombre,
            "descripcionComercio" to descripcion,
            "correoComercio" to correo,
            "telefonoComercio" to telefono,
            "type" to "vendedor",
            "logoComercio" to logoUrl
        )

        db.child("Users").child(userId).updateChildren(comercio)
            .addOnSuccessListener {
                Toast.makeText(this, "Datos de comercio guardados exitosamente", Toast.LENGTH_SHORT).show()

                val sharedPref = this.getSharedPreferences(this.getString(R.string.prefs_file), Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("type", "vendedor")
                    apply()
                }

                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar los datos en Realtime Database", Toast.LENGTH_SHORT).show()
            }
    }

}