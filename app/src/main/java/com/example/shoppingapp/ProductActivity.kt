package com.example.shoppingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class ProductActivity : AppCompatActivity() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        setupBottomNavigationView()

        val category = intent.getStringExtra("CATEGORY")
        fetchAndDisplayProducts(category)
    }

    private fun setupBottomNavigationView() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavView)
        bottomNavigationView.selectedItemId = R.id.shop

        val navigationMap = mapOf(
            R.id.home to HomeActivity::class.java,
            R.id.shop to ProductActivity::class.java,
            R.id.cart to CartActivity::class.java,
            R.id.orders to OrderActivity::class.java,
            R.id.profile to ProfileActivity::class.java
        )

        bottomNavigationView.setOnNavigationItemSelectedListener { item: MenuItem ->
            navigationMap[item.itemId]?.let {
                startActivity(Intent(applicationContext, it))
                overridePendingTransition(0, 0)
            }
            true
        }
    }

    private fun fetchAndDisplayProducts(category: String?) {
        val recyclerView = findViewById<RecyclerView>(R.id.productsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val productsAdapter = ProductsAdapter(emptyList())
        recyclerView.adapter = productsAdapter

        coroutineScope.launch {
            try {
                val products = fetchProducts(category)
                productsAdapter.updateProducts(products)
            } catch (e: Exception) {
                Toast.makeText(this@ProductActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun fetchProducts(category: String?): List<Product> = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://fakestoreapi.com/products${if (category != null) "/category/$category" else ""}")
            .build()
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        val responseBody = response.body?.string()

        responseBody?.let {
            val gson = Gson()
            return@withContext gson.fromJson(it, Array<Product>::class.java).toList()
        }
        return@withContext emptyList<Product>()
    }
}
