package com.example.newsapp.data.repo

import com.example.newsapp.data.local.ArticleDao
import com.example.newsapp.data.model.Article
import com.example.newsapp.domain.repo.LocalRepository
import com.example.newsapp.domain.state.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val articleDao: ArticleDao
) : LocalRepository{
    override suspend fun addFavoriteArticle(article: Article): Resource<String> {
        TODO("Not yet implemented")
    }

    override fun getAllFavoriteArticle(): Flow<Resource<List<Article>>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFavoriteArticle(article: Article): Resource<String> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllFavoriteArticle(): Resource<String> {
        TODO("Not yet implemented")
    }
}