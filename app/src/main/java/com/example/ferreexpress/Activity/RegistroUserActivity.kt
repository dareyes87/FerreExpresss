package com.example.ferreexpress.Activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Locale
import com.example.ferreexpress.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistroUserActivity : AppCompatActivity(), LocationListener {

    private lateinit var locationManager: LocationManager
    private lateinit var tvNameUser: EditText
    private lateinit var tvNumberPhone: EditText
    private lateinit var tvDepa: EditText
    private lateinit var tvMuni: EditText
    private lateinit var tvBarrio: EditText
    private lateinit var tvCalle: EditText
    private lateinit var tvAddres: EditText
    private lateinit var btnRegistrarse: Button

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_user)

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 101)
        }

        tvNameUser = findViewById(R.id.editTxtNameUser)
        tvNumberPhone = findViewById(R.id.editTxtPhone)
        tvDepa = findViewById(R.id.editTxtDepaUser)
        tvMuni = findViewById(R.id.editTxtMuniUser)
        tvBarrio = findViewById(R.id.editTxtBarrioUser)
        tvCalle = findViewById(R.id.editTxtCalle)
        tvAddres = findViewById(R.id.editTxtDireccionUser)
        btnRegistrarse = findViewById(R.id.btnRegistroDatosUser)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationEnabled()
        getLocation()

        btnRegistrarse.setOnClickListener {
            saveUserData()
        }
    }

    private fun saveUserData() {
        val name = tvNameUser.text.toString()
        val phone = tvNumberPhone.text.toString()
        val depa = tvDepa.text.toString()
        val muni = tvMuni.text.toString()
        val barrio = tvBarrio.text.toString()
        val calle = tvCalle.text.toString()
        val address = tvAddres.text.toString()

        val userId = auth.currentUser?.uid

        if (userId != null) {
            val userRef = database.reference.child("Users").child(userId)
            val userData = mapOf(
                "name" to name,
                "phone" to phone,
                "depa" to depa,
                "muni" to muni,
                "barrio" to barrio,
                "calle" to calle,
                "address" to address,
                "type" to "comprador"
            )

            userRef.setValue(userData).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Guardar en SharedPreferences
                    val sharedPref = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("name", name)
                        putString("phone", phone)
                        putString("depa", depa)
                        putString("muni", muni)
                        putString("barrio", barrio)
                        putString("calle", calle)
                        putString("address", address)
                        apply()
                    }

                    Toast.makeText(this, "Datos guardados exitosamente", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error al guardar datos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun locationEnabled() {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        var networkEnabled = false
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (!gpsEnabled && !networkEnabled) {
            AlertDialog.Builder(this)
                .setTitle("Enable GPS Service")
                .setMessage("We need your GPS location to show Near Places around you.")
                .setCancelable(false)
                .setPositiveButton("Enable") { _, _ ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun getLocation() {
        try {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5f, this)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    override fun onLocationChanged(location: Location) {
        try {
            val geocoder = Geocoder(applicationContext, Locale.getDefault())
            val addresses: List<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)

            addresses?.let {
                if (it.isNotEmpty()) {
                    val locationDepaInfo = it[0].countryName + ", " + it[0].adminArea
                    tvDepa.text = locationDepaInfo.toEditable()
                    tvMuni.text = it[0].locality?.toEditable() ?: "N/A".toEditable()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        // Deprecated
    }

    override fun onProviderEnabled(provider: String) {
        // Handle provider enabled
    }

    override fun onProviderDisabled(provider: String) {
        // Handle provider disabled
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}