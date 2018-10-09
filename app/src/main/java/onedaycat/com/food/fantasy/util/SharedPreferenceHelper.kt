package onedaycat.com.food.fantasy.util

import onedaycat.com.food.fantasy.common.MainApplication

private const val PREF_FILE = "PREF"

class SharedPreferenceHelper {
    companion object {
        val usernameKey = "username_key"
        val expireKey = "password_key"
        val tokenKey = "token_key"

        @JvmStatic
        fun setString(key: String, value: String) {
            val settings = MainApplication.applicationContext().getSharedPreferences(PREF_FILE, 0)
            val editor = settings.edit()
            editor.putString(key, value)
            editor.apply()
        }

        @JvmStatic
        fun checkUserAuth(key: String): String? {
            val pref = MainApplication.applicationContext().getSharedPreferences(PREF_FILE, 0)

            return pref.getString(key, null)
        }
    }
}