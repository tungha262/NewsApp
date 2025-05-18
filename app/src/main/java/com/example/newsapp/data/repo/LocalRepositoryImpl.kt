package com.example.newsapp.data.repo

import com.example.newsapp.data.local.ArticleDao
import com.example.newsapp.data.model.Article
import com.example.newsapp.domain.repo.LocalRepository
import com.example.newsapp.domain.state.Resource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val articleDao: ArticleDao,
    private val auth: FirebaseAuth
) : LocalRepository {

    private fun getUserId(): String {
        return auth.currentUser?.uid ?: "user@null"
    }

    override suspend fun addFavoriteArticle(article: Article): Resource<String> {
        return try {
            val articleWithUser = article.copy(userId = getUserId())
            articleDao.addFavoriteArticle(articleWithUser)
            Resource.Success("Thêm vào mục yêu thích thành công!")
        } catch (e: Exception) {
            Resource.Failed("Có lỗi xảy ra, vui lòng thử lại!")
        }
    }

    override fun getAllFavoriteArticle(): Flow<Resource<List<Article>>> {
        val userId = getUserId()
        return articleDao.getAllFavoriteArticle(userId)
            .map { Resource.Success(it) as Resource<List<Article>> }
            .onStart { emit(Resource.Loading) }
            .catch { emit(Resource.Failed("Có lỗi xảy ra, vui lòng thử lại!")) }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun deleteFavoriteArticle(article: Article): Resource<String> {
        return try {
            val userId = getUserId()
            articleDao.deleteFavoriteArticle(article.articleId, userId)
            Resource.Success("Xóa thành công!")
        } catch (e: Exception) {
            Resource.Failed("Có lỗi xảy ra, vui lòng thử lại!")
        }
    }

    override suspend fun deleteAllFavoriteArticle(): Resource<String> {
        return try {
            val userId = getUserId()
            articleDao.deleteAllFavoriteArticle(userId)
            Resource.Success("Xóa thành công!")
        } catch (e: Exception) {
            Resource.Failed("Có lỗi xảy ra, vui lòng thử lại!")
        }
    }

    override suspend fun findArticleById(id: String): Flow<Boolean> = flow {
        val userId = getUserId()
        articleDao.findArticleById(id, userId).collect { exists ->
            emit(exists)
        }
    }.flowOn(Dispatchers.IO)
}
