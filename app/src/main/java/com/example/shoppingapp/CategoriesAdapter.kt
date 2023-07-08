package com.example.shoppingapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoriesAdapter(private var categories: List<String>) : RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val categoryName: TextView = view.findViewById(R.id.categoryName)

        fun bind(category: String) {
            categoryName.text = category

            view.setOnClickListener {
                val intent = Intent(view.context, ProductActivity::class.java)
                intent.putExtra("CATEGORY", category)
                view.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.categories, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    fun updateCategories(newCategories: List<String>) {
        categories = newCategories
        notifyDataSetChanged()
    }
}
