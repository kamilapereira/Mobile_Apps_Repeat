package com.example.shoppingapp

data class User(
    val address: Address,
    val id: Int,
    val email: String,
    val username: String,
    val password: String,
    val name: Name,
    val phone: String,
) {
    data class Name(
        val firstname: String,
        val lastname: String
    )

    data class Address(
        val geolocation: Geolocation,
        val street: String,
        val city: String,
        val state: String,
        val zipcode: String,
    ) {
        data class Geolocation(
            val lat: String,
            val long: String
        )
    }
}


