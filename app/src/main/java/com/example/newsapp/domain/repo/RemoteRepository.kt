package com.example.newsapp.domain.repo

import com.example.newsapp.data.model.Article
import com.example.newsapp.data.model.ResultApi
import com.example.newsapp.domain.state.Resource
import kotlinx.coroutines.flow.Flow

interface RemoteRepository {

    fun getArticles(category:String, pageNumber: String?) : Flow<Resource<ResultApi>>

    fun searchArticles(search: String, pageNumber: String?) : Flow<Resource<ResultApi>>

}