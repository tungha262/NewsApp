package com.example.newsapp.presentation.ui.component.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import com.example.newsapp.R
import com.example.newsapp.data.local.SharedPreferenceHelper
import com.example.newsapp.databinding.FragmentSettingBinding
import com.example.newsapp.presentation.base.BaseFragment
import com.example.newsapp.presentation.ui.MainActivity
import com.example.newsapp.utils.Constant.Companion.THEME
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate) {

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var sharedPreferences: SharedPreferenceHelper

    private lateinit var bottomNav: BottomNavigationView

    override fun initListener() {

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                bottomNav.visibility = View.GONE
                binding.btnCancel.visibility = View.VISIBLE
                binding.layoutContent.visibility = View.GONE
                binding.searchContainer.apply {
                    val params = layoutParams as LinearLayout.LayoutParams
                    params.weight = 0.8f
                    layoutParams = params
                }
            } else {
                bottomNav.visibility = View.VISIBLE
            }
        }

        binding.btnCancel.setOnClickListener {
            binding.apply {
                layoutContent.visibility = View.VISIBLE
                searchEditText.text.clear()
                btnCancel.visibility = View.GONE
                searchContainer.apply {
                    val params = layoutParams as LinearLayout.LayoutParams
                    params.weight = 1f
                    layoutParams = params
                }
                // hide keyboard
                val view = requireActivity().currentFocus
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                imm.hideSoftInputFromWindow(view?.windowToken, 0)
                searchEditText.clearFocus()
            }
        }

        // Lắng nghe sự kiện back
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    binding.apply {
                        // hide keyboard
                        val view = requireActivity().currentFocus
                        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE)
                                as InputMethodManager
                        imm.hideSoftInputFromWindow(view?.windowToken, 0)
                        searchEditText.clearFocus()
                    }
                }
            }
        )

        binding.layoutChangePassword.setOnClickListener {

        }

        binding.layoutAuth.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_signInFragment)
        }

        binding.layoutLogout.setOnClickListener {
            showAlertDialog()
        }

        binding.switchDark.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.setTheme(isChecked)
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            requireActivity().recreate()
        }
    }


    override fun initUi() {
        bottomNav = (requireActivity() as MainActivity)
            .findViewById<BottomNavigationView>(R.id.bottom_nav_view)

        val isDarkMode = sharedPreferences.getTheme()

        binding.switchDark.isChecked = isDarkMode


        val currentUser = auth.currentUser
        if (currentUser == null) {
            binding.apply {
                layoutProfile.visibility = View.GONE
                layoutLogout.visibility = View.GONE
                layoutAuth.visibility = View.VISIBLE
                layoutChangePassword.visibility = View.GONE
            }
        } else {
            binding.apply {
                layoutProfile.visibility = View.VISIBLE
                layoutLogout.visibility = View.VISIBLE
                layoutAuth.visibility = View.GONE
                layoutChangePassword.visibility = View.VISIBLE

                val userId = currentUser.uid
                userName.text = sharedPreferences.getUserName(userId)
                userEmail.text = currentUser.email
            }

        }
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Đăng xuất")
            .setMessage("Bạn chắc chắn muốn đăng xuất tài khoản không?")
            .setPositiveButton("Có") { _, _ ->
                auth.signOut()
                findNavController().navigate(R.id.settingFragment)
                Log.d("tung", "log out ${auth.currentUser!!.uid}")
            }
            .setNegativeButton("Không", null)
            .show()
    }

}