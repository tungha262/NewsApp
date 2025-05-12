package com.example.newsapp.data.model

import com.google.gson.annotations.SerializedName

data class ResultApi(
    @SerializedName("status") var status: String? = null,
    @SerializedName("totalResults") var totalResults: Int? = null,
    @SerializedName("results") var results: ArrayList<Article> = arrayListOf(),
    @SerializedName("nextPage") var nextPage: String? = null
)