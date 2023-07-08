package com.example.shoppingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import okhttp3.*
import java.io.IOException

class ProfileActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var sharedPreferences: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sharedPreferences = SharedPref(this)

        setupBottomNavigationView()
        fetchAndDisplayProfile()
    }

    private fun setupBottomNavigationView() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavView)
        bottomNavigationView.selectedItemId = R.id.profile

        val navigationMap = mapOf(
            R.id.home to HomeActivity::class.java,
            R.id.shop to ProductActivity::class.java,
            R.id.cart to CartActivity::class.java,
            R.id.orders to OrderActivity::class.java,
            R.id.profile to ProfileActivity::class.java
        )

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            navigationMap[item.itemId]?.let {
                startActivity(Intent(applicationContext, it))
                overridePendingTransition(0, 0)
            }
            true
        }
    }

    private fun fetchAndDisplayProfile() {
        val aboutButton = findViewById<Button>(R.id.btnAbout)
        val logoutButton = findViewById<Button>(R.id.btnLogout)
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://fakestoreapi.com/users/${sharedPreferences.getUserId()}")
            .addHeader("Authorization", "Bearer ${sharedPreferences.getUserToken()}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                showToast("Error occurred: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful) {
                    val user = Gson().fromJson(responseBody, User::class.java)
                    runOnUiThread {
                        updateUI(user)
                    }
                } else {
                    showToast("Error: ${response.code}")
                }
            }
        })

        aboutButton.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

        logoutButton.setOnClickListener {
            sharedPreferences.clearAll()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.googleMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun updateUI(user: User) {
        val nameTextView = findViewById<TextView>(R.id.nameTextView)
        val usernameTextView = findViewById<TextView>(R.id.usernameTextView)
        val emailTextView = findViewById<TextView>(R.id.emailTextView)
        val phoneTextView = findViewById<TextView>(R.id.phoneTextView)
        val addressTextView = findViewById<TextView>(R.id.addressTextView)
        val profileImage = findViewById<ImageView>(R.id.profileImageView)

        val imgNumber = (3316..4316).random()
        Picasso.get()
            .load("https://thispersondoesnotexist.xyz/img/$imgNumber.jpg")
            .into(profileImage)

        nameTextView.text = "${user.name?.firstname} ${user.name?.lastname}"
        usernameTextView.text = user.username
        emailTextView.text = user.email
        phoneTextView.text = user.phone
        addressTextView.text = "${user.address?.street}, ${user.address?.city}, ${user.address?.zipcode}"

        val latitude = user.address?.geolocation?.lat?.toDouble() ?: 0.0
        val longitude = user.address?.geolocation?.long?.toDouble() ?: 0.0
        val userLocation = LatLng(latitude, longitude)
        mMap.addMarker(MarkerOptions().position(userLocation).title("User's Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
    }

    private fun showToast(message: String) {
        Toast.makeText(this@ProfileActivity, message, Toast.LENGTH_SHORT).show()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

}
