package com.example.newsapp.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.newsapp.utils.Constant.Companion.USER_NAME
import com.example.newsapp.utils.Constant.Companion.THEME
import javax.inject.Inject

class SharedPreferenceHelper @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    fun setUserName(userId:String, userName: String){
        sharedPreferences.edit{
            putString(USER_NAME+userId, userName)
            apply()
        }
    }

    fun getUserName(userId:String) = sharedPreferences.getString(USER_NAME+userId, "")

    fun setTheme(isDark : Boolean){
        sharedPreferences.edit {
            putBoolean(THEME, isDark)
            apply()
        }
    }

    fun getTheme() = sharedPreferences.getBoolean(THEME, false)

}