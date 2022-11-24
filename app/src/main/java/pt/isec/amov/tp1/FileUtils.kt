package pt.isec.amov.tp1

import android.content.Context
import android.util.Log

fun saveUsername(context: Context, username: String) {
    val prefs = context.getSharedPreferences("pt.isec.amov.tp1", Context.MODE_PRIVATE)
    prefs.edit().putString("username", username).apply()
}

fun getUsername(context: Context) : String? {
    val prefs = context.getSharedPreferences("pt.isec.amov.tp1", Context.MODE_PRIVATE)
    return prefs.getString("username", null)
}