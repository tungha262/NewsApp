package com.example.newsapp.presentation.ui.component.category

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.newsapp.data.model.Article
import com.example.newsapp.databinding.ArticleItemBinding
import com.example.newsapp.databinding.FragmentCategoryBinding
import com.example.newsapp.network.NetworkConfig
import com.example.newsapp.presentation.base.BaseAdapter
import com.example.newsapp.utils.FormatDateTime

class CategoryAdapter : BaseAdapter<ArticleItemBinding, Article>() {
    override fun bind(
        binding: ArticleItemBinding,
        item: Article
    ) {
        binding.apply {
            articleTitle.text = item.title
            articleSource.text = item.sourceName
            articleDescription.text = item.description
            articleDateTime.text = FormatDateTime.format(item.pubDate.toString())
            Glide.with(root).load(item.imageUrl).into(articleImage)
        }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ArticleItemBinding
        get() = { inflater, parent, _ ->
            ArticleItemBinding.inflate(inflater, parent,false)
        }
}