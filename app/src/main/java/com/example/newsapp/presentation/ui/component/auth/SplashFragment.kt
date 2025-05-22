package com.example.newsapp.presentation.ui.component.auth

import android.os.Handler
import android.os.Looper
import androidx.navigation.fragment.findNavController
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentSplashBinding
import com.example.newsapp.presentation.base.BaseFragment
import com.example.newsapp.utils.FormatDateTime
import com.example.newsapp.utils.NavOptionsConfig
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate) {

    override fun initUi() {
        binding.apply {
            tvCurrentDate.text = FormatDateTime.getCurrentDate()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) {
                findNavController().navigate(
                    R.id.action_splashFragment_to_homeFragment,
                    null,
                    NavOptionsConfig.getFadeAnimWithPopUpTo(R.id.splashFragment, true)
                )
            }
        }, 2000)
    }
    override fun initListener() {}

}