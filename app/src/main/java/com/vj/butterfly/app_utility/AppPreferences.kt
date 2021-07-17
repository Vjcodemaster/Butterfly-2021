package com.vj.butterfly.app_utility

import android.content.Context
import android.content.SharedPreferences


object AppPreferences {
    private const val APP_PREFERENCES = "BUTTERFLY_PREFERENCES"

    private const val PRIVATE_MODE = 0

    private lateinit var sharedPreferences: SharedPreferences

    private val IS_LOGGED_IN = Pair("IS_LOGGED_IN", false)

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE)
    }

    /**
     * SharedPreferences extension function, so we won't need to call edit() and apply()
     * ourselves on every SharedPreferences operation.
     */
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var loggedIn: Boolean
        // custom getter to get a preference of a desired type, with a predefined default value
        get() = sharedPreferences.getBoolean(IS_LOGGED_IN.first, IS_LOGGED_IN.second)
        // custom setter to save a preference back to preferences file
        set(value) = sharedPreferences.edit {
            it.putBoolean(IS_LOGGED_IN.first, value)
        }

}