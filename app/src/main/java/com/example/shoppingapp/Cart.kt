package com.example.shoppingapp

import java.io.Serializable

data class Cart(
    val id: String,
    val date: String,
    val products: List<ProductWithQuantity>
) : Serializable {
    data class ProductWithQuantity(
        var quantity: Int,
        var productId: Int,
        var product: Product? = null
    ) : Serializable
}
