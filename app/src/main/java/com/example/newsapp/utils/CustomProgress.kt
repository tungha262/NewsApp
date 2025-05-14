package com.example.ui_news.util

import android.app.Activity
import android.app.ProgressDialog
import android.view.Gravity
import com.example.newsapp.R

@Suppress("DEPRECATION")
class CustomProgress {
    companion object{
        private var progressDialog: ProgressDialog? = null
        fun show(activity: Activity) {
            if (progressDialog?.isShowing == true) return

            progressDialog = ProgressDialog(activity).apply {
                setCanceledOnTouchOutside(false)
                show()
                setContentView(R.layout.dialog_progress)
                window?.apply {
                    setBackgroundDrawableResource(android.R.color.transparent)
                    clearFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    setDimAmount(0f)
                    val params = attributes
                    params.gravity = Gravity.CENTER
                    attributes = params
                }
            }
        }
        fun hide(){
            progressDialog?.let {
                if(it.isShowing){
                    it.dismiss()
                }
                progressDialog = null
            }
        }
    }
}