package com.example.shoppingapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key.Companion.Notification
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso

class CartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val product: Product? = intent.getSerializableExtra("Product") as? Product
        val quantity: Int = intent.getIntExtra("Quantity", 0)

        product?.let {
            val productImageView = findViewById<ImageView>(R.id.cartProductImage)
            val productTitleTextView = findViewById<TextView>(R.id.cartProductTitle)
            val productPriceTextView = findViewById<TextView>(R.id.cartProductPrice)
            val productQuantityTextView = findViewById<TextView>(R.id.cartProductQuantity)
            val totalAmount = findViewById<TextView>(R.id.total_price)
            val checkoutButton = findViewById<TextView>(R.id.checkout_button)
            val total = quantity * it.price

            productTitleTextView.text = it.title
            productQuantityTextView.text = "Quantity: $quantity"
            productPriceTextView.text = "Price: ${it.price}"
            totalAmount.text = "Total Amount: $total"

            Picasso
                .get()
                .load(it.image)
                .into(productImageView)

            checkoutButton.setOnClickListener {
                val intent = Intent(applicationContext, ProductActivity::class.java)
                intent.putExtra("Product", product)
                intent.putExtra("Quantity", quantity)
                startActivity(intent)

                showOrderNotification()
            }
        }

        setupBottomNavigationView()
    }

    private fun setupBottomNavigationView() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavView)
        bottomNavigationView.selectedItemId = R.id.cart

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
            item.itemId == R.id.cart || navigationMap.containsKey(item.itemId)
        }
    }

    companion object {
        const val CHANNEL_ID = "channel_id"
        const val NOTIFICATION_ID = 1
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Channel"
            val descriptionText = "Channel Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showOrderNotification() {
        createNotificationChannel()

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_active_24)
            .setContentTitle("Order placed")
            .setContentText("Your order has been placed!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@CartActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(NOTIFICATION_ID, builder.build())
        }
    }
}
