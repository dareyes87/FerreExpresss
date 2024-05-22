package com.example.ferreexpress.Activity

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.ferreexpress.R

class RegistroComercioActivity : AppCompatActivity() {

    private val sharedPref = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
    val userId = sharedPref.getString("usuario", null)

    private lateinit var imageComercio: ImageView
    private lateinit var nombreComercio: EditText
    private lateinit var descripComercio: EditText
    private lateinit var correoComercio: EditText
    private lateinit var telComercio: EditText
    private lateinit var registroComercio: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_comercio)

        imageComercio = findViewById(R.id.imageLogoComercio)
        nombreComercio = findViewById(R.id.editTxtNameStore)
        descripComercio = findViewById(R.id.editTxtDescriptionStore)
        correoComercio = findViewById(R.id.editTxtCorreoContacto)
        telComercio = findViewById(R.id.editTxtPhoneContacto)
        registroComercio = findViewById(R.id.btnRegistrarComercio)
        
        registroComercio.setOnClickListener{
            registrarComercio(userId)
        }
        
    }

    private fun registrarComercio(userId: String?) {

    }


}