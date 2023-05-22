package com.example.feedarticlescompose.utils

import SHAREDPREF_NAME
import SHAREDPREF_SESSION_TOKEN
import SHAREDPREF_SESSION_USER_ID
import android.content.Context
import android.content.SharedPreferences

class MySharedPref(context: Context) {


    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SHAREDPREF_NAME, Context.MODE_PRIVATE)

    private fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    private fun getString(key: String, defaultValue: String?): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    private fun saveLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

    private fun getInt(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }


    fun getUserId(): Int {
        return getInt(SHAREDPREF_SESSION_USER_ID, 0)
    }

    fun getToken(): String? {
        return getString(SHAREDPREF_SESSION_TOKEN, null)
    }

    fun saveUserId(userId: Long) {
        saveLong(SHAREDPREF_SESSION_USER_ID, userId)
    }

    fun saveToken(token: String) {
        saveString(SHAREDPREF_SESSION_TOKEN, token)
    }

    fun clearSharedPref() {
        with(sharedPreferences) {
            edit()
                .remove(SHAREDPREF_SESSION_TOKEN)
                .remove(SHAREDPREF_SESSION_USER_ID)
                .apply()
        }
    }
}