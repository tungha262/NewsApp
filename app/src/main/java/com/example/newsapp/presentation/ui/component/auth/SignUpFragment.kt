package com.example.newsapp.presentation.ui.component.auth

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentSignUpBinding
import com.example.newsapp.domain.state.Resource
import com.example.newsapp.presentation.base.BaseFragment
import com.example.newsapp.presentation.viewModel.AuthViewModel
import com.example.newsapp.utils.CustomToast
import com.example.newsapp.utils.NavOptionsConfig
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>(FragmentSignUpBinding::inflate) {

    private val viewModel: AuthViewModel by viewModels()

    override fun initListener() {
        binding.tvLogin.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSignUp.setOnClickListener {
            binding.apply {
                viewModel.signup(
                    edtSignUpName.text.toString(),
                    edtSignUpEmail.text.toString(),
                    edtSignUpPassword.text.toString()
                )
            }
        }
    }

    override fun observerViewModel() {
        super.observerViewModel()
        lifecycleScope.launch {
            viewModel.signupState.collect { state ->
                when (state) {
                    is Resource.Success -> {
                        CustomToast.makeText(requireContext(), CustomToast.SUCCESS, state.data).show()
                        Log.d("TUNG", FirebaseAuth.getInstance().uid.toString())
                        findNavController().navigate(
                            R.id.action_signUpFragment_to_signInFragment,
                            null,
                            NavOptionsConfig.getSlideAnimWithPopUpTo(R.id.signUpFragment)
                        )
                    }
                    is Resource.Failed -> {
                        CustomToast.makeText(requireContext(), CustomToast.FAILED, state.message).show()
                    }
                    Resource.Loading -> {}
                }
            }
        }
    }
    override fun initUi() {

    }
}