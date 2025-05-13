package com.example.newsapp.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.model.Article
import com.example.newsapp.domain.repo.RemoteRepository
import com.example.newsapp.domain.state.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemoteViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository
) : ViewModel() {

    private val nextPageMap = mutableMapOf<String, String?>()
    private val articleMap = mutableMapOf<String, MutableList<Article>>()
    private val isLastPageMap = mutableMapOf<String, Boolean>()

    private val _articles = MutableLiveData<Resource<List<Article>>>()
    val article: LiveData<Resource<List<Article>>> get() = _articles

    fun getArticles(category: String) {
        viewModelScope.launch {
            val nextPage = nextPageMap[category]
            val cachedArticles = articleMap[category] ?: mutableListOf()

            // Nếu đã có dữ liệu và không có trang tiếp theo => không cần gọi lại
            if (cachedArticles.isNotEmpty() && isLastPageMap[category] == true) {
                _articles.value = Resource.Success(cachedArticles)
                return@launch
            }

            remoteRepository.getArticles(category, nextPage).collect { rs ->
                when (rs) {
                    is Resource.Loading -> _articles.value = Resource.Loading
                    is Resource.Success -> {
                        val newArticles = rs.data.results
                        val updatedList = cachedArticles.toMutableList().apply {
                            addAll(newArticles)
                        }

                        articleMap[category] = updatedList
                        nextPageMap[category] = rs.data.nextPage
                        isLastPageMap[category] = newArticles.isEmpty()

                        _articles.value = Resource.Success(updatedList)
                    }
                    is Resource.Failed -> {
                        _articles.value = Resource.Failed(rs.message)
                    }
                }
            }
        }
    }

}
