package com.example.newsapp.presentation.ui.component.favorite

import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.data.model.Article
import com.example.newsapp.databinding.FragmentFavoriteBinding
import com.example.newsapp.domain.state.Resource
import com.example.newsapp.presentation.base.BaseFragment
import com.example.newsapp.presentation.viewModel.LocalViewModel
import com.example.ui_news.util.CustomToast
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FavoriteFragment : BaseFragment<FragmentFavoriteBinding>(FragmentFavoriteBinding::inflate) {

    private val localViewModel: LocalViewModel by viewModels()
    private lateinit var favoriteAdapter: FavoriteAdapter

    private lateinit var currentData: MutableList<Article>


    override fun initListener() {
        binding.layoutDeleteAll.setOnClickListener {
            showAlertDialogDeleteAll()
        }
        binding.apply {
            favoriteAdapter.setOnItemClickListener { item ->
                val action = FavoriteFragmentDirections.actionFavoriteFragmentToArticleFragment(
                    item,
                    item.category[0]
                )
                findNavController().navigate(action)
            }
        }
    }

    override fun initUi() {
        localViewModel.getAllFavoriteArticle()
        Log.d("tung", "get All  FavoriteArticle ${FirebaseAuth.getInstance().currentUser?.uid}")
        favoriteAdapter = FavoriteAdapter()
        binding.rcvListFavorite.apply {
            adapter = favoriteAdapter
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(), DividerItemDecoration.VERTICAL
                )
            )
            layoutManager = LinearLayoutManager(requireContext())
        }

        setUpSwipeAction()
    }


    override fun observerViewModel() {
        super.observerViewModel()

        localViewModel.getAllFavoriteArticle.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Failed -> {
                    CustomToast.makeText(requireContext(), CustomToast.FAILED, state.message).show()
                }

                Resource.Loading -> {}
                is Resource.Success -> {
                    val data = state.data
                    if (data.isEmpty()) {
                        binding.layoutEmpty.visibility = View.VISIBLE
                        binding.layoutDeleteAll.visibility = View.GONE
                        binding.rcvListFavorite.visibility = View.GONE
                    } else {
                        binding.layoutEmpty.visibility = View.GONE
                        binding.layoutDeleteAll.visibility = View.VISIBLE
                        binding.rcvListFavorite.visibility = View.VISIBLE
                    }
                    currentData = data as MutableList<Article>
                    favoriteAdapter.setData(data)
                }
            }
        }

        lifecycleScope.launch {
            localViewModel.deleteFavoriteArticle.collect { state ->
                when (state) {
                    is Resource.Failed -> {
                        CustomToast.makeText(
                            requireContext(),
                            CustomToast.FAILED,
                            state.message
                        ).show()
                    }

                    Resource.Loading -> {}
                    is Resource.Success -> {
                        CustomToast.makeText(requireContext(), CustomToast.SUCCESS, state.data)
                            .show()
                    }
                }
            }
        }

        lifecycleScope.launch {
            localViewModel.deleteAllFavoriteArticle.collect { state ->
                when (state) {
                    is Resource.Failed -> {
                        CustomToast.makeText(
                            requireContext(),
                            CustomToast.FAILED,
                            state.message
                        ).show()
                    }

                    Resource.Loading -> {}
                    is Resource.Success -> {
                        CustomToast.makeText(requireContext(), CustomToast.SUCCESS, state.data)
                            .show()
                    }
                }
            }
        }
    }

    private fun showAlertDialogDeleteAll() {
        AlertDialog.Builder(requireContext())
            .setTitle("Xóa tất cả mục yêu thích?")
            .setMessage("Bạn muốn xóa tất cả bài báo yêu thích?")
            .setPositiveButton("Có") { _, _ ->
                lifecycleScope.launch {
                    localViewModel.deleteAllFavoriteArticle()
                    Log.d(
                        "tung",
                        "delete All FavoriteArticle ${FirebaseAuth.getInstance().currentUser?.uid}"
                    )
                }
            }
            .setNegativeButton("Không", null)
            .show()
    }

    private fun showAlertDialogDelete(article: Article, position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xóa bài báo yêu thích?")
            .setMessage("Bạn muốn xóa bài báo yêu thích này?")
            .setPositiveButton("Có") { _, _ ->
                lifecycleScope.launch {
                    localViewModel.deleteFavoriteArticle(article)
                    Log.d(
                        "tung",
                        "delete Favorite Article ${FirebaseAuth.getInstance().currentUser?.uid}"
                    )

                }
            }
            .setNegativeButton("Không") { _, _ ->
                favoriteAdapter.notifyItemChanged(position)
            }
            .setOnCancelListener {
                favoriteAdapter.notifyItemChanged(position)
            }

            .show()
    }


    private fun setUpSwipeAction() {
        val touchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    val pos = viewHolder.layoutPosition
                    val articleDelete = currentData[pos]
                    showAlertDialogDelete(articleDelete, pos)
                }
            }
        )
        touchHelper.attachToRecyclerView(binding.rcvListFavorite)
    }
}