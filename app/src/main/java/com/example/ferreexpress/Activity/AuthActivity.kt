package com.example.ferreexpress.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ferreexpress.R
import com.example.ferreexpress.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase

enum class ProviderType{
    BASIC,
    GOOGLE

}


class AuthActivity : AppCompatActivity() {

    //para registrar con google




    var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    var firebaseUser : FirebaseUser?=null

    private lateinit var firebaseAuth: FirebaseAuth
    //private val callbackManager = CallbackManager.Factory.create()
    private val RESULT_CODE_GOOGLE_SIGN_IN = 100
    private lateinit var auth: FirebaseAuth
    private val binding : ActivityAuthBinding by lazy {
        ActivityAuthBinding.inflate(layoutInflater)
    }

    //private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = Firebase.auth
        firebaseAuth = FirebaseAuth.getInstance()

        setContentView(R.layout.activity_auth)
        database = FirebaseDatabase.getInstance()

        //ANALYTICS EVENT
        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integracion de Firebase completa")
        analytics.logEvent("InitScreen", bundle)


        //info

        /* val bundle1 = intent.extras
        val email = bundle1?.getString("email")
        val provider =bundle1?.getString("provider")
        setup(email ?: "",provider ?:"")*/



        //setup
        setup()
        session()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainHome)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }

    //GUARDADO DE DATOS
   /* val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
    prefs.putString("email",email)
    prefs.putString("provider",provider)
    prefs.apply()

    private fun setup(email: String, provider: String){
        title = "Inicio"
        val emailText = findViewById<TextView>(R.id.textView12)
        val providerText = findViewById<TextView>(R.id.textView13)
        val cerrar = findViewById<TextView>(R.id.button4)

        emailText.text = email
        providerText.text = provider

        cerrar.setOnClickListener{

            // Borrar Datos
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            if(provider == ProviderType.FACEBOOK.name){
                LoginManager.getInstance().logOut()
            }
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
            val intent = Intent(this, login::class.java)
            startActivity(intent)
}
}

    */
    private fun session(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)

        if(email != null && provider != null){

            showHome(email, ProviderType.valueOf(provider))
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
                        showHome(it.result?.user?.email ?: "", ProviderType.GOOGLE)
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
                        showHome(it.result?.user?.email ?: "", ProviderType.GOOGLE)
                    }else{
                        showAlert()
                    }
                }
            }
        }




    }
    fun loginGoogle(view: View){
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        var googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()

        startActivityForResult(googleSignInClient.signInIntent, RESULT_CODE_GOOGLE_SIGN_IN)
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Ha ocurrido un error en la autenticacion del usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(s: String, google: ProviderType) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        //callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_CODE_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful)showHome(account.email!!, ProviderType.GOOGLE)
                    else Toast.makeText(this, "Error en la conexión con Google", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}