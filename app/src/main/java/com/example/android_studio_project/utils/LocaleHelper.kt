package com.example.android_studio_project.utils

import android.content.Context
import android.content.SharedPreferences
import java.util.*

object LocaleHelper {

    fun loadLocale(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val language = sharedPreferences.getString("Language", "en")
        setLocale(context, language ?: "en")
    }

    private fun getSavedLanguagePreference(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("selected_language", "en") ?: "en"
    }

    private fun saveLanguagePreference(context: Context, language: String) {
        val sharedPreferences = context.getSharedPreferences("LanguagePreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("selected_language", language)
        editor.apply()
    }

    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("Language", languageCode)
        editor.apply()
    }
}
