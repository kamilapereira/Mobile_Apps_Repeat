package com.example.shoppingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initUI()
    }

    private fun initUI() {
        val email = findViewById<EditText>(R.id.email)
        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val confirmPassword = findViewById<EditText>(R.id.confirm_password)
        val register = findViewById<Button>(R.id.register_btn)
        val login = findViewById<Button>(R.id.login_btn)

        login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        register.setOnClickListener {
            val emailString = email.text.toString()
            val usernameString = username.text.toString()
            val passwordString = password.text.toString()
            val confirmPasswordString = confirmPassword.text.toString()

            if (isInputValid(emailString, usernameString, passwordString, confirmPasswordString)) {
                registerUser(emailString, usernameString, passwordString)
            }
        }
    }

    private fun isInputValid(email: String, username: String, password: String, confirmPassword: String): Boolean {
        if (email.isEmpty() || !email.contains("@")) {
            showToast("Invalid email")
            return false
        } else if (username.isEmpty() || !username.matches(Regex("^[a-zA-Z0-9_]+$"))) {
            showToast("Invalid username")
            return false
        } else if (password.isEmpty() || password.length < 6) {
            showToast("Invalid password")
            return false
        } else if (password != confirmPassword) {
            showToast("Passwords don't match")
            return false
        }
        return true
    }

    private fun registerUser(email: String, username: String, password: String) {
        val json = JSONObject()
            .put("email", email)
            .put("username", username)
            .put("password", password)
            .put("name", JSONObject()
                .put("firstname", "Kamila")
                .put("lastname", "Pereira"))
            .put("address", JSONObject()
                .put("city", "Dublin")
                .put("street", "O'Connell Street")
                .put("number", 3)
                .put("zipcode", "D04 89GR")
                .put("geolocation", JSONObject()
                    .put("lat", "53.3498")
                    .put("long", "6.2603")))
            .put("phone", "1-570-236-7033")

        val body = json.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("https://fakestoreapi.com/users")
            .post(body)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                showToast("Registration error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        println(responseBody)
                        runOnUiThread {
                            startActivity(Intent(this@RegisterActivity, HomeActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                            finish()
                            showToast("User registered successfully")
                        }
                    }
                } else {
                    showToast("Registration unsuccessful")
                }
            }
        })
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
