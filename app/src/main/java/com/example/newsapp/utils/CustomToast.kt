package com.example.ui_news.util

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.newsapp.R

class CustomToast(context: Context) : Toast(context) {
    companion object{
        const val SUCCESS = 1
        const val FAILED = 2


        fun makeText(context: Context, type:Int, message:String) : Toast{
            val toast = Toast(context)
            val layout = LayoutInflater.from(context).inflate(R.layout.custom_toast, null, false)
            val iconMain = layout.findViewById<ImageView>(R.id.icon_main)
            val mess = layout.findViewById<TextView>(R.id.tv_toast_message)
            mess.text = message
            when(type){
                1 -> {
                    iconMain.setImageResource(R.drawable.success)
                    layout.setBackgroundResource(R.drawable.custom_toast_success)
                }
                2 -> {
                    iconMain.setImageResource(R.drawable.error)
                    layout.setBackgroundResource(R.drawable.custom_toast_failed)
                }
            }
            toast.apply {
                view = layout
                duration = LENGTH_SHORT
                setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 100)
            }
            return toast
        }
    }


}