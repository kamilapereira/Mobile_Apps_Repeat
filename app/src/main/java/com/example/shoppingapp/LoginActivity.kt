package com.example.shoppingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        checkUserToken()
        initUI()
    }

    private fun checkUserToken() {
        val userToken = SharedPref.getInstance(this).getUserToken()
        userToken?.let {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    private fun initUI() {
        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val btnLogin = findViewById<Button>(R.id.login_btn)
        val btnRegister = findViewById<Button>(R.id.register_btn)

        btnLogin.setOnClickListener {
            val usernameText = username.text.toString().trim()
            val passwordText = password.text.toString()
            if (isInputValid(usernameText, passwordText)) {
                loginUser(usernameText, passwordText)
            }
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun isInputValid(username: String, password: String): Boolean {
        return when {
            username.isBlank() -> {
                showToast("Invalid username")
                false
            }
            password.length < 6 -> {
                showToast("Password should be at least 6 characters long")
                false
            }
            else -> true
        }
    }

    private fun loginUser(username: String, password: String) {
        val requestBody = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url("https://fakestoreapi.com/auth/login")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                showToast("Login error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let {
                        val token = gson.fromJson(it, Token::class.java)
                        SharedPref.getInstance(this@LoginActivity).saveUserToken(token.token)
                        getUser(username, token.token)
                    }
                } else {
                    showToast("Login unsuccessful")
                }
            }
        })
    }

    private fun getUser(username: String, token: String) {
        val request = Request.Builder()
            .url("https://fakestoreapi.com/users")
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                showToast("Error occurred: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let {
                        val listUserType = object : TypeToken<List<User>>() {}.type
                        val users: List<User> = gson.fromJson(it, listUserType)

                        val user = users.find { it.username == username }
                        if (user != null) {
                            SharedPref.getInstance(this@LoginActivity).saveUserId(user.id)
                            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                            finish()
                        } else {
                            showToast("User not found")
                        }
                    }
                } else {
                    showToast("Failed to fetch user data")
                }
            }
        })
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
        }
    }
}
