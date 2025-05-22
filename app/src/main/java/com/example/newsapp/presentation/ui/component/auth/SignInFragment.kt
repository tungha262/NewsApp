package com.example.newsapp.presentation.ui.component.auth

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentSignInBinding
import com.example.newsapp.domain.state.Resource
import com.example.newsapp.presentation.base.BaseFragment
import com.example.newsapp.presentation.viewModel.AuthViewModel
import com.example.newsapp.utils.CustomProgress
import com.example.newsapp.utils.CustomToast
import com.example.newsapp.utils.NavOptionsConfig
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : BaseFragment<FragmentSignInBinding>(FragmentSignInBinding::inflate) {

    private val viewModel: AuthViewModel by viewModels()

    override fun initListener() {
        binding.apply {
            tvForgotPassword.setOnClickListener {
                findNavController().navigate(
                    R.id.action_signInFragment_to_forgotPasswordFragment,
                    null,
                    NavOptionsConfig.getSlideAnim()
                )
            }
            tvSignUp.setOnClickListener {
                findNavController().navigate(
                    R.id.action_signInFragment_to_signUpFragment,
                    null,
                    NavOptionsConfig.getSlideAnim()
                )
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
                    findNavController().navigate(
                        R.id.action_signInFragment_to_homeFragment,
                        null,

                        NavOptionsConfig.getSlideAnimWithPopUpTo(findNavController().graph.startDestinationId, true)
                    )
                }

                is Resource.Failed -> {
                    CustomProgress.hide()
                    CustomToast.makeText(requireContext(), CustomToast.FAILED, state.message).show()

                }
            }
        }


    }
}