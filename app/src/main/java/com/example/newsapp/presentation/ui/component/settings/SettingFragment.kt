package com.example.newsapp.presentation.ui.component.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.data.local.SharedPreferenceHelper
import com.example.newsapp.databinding.FragmentSettingBinding
import com.example.newsapp.domain.state.Resource
import com.example.newsapp.network.NetworkConfig
import com.example.newsapp.presentation.base.BaseFragment
import com.example.newsapp.presentation.ui.MainActivity
import com.example.newsapp.presentation.ui.component.category.CategoryAdapter
import com.example.newsapp.presentation.viewModel.RemoteViewModel
import com.example.newsapp.utils.Constant.Companion.THEME
import com.example.newsapp.utils.DialogNetworkError
import com.example.ui_news.util.CustomProgress
import com.example.ui_news.util.CustomToast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.play.integrity.internal.ac
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.Normalizer
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate) {

    private val remoteViewModel: RemoteViewModel by viewModels()
    private lateinit var adapter: SearchAdapter
    private var networkDialog: DialogNetworkError? = null

    private var isLoadingMore = false
    private var lastSearchQuery: String = ""

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var sharedPreferences: SharedPreferenceHelper

    private lateinit var bottomNav: BottomNavigationView

    private var searchJob: Job? = null

    override fun initListener() {
        binding.apply {
            adapter.setOnItemClickListener { item ->
                val action = SettingFragmentDirections
                    .actionSettingFragmentToArticleFragment(item, "")
                findNavController().navigate(action)
            }
        }


        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() == lastSearchQuery) return
                if (s.toString().isBlank() || s.toString() == "") {
                    remoteViewModel.clearSearch()
                    adapter.setData(emptyList())
                } else {
                    searchJob?.cancel()
                    searchJob = lifecycleScope.launch {
                        delay(1000)
                        lastSearchQuery = s.toString()
                        remoteViewModel.searchArticle(removeVietnameseAccents(s.toString().trim()))
                    }
                }
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

            }
        })


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
                binding.recyclerViewSearchResults.visibility = View.VISIBLE
            } else {
                bottomNav.visibility = View.VISIBLE
            }
        }

        binding.btnCancel.setOnClickListener {
            binding.apply {
                layoutContent.visibility = View.VISIBLE
                searchEditText.text.clear()
                btnCancel.visibility = View.GONE
                layoutDarkMode.visibility = View.VISIBLE

                searchContainer.apply {
                    val params = layoutParams as LinearLayout.LayoutParams
                    params.weight = 1f
                    layoutParams = params
                }
                binding.recyclerViewSearchResults.visibility = View.GONE
                binding.tvNoData.visibility = View.GONE
                remoteViewModel.clearSearch()
                adapter.setData(emptyList())
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

        binding.recyclerViewSearchResults.apply {
            addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int
                ) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layout = layoutManager as LinearLayoutManager
                    val visibleItem = layout.childCount
                    val totalItem = layout.itemCount
                    val firstItem = layout.findFirstVisibleItemPosition()

                    val isAtEnd = firstItem + visibleItem >= totalItem
                    val isScrollDown = dy > 0
                    val shouldLoad = isAtEnd && isScrollDown && !isLoadingMore &&
                            !(remoteViewModel.getLastSearchPage()) && firstItem >= 0

                    if (shouldLoad) {
                        if (NetworkConfig.isInternetConnected(requireContext())) {
                            isLoadingMore = true
                            Log.d("tung", "search load more")
                            remoteViewModel.searchArticle(binding.searchEditText.text.toString())
                        }
                        else{
                            Log.d("tung", "no internet search")
                            networkDialog = DialogNetworkError{
                                if(NetworkConfig.isInternetConnected(requireContext())){
                                    networkDialog?.dismiss()
                                    networkDialog = null
                                }
                            }
                            networkDialog!!.show(
                                requireActivity().supportFragmentManager,
                                "DialogNetworkError"
                            )
                        }
                    }
                }
            })
        }


        binding.layoutChangePassword.setOnClickListener {
            ChangePasswordFragment().show(childFragmentManager, "ChangePassword")
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
            } else {
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

        adapter = SearchAdapter()
        binding.recyclerViewSearchResults.adapter = adapter
        binding.recyclerViewSearchResults.apply {
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            layoutManager = LinearLayoutManager(requireContext())
        }


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
                Log.d("tung", "log out ")
            }
            .setNegativeButton("Không", null)
            .show()
    }

    override fun observerViewModel() {
        super.observerViewModel()
        remoteViewModel.searchArticle.observe(viewLifecycleOwner) { rs ->
            when (rs) {
                is Resource.Failed -> {
                    CustomProgress.hide()
                    isLoadingMore = false
                    if (!NetworkConfig.isInternetConnected(requireContext())) {
                        networkDialog = DialogNetworkError {
                            if (NetworkConfig.isInternetConnected(requireContext())) {
                                networkDialog?.dismiss()
                                networkDialog = null
                            }
                        }
                        networkDialog!!.show(
                            requireActivity().supportFragmentManager,
                            "DialogNetworkError"
                        )
                    } else {
                        CustomToast.makeText(requireContext(), CustomToast.FAILED, rs.message).show()
                    }
                }

                Resource.Loading -> {
                    CustomProgress.show(requireActivity())
                }

                is Resource.Success -> {
                    CustomProgress.hide()
                    isLoadingMore = false
                    val view = requireActivity().currentFocus
                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE)
                            as InputMethodManager
                    imm.hideSoftInputFromWindow(view?.windowToken, 0)
                    binding.searchEditText.clearFocus()

                    Log.d("tung", "search success ${rs.data.size}")
                    if (remoteViewModel.isClearAdapter()) {
                        adapter.setData(emptyList())
                    }
                    val currentData = adapter.itemCount
                    val newData = rs.data
                    if (currentData == 0 && newData.isEmpty()) {
                        binding.recyclerViewSearchResults.visibility = View.GONE
                        binding.tvNoData.visibility = View.VISIBLE
                    } else {
                        binding.recyclerViewSearchResults.visibility = View.VISIBLE
                        binding.tvNoData.visibility = View.GONE
                    }

                    if (currentData == 0) {
                        adapter.setData(newData)
                    } else if (newData.size > currentData) {
                        val moreItems = newData.subList(currentData, newData.size)
                        adapter.appendData(moreItems)
                    }
                }
            }
        }
    }

    private fun removeVietnameseAccents(str: String): String {
        val temp = Normalizer.normalize(str, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(temp).replaceAll("")
            .replace('đ', 'd')
            .replace('Đ', 'D')
    }

    override fun onResume() {
        super.onResume()
        if(adapter.itemCount > 0){
            binding.apply {
                layoutContent.visibility = View.GONE
                binding.btnCancel.visibility = View.VISIBLE
            }
        }
    }
}