package com.example.newsapp.data.remote

import com.example.newsapp.data.model.ResultApi
import com.example.newsapp.utils.Constant
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("api/1/latest")
    suspend fun getArticles(
        @Query("country") country: String = Constant.COUNTRY,
        @Query("language") language: String = Constant.COUNTRY,
        @Query("category") category: String,
        @Query("page") pageNumber: String? = null,
        @Query("apikey") apiKey: String = Constant.API_KEY
    ): Response<ResultApi>

    @GET("api/1/latest")
    suspend fun searchArticles(
        @Query("country") country: String = Constant.COUNTRY,
        @Query("language") language: String = Constant.COUNTRY,
        @Query("page") pageNumber: String? = null,
        @Query("apikey") apiKey: String = Constant.API_KEY,
        @Query("q") search: String? = null
    ): Response<ResultApi>
}