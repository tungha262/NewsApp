package com.example.newsapp.data.repo

import com.example.newsapp.data.local.ArticleDao
import com.example.newsapp.data.model.Article
import com.example.newsapp.domain.repo.LocalRepository
import com.example.newsapp.domain.state.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val articleDao: ArticleDao
) : LocalRepository {
    override suspend fun addFavoriteArticle(article: Article): Resource<String> {
        try {
            articleDao.addFavoriteArticle(article)
            return Resource.Success("Thêm vào mục yêu thích thành công!")
        } catch (e: Exception) {
            return Resource.Failed("Có lỗi xảy ra, vui lòng thử lại!")
        }
    }

    override fun getAllFavoriteArticle(): Flow<Resource<List<Article>>> {
        return articleDao.getAllFavoriteArticle()
            .map { Resource.Success(it) as Resource<List<Article>> }
            .onStart { emit(Resource.Loading) }
            .catch { emit(Resource.Failed("Có lỗi xảy ra, vui lòng thử lại!")) }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun deleteFavoriteArticle(article: Article): Resource<String> {
        try {
            articleDao.deleteFavoriteArticle(article)
            return Resource.Success("Xóa thành công!")
        } catch (e: Exception) {
            return Resource.Failed("Có lỗi xảy ra, vui lòng thử lại!")
        }
    }

    override suspend fun deleteAllFavoriteArticle(): Resource<String> {
        try {
            articleDao.deleteAllFavoriteArticle()
            return Resource.Success("Xóa thành công!")
        } catch (e: Exception) {
            return Resource.Failed("Có lỗi xảy ra, vui lòng thử lại!")
        }
    }


    override suspend fun findArticleById(id: String): Flow<Boolean> = flow {
        articleDao.findArticleById(id).collect { exists ->
            emit(exists)
        }
    }.flowOn(Dispatchers.IO)
}