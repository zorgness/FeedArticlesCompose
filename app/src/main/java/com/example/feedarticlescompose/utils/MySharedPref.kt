package com.example.feedarticlescompose.utils

import SHAREDPREF_NAME
import SHAREDPREF_SESSION_TOKEN
import SHAREDPREF_SESSION_USER_ID
import android.content.Context
import android.content.SharedPreferences

class MySharedPref(context: Context) {


    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SHAREDPREF_NAME, Context.MODE_PRIVATE)


/*    var token: String?
    get() = sharedPreferences.getString(SHAREDPREF_SESSION_TOKEN, null)
    set(value) = sharedPreferences.edit().putString(SHAREDPREF_SESSION_TOKEN, value).apply()

    var userId: Long?
        get() = sharedPreferences.getLong(SHAREDPREF_SESSION_USER_ID, 0L)
        set(value) = sharedPreferences.edit().putLong(SHAREDPREF_SESSION_USER_ID, value ?: 0L).apply()*/

    private fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    private fun getString(key: String, defaultValue: String?): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    private fun saveLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

    private fun getLong(key: String, defaultValue: Long): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }


    fun getUserId(): Long {
        return getLong(SHAREDPREF_SESSION_USER_ID, 0)
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