package com.example.newsapp.presentation.viewModel

import android.util.Log
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

    private val _searchArticle = MutableLiveData<Resource<List<Article>>>()
    val searchArticle : LiveData<Resource<List<Article>>> get() = _searchArticle
    private var oldQuery : String? = null
    private var nextPageSearch : String? = null
    private var searchResponse : MutableList<Article> = mutableListOf()
    private var isLastSearchPage = false
    private var query: String? = null


    fun searchArticle(query : String){
        this.query = query
        Log.d("tung", "call search with $query")
        viewModelScope.launch {
            if(query != oldQuery){
                searchResponse.clear()
                nextPageSearch = null
                oldQuery = query
                isLastSearchPage = false
            }

            if (isLastSearchPage) {
                _searchArticle.value = Resource.Success(searchResponse)
                return@launch
            }

            remoteRepository.searchArticles(query, nextPageSearch).collect { rs ->
                when(rs){
                    is Resource.Failed -> {
                        Log.d("tung", "search fail")
                        _searchArticle.value = Resource.Failed(rs.message)
                    }
                    is Resource.Loading -> _searchArticle.value = Resource.Loading
                    is Resource.Success-> {
                        Log.d("tung", "search with $query success")
                        val newSearch = rs.data.results
                        searchResponse.addAll(newSearch)
                        nextPageSearch = rs.data.nextPage
                        isLastSearchPage = searchResponse.isEmpty() || nextPageSearch == null
                        _searchArticle.value = Resource.Success(searchResponse)
                    }
                }

            }


        }
    }


    fun getArticles(category: String) {
        Log.d("tung", "viewModel getArticles")
        viewModelScope.launch {
            val nextPage = nextPageMap[category]
            val currentArticles = articleMap[category] ?: mutableListOf()

            if (currentArticles.isNotEmpty() && isLastPageMap[category] == true) {
                Log.d("tung", "end page getArticles ${currentArticles.size}")
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

    fun clearSearch(){
        oldQuery = null
        nextPageSearch = null
        searchResponse.clear()
        isLastSearchPage = false
    }

    fun isClearAdapter() : Boolean{
        return query==oldQuery
    }
}