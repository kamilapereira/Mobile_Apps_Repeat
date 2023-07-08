package com.example.shoppingapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class OrdersAdapter(private var orders: List<Cart>) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderId: TextView = view.findViewById(R.id.orderId)
        val orderDate: TextView = view.findViewById(R.id.orderDate)
        val productsRecyclerView: RecyclerView = view.findViewById(R.id.ordersRecyclerView2)
    }

    inner class OrderProductsAdapter(private val products: List<Cart.ProductWithQuantity>) : RecyclerView.Adapter<OrderProductsAdapter.ProductViewHolder>() {

        inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val orderProductTitle: TextView = view.findViewById(R.id.orderProductTitle)
            val orderProductQuantity: TextView = view.findViewById(R.id.orderProductQuantity)
            val orderProductPrice: TextView = view.findViewById(R.id.orderProductPrice)
            val orderProductImage: ImageView = view.findViewById(R.id.orderProductImage)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.orders_products, parent, false)
            return ProductViewHolder(view)
        }

        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            val productWithQuantity = products[position]
            val product = productWithQuantity.product!!
            holder.orderProductTitle.text = product.title
            holder.orderProductQuantity.text = "Quantity: ${productWithQuantity.quantity}"
            holder.orderProductPrice.text = "Price: ${product.price}"
            Picasso
                .get()
                .load(product.image)
                .into(holder.orderProductImage)

            holder.itemView.setOnClickListener {
                val intent = Intent(it.context, DetailsActivity::class.java).apply {
                    putExtra("Product", product)
                }
                it.context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int = products.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.orders, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.orderId.text = "Order ID: ${order.id}"
        holder.orderDate.text = "Date: ${order.date}"
        holder.productsRecyclerView.apply {
            adapter = OrderProductsAdapter(order.products)
            layoutManager = LinearLayoutManager(holder.itemView.context)
        }

        holder.itemView.setOnClickListener {
            Toast.makeText(it.context, "Order ID: ${order.id}, Date: ${order.date}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = orders.size

    fun updateOrders(newOrders: List<Cart>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}
