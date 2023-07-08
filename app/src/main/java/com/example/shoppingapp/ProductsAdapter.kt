package com.example.shoppingapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ProductsAdapter(private var products: List<Product>) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val price: TextView = view.findViewById(R.id.price)
        private val imageView: ImageView = view.findViewById(R.id.image)

        fun bind(product: Product) {
            title.text = product.title
            price.text = String.format("Price: €%.2f", product.price)

            Picasso.get()
                .load(product.image)
                .into(imageView)

            view.setOnClickListener {
                val intent = Intent(view.context, DetailsActivity::class.java).apply {
                    putExtra("Product", product)
                }
                view.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.products, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}
