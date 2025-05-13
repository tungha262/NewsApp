package com.example.newsapp.data.repo

import com.example.newsapp.data.model.Article
import com.example.newsapp.data.model.ResultApi
import com.example.newsapp.data.remote.ApiService
import com.example.newsapp.domain.repo.RemoteRepository
import com.example.newsapp.domain.state.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okio.IOException
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class RemoteRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : RemoteRepository {
    private lateinit var response: Response<ResultApi>
    override fun getArticles(
        category: String,
        pageNumber: String?
    ): Flow<Resource<ResultApi>> = flow {
        emit(Resource.Loading)
        try {
            if(pageNumber==null){
                response = apiService.getArticles(category = category)
            }else{
                response = apiService.getArticles(category = category, pageNumber = pageNumber)
            }
            if(response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    emit(Resource.Success(data))
                } else {
                    emit(Resource.Failed("Có lỗi xảy ra, không có dữ liệu trả về!"))
                }
            }else{
                emit(Resource.Failed(response.message()))
            }
        }catch (e: Exception){
            when(e){
                is IOException -> emit(Resource.Failed("Không có kết nối mạng!"))
                is HttpException -> emit(Resource.Failed("${e.code()}- ${e.message}"))
                else -> emit(Resource.Failed(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)
}