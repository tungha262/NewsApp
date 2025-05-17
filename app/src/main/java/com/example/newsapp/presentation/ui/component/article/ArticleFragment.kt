package com.example.newsapp.presentation.ui.component.article

import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.newsapp.data.model.Article
import com.example.newsapp.databinding.FragmentArticleBinding
import com.example.newsapp.domain.state.Resource
import com.example.newsapp.network.NetworkConfig
import com.example.newsapp.presentation.base.BaseFragment
import com.example.newsapp.presentation.viewModel.LocalViewModel
import com.example.newsapp.utils.DialogNetworkError
import com.example.newsapp.utils.FormatDateTime
import com.example.ui_news.util.CustomToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ArticleFragment : BaseFragment<FragmentArticleBinding>(FragmentArticleBinding::inflate) {

    private var lastScrollY = 0
    private var buttonsVisible = true
    private var networkError: DialogNetworkError? = null
    private lateinit var defaultChromeIntent: CustomTabsIntent
    private lateinit var articleUrl: String
    private lateinit var article: Article
    private var isArticleExist = false

    private val localViewModel: LocalViewModel by viewModels()

    override fun observerViewModel() {
        super.observerViewModel()
        lifecycleScope.launch {
            localViewModel.addFavoriteArticle.collect { state ->
                when(state){
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        Log.d("tung", "call localViewModel add favorite success")
                        CustomToast.makeText(requireContext(), CustomToast.SUCCESS, state.data).show()
                    }
                    is Resource.Failed -> {
                        Log.d("tung", "call localViewModel add favorite success")
                        CustomToast.makeText(requireContext(), CustomToast.FAILED, state.message).show()
                    }
                }
            }
        }

        localViewModel.articleFavoriteExist.observe(viewLifecycleOwner) { exist ->
            if(exist){
                binding.btnFavorite.visibility = View.GONE
                isArticleExist = true
            }else{
                isArticleExist = false
                binding.btnFavorite.visibility = View.VISIBLE
            }
        }
    }

    override fun initListener() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            }
        )
        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnFavorite.setOnClickListener {
            if (!NetworkConfig.isInternetConnected(requireContext())) {
                networkError = DialogNetworkError {
                    if (NetworkConfig.isInternetConnected(requireContext())) {
                        networkError?.dismiss()
                        networkError = null
                    }
                }
                networkError?.show(childFragmentManager, "DialogNetworkError")
            } else {
                Log.d("tung", "call localViewModel add favorite")
                localViewModel.addFavoriteArticle(article)
            }
        }

        binding.scrollView.apply {
            viewTreeObserver.addOnScrollChangedListener {
                val scrollY = scrollY
                val limit = 20 // giới hạn kéo

                if (scrollY - lastScrollY > limit && buttonsVisible) {
                    // Kéo xuống vượt giới hạn thì ẩn
                    binding.btnClose.visibility = View.GONE
                    binding.btnFavorite.visibility = View.GONE
                    buttonsVisible = false

                } else if (lastScrollY - scrollY > limit && !buttonsVisible) {
                    // Kéo lên vượt giới hạn thì hiện
                    binding.btnClose.visibility = View.VISIBLE
                    if(isArticleExist){
                        binding.btnFavorite.visibility = View.VISIBLE
                    }
                    buttonsVisible = true
                }
                lastScrollY = scrollY
            }
        }
        binding.layoutLink.setOnClickListener {
            if (NetworkConfig.isInternetConnected(requireContext())) {
                defaultChromeIntent.launchUrl(requireContext(), articleUrl.toUri())
            } else {
                networkError = DialogNetworkError {
                    if (NetworkConfig.isInternetConnected(requireContext())) {
                        networkError?.dismiss()
                        networkError = null
                        defaultChromeIntent.launchUrl(requireContext(), articleUrl.toUri())
                    }
                }
                networkError!!.show(childFragmentManager, "DialogNetworkError")
            }
        }
    }

    override fun initUi() {
        val args: ArticleFragmentArgs by navArgs()
        article = args.article
        val category: String = getCategory(args.category)
        defaultChromeIntent = CustomTabsIntent.Builder().build()
        articleUrl = article.link.toString()
        localViewModel.findArticleById(article.articleId)

        binding.apply {
            tvTitle.text = article.title ?: "Xảy ra lỗi, vui lòng thử lại!"
            tvCategory.text = category
            tvDesc.text = splitContent(article.description.toString())
            tvSourceName.text = article.sourceName
            tvDateTime.text = FormatDateTime.formatFull(article.pubDate.toString())
            Glide.with(root)
                .load(article.sourceIcon)
                .into(imageSource)
            tvLink.text = article.link
            Glide.with(root)
                .load(article.imageUrl)
                .into(imageArticle)
        }


    }

    private fun getCategory(category: String): String {
        return when (category) {
            "top" -> "Mới nhất"
            "world" -> "Thế giới"
            "business" -> "Kinh tế"
            "technology" -> "Công nghệ"
            "health" -> "Sức khỏe"
            "sports" -> "Thể thao"
            else -> "entertainment"
        }
    }

    private fun splitContent(content: String): String {
        if (content.length <= 200) {
            return content
        }

        val sentences = content.split(Regex("(?<=[.!?])\\s+"))
            .filter { it.isNotBlank() }

        val smallParagraphs = mutableListOf<String>()
        var i = 0
        while (i < sentences.size) {
            if (i + 1 < sentences.size) {
                smallParagraphs.add(sentences[i] + " " + sentences[i + 1])
                i += 2
            } else {
                smallParagraphs.add(sentences[i])
                i++
            }
        }
        val rs = smallParagraphs.joinToString("\n\n")
        if (rs.isEmpty()) {
            return ""
        }
        return rs
    }

    override fun onResume() {
        super.onResume()
        if (!NetworkConfig.isInternetConnected(requireContext())) {
            networkError = DialogNetworkError {
                if (NetworkConfig.isInternetConnected(requireContext())) {
                    networkError?.dismiss()
                }
            }
            networkError!!.show(childFragmentManager, "DialogNetworkError")
        }
    }
}