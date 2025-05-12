package com.example.newsapp.data.local

import android.content.SharedPreferences
import com.example.newsapp.utils.Constant
import javax.inject.Inject
import androidx.core.content.edit
import com.example.newsapp.utils.Constant.Companion.EMAIL
import com.example.newsapp.utils.Constant.Companion.USER_ID

class SharedPreferenceHelper @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    fun setUserId(userId:String){
        sharedPreferences.edit{
            putString(USER_ID, userId)
            apply()
        }
    }

    fun getUserId() = sharedPreferences.getString(USER_ID, "")

    fun setEmail(email: String){
        sharedPreferences.edit{
            putString(EMAIL, email)
            apply()
        }
    }
    fun getEmail() = sharedPreferences.getString(EMAIL, "")

}