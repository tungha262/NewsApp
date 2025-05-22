package com.example.newsapp.utils

import androidx.navigation.NavOptions
import com.example.newsapp.R

class NavOptionsConfig {
    companion object{
        fun getFadeAnim(): NavOptions {
            return NavOptions.Builder()
                .setEnterAnim(R.anim.fade_in)
                .setExitAnim(R.anim.fade_out)
                .setPopEnterAnim(R.anim.fade_in)
                .setPopExitAnim(R.anim.fade_out)
                .build()
        }

        fun getSlideAnim() : NavOptions{
            return NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_left)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_right)
                .setPopExitAnim(R.anim.slide_out_right)
                .build()
        }
        fun getFadeAnimWithPopUpTo(
            popUpToDestinationId: Int,
            inclusive: Boolean = true
        ): NavOptions {
            return NavOptions.Builder()
                .setEnterAnim(R.anim.fade_in)
                .setExitAnim(R.anim.fade_out)
                .setPopEnterAnim(R.anim.fade_in)
                .setPopExitAnim(R.anim.fade_out)
                .setPopUpTo(popUpToDestinationId, inclusive)
                .build()
        }

        fun getSlideAnimWithPopUpTo(
            popUpToDestinationId: Int,
            inclusive: Boolean = true
        ): NavOptions {
            return NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_left)
                .setPopExitAnim(R.anim.slide_out_right)
                .setPopUpTo(popUpToDestinationId, inclusive)
                .build()
        }
    }
}