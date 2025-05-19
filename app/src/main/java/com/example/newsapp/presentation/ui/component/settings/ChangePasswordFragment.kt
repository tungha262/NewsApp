package com.example.newsapp.presentation.ui.component.settings

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentChangePasswordBinding
import com.example.newsapp.domain.state.Resource
import com.example.newsapp.network.NetworkConfig
import com.example.newsapp.presentation.base.BaseFragment
import com.example.newsapp.presentation.ui.MainActivity
import com.example.newsapp.presentation.viewModel.AuthViewModel
import com.example.newsapp.utils.DialogNetworkError
import com.example.ui_news.util.CustomToast
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangePasswordFragment : DialogFragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    val binding: FragmentChangePasswordBinding get() = _binding!!

    private var networkError: DialogNetworkError? = null
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var bottomNav: BottomNavigationView


    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            (resources.displayMetrics.heightPixels * 0.5).toInt()
        )
        dialog?.window?.setBackgroundDrawableResource(R.drawable.custom_dialog_change_password)
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangePasswordBinding.inflate(layoutInflater)
        observerViewModel()
        return binding.root
    }

    private fun observerViewModel() {
        lifecycleScope.launch {
            authViewModel.changePassWorkState.collect { state ->
                when (state) {
                    is Resource.Failed -> {
                        CustomToast.makeText(requireContext(), CustomToast.FAILED, state.message)
                            .show()
                    }

                    Resource.Loading -> {}

                    is Resource.Success -> {
                        dismiss()
                        CustomToast.makeText(requireContext(), CustomToast.SUCCESS, state.data)
                            .show()
                        bottomNav.visibility = View.VISIBLE
                    }
                }
            }
        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()

        initListener()
    }
    private fun initUi() {
        bottomNav = (requireActivity() as MainActivity)
            .findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNav.visibility = View.GONE
    }

    private fun initListener() {
        binding.apply {
            btnChangePassword.setOnClickListener {
                // hide keyboard
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                imm.hideSoftInputFromWindow(binding.root.windowToken, 0)

                val oldPass = edtCurrentPassword.text.toString()
                val newPass = edtNewPassword.text.toString()
                val confirmPass = edtConfirmPassword.text.toString()
                if (!NetworkConfig.isInternetConnected(requireContext())) {
                    networkError = DialogNetworkError {
                        if (NetworkConfig.isInternetConnected(requireContext())) {
                            networkError?.dismiss()
                            networkError = null
                        }
                    }
                    networkError?.show(childFragmentManager, "DialogNetworkError")
                } else {
                    authViewModel.changePassword(oldPass, newPass, confirmPass)
                }
            }

            btnClose.setOnClickListener {
                dismiss()
                bottomNav.visibility = View.VISIBLE

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}