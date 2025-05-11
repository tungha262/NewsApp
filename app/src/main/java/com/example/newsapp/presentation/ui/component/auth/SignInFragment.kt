package com.example.newsapp.presentation.ui.component.auth

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentSignInBinding
import com.example.newsapp.domain.state.Resource
import com.example.newsapp.presentation.base.BaseFragment
import com.example.newsapp.presentation.viewModel.AuthViewModel
import com.example.ui_news.util.CustomProgress
import com.example.ui_news.util.CustomToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : BaseFragment<FragmentSignInBinding>(FragmentSignInBinding::inflate) {

    private val viewModel: AuthViewModel by viewModels()

    override fun initListener() {
        binding.apply {
            tvForgotPassword.setOnClickListener {

            }

            tvSignUp.setOnClickListener {
                findNavController().navigate(R.id.action_signInFragment_to_signUpFragment, null)
            }

            btnLogin.setOnClickListener {
                val email = binding.edtLoginEmail.text.toString()
                val password = binding.edtLoginPassword.text.toString()
                viewModel.login(email, password)
            }
        }
    }

    override fun initUi() {

    }

    override fun observerViewModel() {
        super.observerViewModel()
        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> {
                    CustomProgress.show(requireActivity())
                }

                is Resource.Success -> {
                    CustomProgress.hide()
                    CustomToast.makeText(requireContext(), CustomToast.SUCCESS, state.data).show()
                }

                is Resource.Failed -> {
                    CustomProgress.hide()
                    CustomToast.makeText(requireContext(), CustomToast.FAILED, state.message).show()
                }
            }
        }


    }
}