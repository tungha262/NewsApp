package com.example.newsapp.data.model

import com.google.gson.annotations.SerializedName


data class Article(
    @SerializedName("article_id") var articleId: String,
    @SerializedName("title") var title: String? = null,
    @SerializedName("link") var link: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("content") var content: String? = null,
    @SerializedName("pubDate") var pubDate: String? = null,
    @SerializedName("pubDateTZ") var pubDateTZ: String? = null,
    @SerializedName("image_url") var imageUrl: String? = null,
    @SerializedName("source_id") var sourceId: String? = null,
    @SerializedName("source_name") var sourceName: String? = null,
    @SerializedName("source_icon") var sourceIcon: String? = null,
    @SerializedName("language") var language: String? = null,
)