package com.example.shoppingapp

import android.content.Context
import android.content.SharedPreferences

class SharedPref(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)

    fun saveUserToken(token: String) {
        with(sharedPreferences.edit()) {
            putString("USER_TOKEN", token)
            apply()
        }
    }

    fun getUserToken(): String? {
        return sharedPreferences.getString("USER_TOKEN", null)
    }

    fun saveUserId(userId: Int) {
        with(sharedPreferences.edit()) {
            putInt("USER_ID", userId)
            apply()
        }
    }

    fun getUserId(): Int {
        return sharedPreferences.getInt("USER_ID", -1)
    }

    fun clearAll() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }

    companion object {
        @Volatile private var INSTANCE: SharedPref? = null

        fun getInstance(context: Context): SharedPref {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SharedPref(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
