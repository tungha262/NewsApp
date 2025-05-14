package com.example.newsapp.presentation.ui.component.category

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.databinding.FragmentCategoryBinding
import com.example.newsapp.domain.state.Resource
import com.example.newsapp.network.NetworkConfig
import com.example.newsapp.presentation.base.BaseFragment
import com.example.newsapp.presentation.viewModel.RemoteViewModel
import com.example.newsapp.utils.Constant
import com.example.newsapp.utils.DialogNetworkError
import com.example.ui_news.util.CustomProgress
import com.example.ui_news.util.CustomToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryFragment : BaseFragment<FragmentCategoryBinding>(FragmentCategoryBinding::inflate) {
    private val viewModel: RemoteViewModel by viewModels()
    private var category: String = "top"
    private lateinit var adapter: CategoryAdapter

    private var networkDialog: DialogNetworkError? = null
    private var isFragmentVisible: Boolean = false

    override fun onResume() {
        super.onResume()
        isFragmentVisible = true
        if (NetworkConfig.isInternetConnected(requireContext())) {
            if (viewModel.getCurrentData(category).isEmpty()) {
                viewModel.getArticles(category)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        isFragmentVisible = false
    }

    override fun initUi() {
        category = arguments?.getString(Constant.CATEGORY) ?: "top"
        Log.d("tung", category)
        adapter = CategoryAdapter()

        binding.rcvCategory.apply {
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            layoutManager = LinearLayoutManager(requireContext())
        }
        viewModel.getArticles(category)
    }

    override fun initListener() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            val hasNetwork = NetworkConfig.isInternetConnected(requireContext())

            if (hasNetwork) {
                if (viewModel.getCurrentData(category).isEmpty()) {
                    viewModel.refreshCategory(category)
                    viewModel.getArticles(category)
                } else {
                    binding.swipeRefreshLayout.postDelayed({
                        binding.swipeRefreshLayout.isRefreshing = false
                    }, 1000)
                }

            } else {
                binding.swipeRefreshLayout.isRefreshing = false
                if (isFragmentVisible  && networkDialog == null) {
                    networkDialog = DialogNetworkError {
                        viewModel.refreshCategory(category)
                    }
                    networkDialog!!.show(childFragmentManager, "DialogNetworkError")
                }
            }
        }
    }

    override fun observerViewModel() {
        super.observerViewModel()
        binding.swipeRefreshLayout.isRefreshing = false
        viewModel.article.observe(viewLifecycleOwner) { rs ->
            when (rs) {
                is Resource.Failed -> {
                    CustomProgress.hide()
                    if (!NetworkConfig.isInternetConnected(requireContext())) {
                        if (isFragmentVisible  && networkDialog == null) {
                            Log.d("tung", "$category - dialog error")
                            networkDialog = DialogNetworkError {
                                viewModel.refreshCategory(category)
                            }
                            networkDialog!!.show(childFragmentManager, "DialogNetworkError")
                        }
                    } else {
                        CustomToast.showCustomToastShort(
                            requireContext(),
                            CustomToast.FAILED,
                            rs.message
                        )
                    }
                    Log.d("tung", rs.message)

                }

                is Resource.Loading -> {
                    if(!binding.swipeRefreshLayout.isRefreshing){
                        CustomProgress.show(requireActivity())
                    }
                }

                is Resource.Success -> {
                    CustomProgress.hide()
                    val dialog =
                        childFragmentManager.findFragmentByTag("DialogNetworkError") as? DialogNetworkError
                    if (dialog != null) {
                        Log.d("tung", "$category - dialog success")
                        dialog.dismiss()
                        networkDialog = null
                    }
                    adapter.setData(rs.data)
                    binding.rcvCategory.adapter = adapter
                    for (i in rs.data) {
                        Log.d("tung", i.toString())
                    }

                }
            }
        }
    }
}