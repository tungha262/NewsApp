package com.example.newsapp.presentation.ui.component.auth

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentForgotPasswordBinding
import com.example.newsapp.domain.state.Resource
import com.example.newsapp.presentation.base.BaseFragment
import com.example.newsapp.presentation.viewModel.AuthViewModel
import com.example.newsapp.utils.CustomToast
import com.example.newsapp.utils.NavOptionsConfig
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordFragment : BaseFragment<FragmentForgotPasswordBinding>
    (FragmentForgotPasswordBinding::inflate) {

        private val viewModel: AuthViewModel by viewModels()

    override fun initUi() {

    }

    override fun initListener() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnGetPassword.setOnClickListener {
            viewModel.resetPassword(binding.edtForgotEmail.text.toString())
        }

    }

    override fun observerViewModel() {
        super.observerViewModel()
        lifecycleScope.launch {
            viewModel.resetPasswordState.collect { state ->
                when(state){
                    is Resource.Success -> {
                        CustomToast.makeText(requireContext(), CustomToast.SUCCESS, state.data).show()
                        findNavController().popBackStack()
                    }
                    is Resource.Failed -> {
                        CustomToast.makeText(requireContext(), CustomToast.FAILED, state.message).show()
                    }
                    is Resource.Loading -> {}
                }

            }
        }

    }

}