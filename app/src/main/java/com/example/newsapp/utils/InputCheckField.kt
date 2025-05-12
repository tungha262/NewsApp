package com.example.newsapp.utils


class InputCheckField {
    companion object{
        fun isValidEmail(email: String): Boolean {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
        fun isValidPassword(password: String): Boolean {
            val regex = "^(?=\\S+$).{6,}"
            return Regex(regex).matches(password)
        }
    }
}