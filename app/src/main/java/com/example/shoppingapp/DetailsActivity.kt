package com.example.shoppingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import java.io.Serializable

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val product: Product? = intent.getSerializableExtra("Product") as? Product

        product?.let { productData ->
            with(productData) {
                findViewById<TextView>(R.id.product_title).text = title
                findViewById<TextView>(R.id.product_price).text = price.toString()
                findViewById<TextView>(R.id.product_description).text = description
                findViewById<TextView>(R.id.product_category).text = category
                findViewById<TextView>(R.id.product_rating).text = "Rating: ${rating.rate} (${rating.count} reviews)"
                Picasso.get().load(image).into(findViewById<ImageView>(R.id.product_image))

                val quantityTextView = findViewById<TextView>(R.id.product_quantity)
                findViewById<Button>(R.id.decrease_quantity).setOnClickListener {
                    val quantity = quantityTextView.text.toString().toInt()
                    if (quantity > 1) {
                        quantityTextView.text = (quantity - 1).toString()
                    }
                }

                findViewById<Button>(R.id.increase_quantity).setOnClickListener {
                    val quantity = quantityTextView.text.toString().toInt()
                    quantityTextView.text = (quantity + 1).toString()
                }

                findViewById<Button>(R.id.add_to_cart).setOnClickListener {
                    val quantity = quantityTextView.text.toString().toInt()

                    Intent(this@DetailsActivity, CartActivity::class.java).apply {
                        putExtra("Product", this@with as Serializable)
                        putExtra("Quantity", quantity)
                        startActivity(this)
                    }
                }
            }
        }
    }
}
