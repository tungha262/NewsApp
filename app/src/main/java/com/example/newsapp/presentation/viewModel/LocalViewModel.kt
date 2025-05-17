package com.example.newsapp.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.model.Article
import com.example.newsapp.domain.repo.LocalRepository
import com.example.newsapp.domain.state.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalViewModel @Inject constructor(
    private val localRepository: LocalRepository
) : ViewModel() {

    private var _addFavoriteArticle = MutableSharedFlow<Resource<String>>()
    val addFavoriteArticle: SharedFlow<Resource<String>> get() = _addFavoriteArticle

    private var _getAllFavoriteArticle = MutableLiveData<Resource<List<Article>>>()
    val getAllFavoriteArticle: LiveData<Resource<List<Article>>> get() = _getAllFavoriteArticle

    private var _deleteFavoriteArticle = MutableSharedFlow<Resource<String>>()
    val deleteFavoriteArticle: SharedFlow<Resource<String>> get() = _deleteFavoriteArticle

    private var _deleteAllFavoriteArticle = MutableSharedFlow<Resource<String>>()
    val deleteAllFavoriteArticle: SharedFlow<Resource<String>> get() = _deleteAllFavoriteArticle


    private var _articleFavoriteExist = MutableLiveData<Boolean>()
    val articleFavoriteExist: LiveData<Boolean> get() = _articleFavoriteExist


    fun addFavoriteArticle(article: Article) {
        viewModelScope.launch(Dispatchers.IO) {
            _addFavoriteArticle.emit(localRepository.addFavoriteArticle(article))
        }
    }

    fun getAllFavoriteArticle() {
        viewModelScope.launch {
            localRepository.getAllFavoriteArticle().collect { rs ->
                when (rs) {
                    is Resource.Loading -> _getAllFavoriteArticle.value = Resource.Loading
                    is Resource.Success -> {
                        val data = rs.data
                        _getAllFavoriteArticle.value = Resource.Success(data)
                    }

                    is Resource.Failed -> {
                        _getAllFavoriteArticle.value = Resource.Failed(rs.message)
                    }
                }
            }
        }
    }

    fun deleteFavoriteArticle(article: Article) {
        viewModelScope.launch(Dispatchers.IO) {
            _deleteFavoriteArticle.emit(localRepository.deleteFavoriteArticle(article))
        }
    }

    fun deleteAllFavoriteArticle() {
        viewModelScope.launch(Dispatchers.IO) {
            _deleteAllFavoriteArticle.emit(localRepository.deleteAllFavoriteArticle())
        }
    }

    fun findArticleById(id: String){
        viewModelScope.launch {
            localRepository.findArticleById(id).collect { exist ->
                _articleFavoriteExist.value = exist
            }
        }
    }

}