package com.example.shoppingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

class OrderActivity : AppCompatActivity() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var sharedPreferences: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        sharedPreferences = SharedPref(this)

        setupBottomNavigationView()
        fetchAndDisplayOrders()
    }

    private fun setupBottomNavigationView() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavView)
        bottomNavigationView.selectedItemId = R.id.orders

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

    private fun fetchAndDisplayOrders() {
        val recyclerView = findViewById<RecyclerView>(R.id.ordersRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val ordersAdapter = OrdersAdapter(emptyList())
        recyclerView.adapter = ordersAdapter

        coroutineScope.launch {
            try {
                val orders = fetchOrders()
                ordersAdapter.updateOrders(orders)
            } catch (e: Exception) {
                Toast.makeText(this@OrderActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("OrderActivity", "Error fetching orders", e)
            }
        }
    }

    private suspend fun fetchOrders(): List<Cart> = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://fakestoreapi.com/carts/user/${sharedPreferences.getUserId()}")
            .build()
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()

        val carts = responseBody?.let {
            Gson().fromJson(it, Array<Cart>::class.java).toList()
        } ?: emptyList()

        val detailedCarts = carts.map { cart ->
            cart.products.forEach { productWithQuantity ->
                val productDetails = fetchProductDetails(productWithQuantity.productId) // here
                if(productDetails != null){
                    productWithQuantity.product = productDetails
                } else {
                    throw Exception("Product not found")
                }
            }
            cart
        }
        return@withContext detailedCarts
    }



    private suspend fun fetchProductDetails(productId: Int): Product? = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("https://fakestoreapi.com/products/$productId")
            .build()
        val response = OkHttpClient().newCall(request).execute()
        val responseBody = response.body?.string()
        responseBody?.let {
            Gson().fromJson(it, Product::class.java)
        }
    }
}

