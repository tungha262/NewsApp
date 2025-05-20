package com.example.newsapp.presentation.ui.component.category

import android.util.Log
import android.widget.AbsListView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentCategoryBinding
import com.example.newsapp.domain.state.Resource
import com.example.newsapp.network.NetworkConfig
import com.example.newsapp.presentation.base.BaseFragment
import com.example.newsapp.presentation.ui.component.home.HomeFragmentDirections
import com.example.newsapp.presentation.viewModel.RemoteViewModel
import com.example.newsapp.utils.Constant
import com.example.newsapp.utils.DialogNetworkError
import com.example.ui_news.util.CustomProgress
import com.example.ui_news.util.CustomToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryFragment : BaseFragment<FragmentCategoryBinding>(FragmentCategoryBinding::inflate) {

    private val viewModel: RemoteViewModel by viewModels()
    private var category: String = "top"
    private lateinit var adapter: CategoryAdapter

    private var networkDialog: DialogNetworkError? = null
    private var isFragmentVisible: Boolean = false
    private var isLoadingMore = false
    private var isLastPage = false

    override fun initUi() {
        category = arguments?.getString(Constant.CATEGORY) ?: "top"
        Log.d("tung", "$category init ui")
        adapter = CategoryAdapter()
        binding.rcvCategory.adapter = adapter

        binding.rcvCategory.apply {
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            layoutManager = LinearLayoutManager(requireContext())
        }
        if (NetworkConfig.isInternetConnected(requireContext())) {
            if (viewModel.getCurrentData(category).isEmpty()) {
                Log.d("tung", "call api")
                viewModel.getArticles(category)
            }
        }

    }

    override fun initListener() {

        //Refresh layout
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            val hasNetwork = NetworkConfig.isInternetConnected(requireContext())

            if (hasNetwork) {
                if (viewModel.getCurrentData(category).isEmpty()) {
                    Log.d("tung", "Refresh with no data")
                    viewModel.getArticles(category)
                    binding.swipeRefreshLayout.isRefreshing = false
                } else {
                    binding.swipeRefreshLayout.postDelayed({
                        binding.swipeRefreshLayout.isRefreshing = false
                    }, 1000)

                }

            } else {
                binding.swipeRefreshLayout.isRefreshing = false
                if (isFragmentVisible && networkDialog == null) {
                    networkDialog = DialogNetworkError {
                        Log.d("tung", "Refresh data network error")
                        viewModel.refreshCategory(category)
                    }
                    networkDialog!!.show(childFragmentManager, "DialogNetworkError")
                }
            }
        }

        // keo recyclerView
        binding.rcvCategory.apply {
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                    val isScrollingDown = dy > 0

                    val shouldLoad = !isLoadingMore && !isLastPage && firstItem >= 0
                            && isAtEnd && isScrollingDown

                    if (shouldLoad) {
                        if (NetworkConfig.isInternetConnected(requireContext())) {
                            isLoadingMore = true
                            Log.d("tung", "Load more")
                            viewModel.getArticles(category)
                        } else {
                            if (isFragmentVisible && networkDialog == null) {
                                Log.d("tung", "$category - dialog error")
                                networkDialog = DialogNetworkError {
                                    viewModel.refreshCategory(category)
                                }
                                networkDialog!!.show(childFragmentManager, "DialogNetworkError")
                            }
                        }
                    }
                }
            })
        }

        // set onItemClick
        binding.apply {
            adapter.setOnItemClickListener { item ->
                val action =
                    HomeFragmentDirections.actionHomeFragmentToArticleFragment(item, category)
                findNavController().navigate(action)
            }
        }
    }

    override fun observerViewModel() {
        super.observerViewModel()
        viewModel.article.observe(viewLifecycleOwner) { rs ->
            when (rs) {
                is Resource.Failed -> {
                    CustomProgress.hide()
                    binding.swipeRefreshLayout.isRefreshing = false
                    isLoadingMore = false
                    if (!NetworkConfig.isInternetConnected(requireContext())) {
                        if (isFragmentVisible && networkDialog == null) {
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
                    if (!binding.swipeRefreshLayout.isRefreshing && !isLoadingMore) {
                        CustomProgress.show(requireActivity())
                    }
                }

                is Resource.Success -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    CustomProgress.hide()
                    val dialog =
                        childFragmentManager.findFragmentByTag("DialogNetworkError") as? DialogNetworkError
                    if (dialog != null) {
                        Log.d("tung", "$category - dialog success")
                        dialog.dismiss()
                        networkDialog = null
                    }
                    isLoadingMore = false
                    isLastPage = rs.data.isEmpty()

                    val currentData = adapter.itemCount
                    val newData = rs.data

                    if (currentData == 0) {
                        adapter.setData(newData)
                    } else if (newData.size > currentData) {
                        val moreItems = newData.subList(currentData, newData.size)
                        adapter.appendData(moreItems)
                    }
                    Log.d("tung", "setData $category")
                    Log.d("tung", rs.data.size.toString())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("tung", "resume $category")
        Log.d("tung", "${viewModel.getCurrentData(category).size} - size $category")
        isFragmentVisible = true

        if (networkDialog == null) {
            if (!NetworkConfig.isInternetConnected(requireContext())) {
                networkDialog = DialogNetworkError {
                    Log.d("tung", "Refresh data network error")
                    viewModel.refreshCategory(category)
                }
                networkDialog!!.show(childFragmentManager, "DialogNetworkError")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        isFragmentVisible = false
    }
}