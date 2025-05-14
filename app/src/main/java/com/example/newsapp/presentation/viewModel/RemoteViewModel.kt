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

    private var nextPageMap = mutableMapOf<String, String?>()
    private var articleMap = mutableMapOf<String, MutableList<Article>>()
    private var isLastPageMap = mutableMapOf<String, Boolean>()

    private val _articles = MutableLiveData<Resource<List<Article>>>()
    val article: LiveData<Resource<List<Article>>> get() = _articles

    fun getArticles(category: String) {
        viewModelScope.launch {

            val nextPage = nextPageMap[category]
            val currentArticles = articleMap[category] ?: mutableListOf()

            if (currentArticles.isNotEmpty() && isLastPageMap[category] == true) {
                _articles.value = Resource.Success(currentArticles)
                return@launch
            }

            remoteRepository.getArticles(category, nextPage).collect { rs ->
                when (rs) {
                    is Resource.Loading -> _articles.value = Resource.Loading

                    is Resource.Success -> {
                        val newArticles = rs.data.results
                        val updatedList = currentArticles.toMutableList().apply {
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


    fun refreshCategory(category: String) {
        nextPageMap.remove(category)
        articleMap.remove(category)
        isLastPageMap.remove(category)
        getArticles(category)
    }

    fun getCurrentData(category: String) : List<Article>{
        return articleMap[category] ?: emptyList()
    }

}