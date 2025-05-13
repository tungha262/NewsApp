package com.example.newsapp.presentation.ui.component.category

import android.util.Log
import androidx.fragment.app.viewModels
import com.example.newsapp.databinding.FragmentCategoryBinding
import com.example.newsapp.domain.state.Resource
import com.example.newsapp.presentation.base.BaseFragment
import com.example.newsapp.presentation.viewModel.RemoteViewModel
import com.example.newsapp.utils.Constant
import com.example.ui_news.util.CustomToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryFragment : BaseFragment<FragmentCategoryBinding>(FragmentCategoryBinding::inflate) {
    private val viewModel: RemoteViewModel by viewModels()
    private var category: String = "top"
    override fun initUi() {
        category = arguments?.getString(Constant.CATEGORY) ?: "top"
        Log.d("tung", category)
        viewModel.getArticles(category)
    }

    override fun initListener() {

    }

    override fun observerViewModel() {
        super.observerViewModel()
        viewModel.article.observe(viewLifecycleOwner) { rs ->
            when(rs){
                is Resource.Failed -> {
//                    CustomProgress.hide()
                    Log.d("tung", rs.message)
                    CustomToast.makeText(requireContext(), CustomToast.FAILED, rs.message).show()
                }
                is Resource.Loading -> {
//                    CustomProgress.show(requireActivity())
                }
                is Resource.Success -> {
//                    CustomProgress.hide()
                    CustomToast.makeText(requireContext(), CustomToast.SUCCESS, rs.data.size.toString()).show()
                    for(i in rs.data){
                        Log.d("tung", i.toString())
                    }
                }
            }
        }
    }
}