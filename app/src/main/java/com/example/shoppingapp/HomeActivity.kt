package com.example.shoppingapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class HomeActivity : AppCompatActivity() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var sharedPref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sharedPref = SharedPref.getInstance(this)
        setupBottomNavigationView()
        fetchAndDisplayCategories()
    }

    private fun setupBottomNavigationView() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavView)
        bottomNavigationView.selectedItemId = R.id.home

        val navigationMap = mapOf(
            R.id.home to HomeActivity::class.java,
            R.id.shop to ProductActivity::class.java,
            R.id.cart to CartActivity::class.java,
            R.id.orders to OrderActivity::class.java,
            R.id.profile to ProfileActivity::class.java
        )

        bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
            navigationMap[item.itemId]?.let {
                startActivity(Intent(applicationContext, it))
                overridePendingTransition(0, 0)
            }
            item.itemId == R.id.home || navigationMap.containsKey(item.itemId)
        }
    }

    private fun fetchAndDisplayCategories() {
        val recyclerView = findViewById<RecyclerView>(R.id.categoriesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val categoryAdapter = CategoriesAdapter(emptyList())
        recyclerView.adapter = categoryAdapter

        coroutineScope.launch {
            try {
                val categories = fetchCategories()
                categoryAdapter.updateCategories(categories)
            } catch (e: Exception) {
                Toast.makeText(this@HomeActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun fetchCategories(): List<String> = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://fakestoreapi.com/products/categories")
            .build()
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        val responseBody = response.body?.string()
        return@withContext responseBody?.let {
            it.replace("[", "").replace("]", "").replace("\"", "").split(",")
        } ?: emptyList()
    }

    private fun checkUserToken() {
        val token = sharedPref.getUserToken()
        if (token == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        checkUserToken()
    }
}
