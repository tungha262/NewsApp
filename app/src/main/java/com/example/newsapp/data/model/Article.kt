package com.example.newsapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "favorite_article")
data class Article(
    @PrimaryKey
    @SerializedName("article_id")
    var articleId: String,
    @SerializedName("title") var title: String? = null,
    @SerializedName("link") var link: String? = null,
    @SerializedName("keywords") var keywords: String? = null,
    @SerializedName("creator") var creator: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("content") var content: String? = null,
    @SerializedName("pubDate") var pubDate: String? = null,
    @SerializedName("pubDateTZ") var pubDateTZ: String? = null,
    @SerializedName("image_url") var imageUrl: String? = null,
    @SerializedName("video_url") var videoUrl: String? = null,
    @SerializedName("source_id") var sourceId: String? = null,
    @SerializedName("source_name") var sourceName: String? = null,
    @SerializedName("source_priority") var sourcePriority: Int? = null,
    @SerializedName("source_url") var sourceUrl: String? = null,
    @SerializedName("source_icon") var sourceIcon: String? = null,
    @SerializedName("language") var language: String? = null,
    @SerializedName("sentiment") var sentiment: String? = null,
    @SerializedName("sentiment_stats") var sentimentStats: String? = null,
    @SerializedName("ai_tag") var aiTag: String? = null,
    @SerializedName("ai_region") var aiRegion: String? = null,
    @SerializedName("ai_org") var aiOrg: String? = null,
    @SerializedName("duplicate") var duplicate: Boolean? = null
)