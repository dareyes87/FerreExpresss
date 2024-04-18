package com.example.ferreexpress.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ferreexpress.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class AuthActivity : AppCompatActivity() {

    var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    var firebaseUser : FirebaseUser?=null

    //private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_auth)
        database = FirebaseDatabase.getInstance()
        
        //ANALYTICS EVENT
        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integracion de Firebase completa")
        analytics.logEvent("InitScreen", bundle)

        //setup
        setup()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainHome)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



    }





    private fun setup(){
        title = "Autenticacion"
        val btnRegistro = findViewById<Button>(R.id.btnregistro)
        val btnIniciarSesion = findViewById<Button>(R.id.btnInicioSesion)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

        btnRegistro.setOnClickListener{
            if(emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()){
                FirebaseAuth.getInstance().
                createUserWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString()).addOnCompleteListener(){
                    if(it.isSuccessful){
                        showHome()
                    }else{
                        showAlert()
                    }
                }
            }
        }

        btnIniciarSesion.setOnClickListener(){
            if(emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()){
                FirebaseAuth.getInstance().
                signInWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString()).addOnCompleteListener(){
                    if(it.isSuccessful){
                        showHome()
                    }else{
                        showAlert()
                    }
                }
            }
        }

    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Ha ocurrido un error en la autenticacion del usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(){
        val homeIntent = Intent(this, MainActivity::class.java).apply {
        }
        startActivity(homeIntent)
    }
    private fun PruebaDosGit(){
        //Esta es la segunda prueba para el manejo de git
    }

    private fun ComprobarSesion(){
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser!=null){
            val intent = Intent(this@AuthActivity, MainActivity::class.java)
            Toast.makeText(applicationContext, "La sesión esta Activa", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        ComprobarSesion()
        super.onStart()
    }

    private fun PruebaGitDosDos(){
        //Una funcion vacia
    }

}