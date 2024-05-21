package com.example.ferreexpress.Activity

import android.content.Context
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
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class AuthActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private var firebaseUser: FirebaseUser? = null

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)

        // Initialize Firebase components
        database = FirebaseDatabase.getInstance()

        // Analytics event
        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integracion de Firebase completa")
        analytics.logEvent("InitScreen", bundle)

        // Setup UI elements and listeners
        setup()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainHome)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setup() {
        title = "Autenticacion"
        val btnRegistro = findViewById<Button>(R.id.btnregistro)
        val btnIniciarSesion = findViewById<Button>(R.id.btnInicioSesion)
        val googleBtn = findViewById<Button>(R.id.googleBtn)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

        btnRegistro.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showRegistro()
                        } else {
                            showAlert()
                        }
                    }
            }
        }

        btnIniciarSesion.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            fetchAndStoreUserData() // Llama a fetchAndStoreUserData cuando el inicio de sesión sea exitoso
                        } else {
                            showAlert()
                        }
                    }
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this) { }
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        googleBtn.setOnClickListener {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    private fun fetchAndStoreUserData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val userRef = database.reference.child("Users").child(userId)
            userRef.get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val userData = dataSnapshot.value as Map<String, String>
                    val sharedPref = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("name", userData["name"])
                        putString("phone", userData["phone"])
                        putString("depa", userData["depa"])
                        putString("muni", userData["muni"])
                        putString("barrio", userData["barrio"])
                        putString("calle", userData["calle"])
                        putString("address", userData["address"])
                        putString("usuario", userId)
                        putString("type", userData["type"])
                        putString("profileImageUrl", userData["profileImageUrl"])
                        apply()
                    }
                    Toast.makeText(this, "Bienvenido ${userData["name"]}", Toast.LENGTH_SHORT).show()
                    showHome() // Llama a showHome después de actualizar los datos
                } else {
                    Toast.makeText(this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error al conectar con la base de datos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showHome() {
        val homeIntent = Intent(this, MainActivity::class.java)
        startActivity(homeIntent)
    }

    private fun showRegistro() {
        val registroIntent = Intent(this, RegistroUserActivity::class.java)
        startActivity(registroIntent)
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Ha ocurrido un error en la autenticacion del usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun comprobarSesion() {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            val intent = Intent(this@AuthActivity, MainActivity::class.java)
            Toast.makeText(applicationContext, "La sesión está activa", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        comprobarSesion()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN && data != null) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result?.isSuccess == true) {
                val account = result.signInAccount
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                } else {
                    showAlert()
                }
            } else {
                showAlert()
            }
        } else {
            showAlert() // Handle the case when data is null
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    fetchAndStoreUserData()
                } else {
                    showAlert()
                }
            }
    }
}