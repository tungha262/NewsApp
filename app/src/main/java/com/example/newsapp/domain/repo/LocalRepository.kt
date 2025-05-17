package com.example.newsapp.domain.repo

import com.example.newsapp.data.model.Article
import com.example.newsapp.domain.state.Resource
import kotlinx.coroutines.flow.Flow
interface LocalRepository {

    suspend fun addFavoriteArticle(article: Article) : Resource<String>

    fun getAllFavoriteArticle() : Flow<Resource<List<Article>>>

    suspend fun deleteFavoriteArticle(article: Article): Resource<String>

    suspend fun deleteAllFavoriteArticle(): Resource<String>

    suspend fun findArticleById(id :String) : Flow<Boolean>

}