package com.example.newsapp.presentation.viewModel

import android.util.Log
import androidx.lifecycle.*
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
    var selectedTabIndex = 0

    private val scrollPositions = mutableMapOf<String, Int>()

    fun saveScrollPosition(category: String, position: Int) {
        scrollPositions[category] = position
    }

    fun getScrollPosition(category: String): Int {
        return scrollPositions[category] ?: 0
    }

    // Dữ liệu theo từng category
    private val articleMap = mutableMapOf<String, MutableList<Article>>()
    private val nextPageMap = mutableMapOf<String, String?>()
    private val isLastPageMap = mutableMapOf<String, Boolean>()
    private val liveDataMap = mutableMapOf<String, MutableLiveData<Resource<List<Article>>>>()

    // Tìm kiếm
    private val _searchArticle = MutableLiveData<Resource<List<Article>>>()
    val searchArticle: LiveData<Resource<List<Article>>> get() = _searchArticle
    private var oldQuery: String? = null
    private var nextPageSearch: String? = null
    private var searchResponse: MutableList<Article> = mutableListOf()
    private var isLastSearchPage = false
    private var query: String? = null

    /**
     * Trả về LiveData tương ứng với một category.
     */
    fun getArticleLiveData(category: String): LiveData<Resource<List<Article>>> {
        return liveDataMap.getOrPut(category) { MutableLiveData() }
    }

    /**
     * Gọi API và post dữ liệu về đúng LiveData của category.
     */
    fun getArticles(category: String) {
        val liveData = liveDataMap.getOrPut(category) { MutableLiveData() }
        Log.d("tung", "viewModel getArticles category=$category")

        viewModelScope.launch {
            val nextPage = nextPageMap[category]
            val currentArticles = articleMap[category] ?: mutableListOf()

            if (currentArticles.isNotEmpty() && isLastPageMap[category] == true) {
                Log.d("tung", "end page getArticles ${currentArticles.size}")
                liveData.value = Resource.Success(currentArticles)
                return@launch
            }

            remoteRepository.getArticles(category, nextPage).collect { rs ->
                when (rs) {
                    is Resource.Loading -> liveData.value = Resource.Loading

                    is Resource.Success -> {
                        val newArticles = rs.data.results
                        val updatedList = currentArticles.toMutableList().apply {
                            addAll(newArticles)
                        }

                        articleMap[category] = updatedList
                        nextPageMap[category] = rs.data.nextPage
                        isLastPageMap[category] = newArticles.isEmpty()
                        liveData.value = Resource.Success(updatedList)
                    }

                    is Resource.Failed -> {
                        liveData.value = Resource.Failed(rs.message)
                    }
                }
            }
        }
    }

    /**
     * Làm mới toàn bộ dữ liệu category.
     */
    fun refreshCategory(category: String) {
        nextPageMap.remove(category)
        articleMap.remove(category)
        isLastPageMap.remove(category)
        liveDataMap[category]?.value = Resource.Loading
        getArticles(category)
    }

    /**
     * Trả về danh sách hiện tại của category.
     */
    fun getCurrentData(category: String): List<Article> {
        return articleMap[category] ?: emptyList()
    }

    // ==== SEARCH LOGIC GIỮ NGUYÊN ====

    fun searchArticle(query: String) {
        if (query.isEmpty()) return

        this.query = query
        Log.d("tung", "call search with $query")

        viewModelScope.launch {
            if (query != oldQuery) {
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
                when (rs) {
                    is Resource.Failed -> {
                        Log.d("tung", "search fail")
                        _searchArticle.value = Resource.Failed(rs.message)
                    }

                    is Resource.Loading -> _searchArticle.value = Resource.Loading

                    is Resource.Success -> {
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

    fun clearSearch() {
        oldQuery = null
        nextPageSearch = null
        searchResponse.clear()
        isLastSearchPage = false
    }

    fun isClearAdapter(): Boolean {
        return query == oldQuery
    }

    fun getLastSearchPage(): Boolean {
        return isLastSearchPage
    }
}
