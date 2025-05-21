package com.example.newsapp.presentation.ui.component.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentHomeBinding
import com.example.newsapp.presentation.base.BaseFragment
import com.example.newsapp.presentation.ui.component.category.CategoryFragment
import com.example.newsapp.presentation.viewModel.RemoteViewModel
import com.example.ui_news.util.CustomToast
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private lateinit var adapter: NewPagerAdapter

    private val viewModel: RemoteViewModel by activityViewModels()

    override fun initUi() {
        adapter = NewPagerAdapter(this)
        binding.viewPager2.adapter = adapter
        binding.viewPager2.offscreenPageLimit = 1
        TabLayoutMediator(binding.tabLayout, binding.viewPager2){ tab, pos ->
            tab.text = when(pos){
                0 -> "Mới nhất"
                1 -> "Thế giới"
                2 -> "Kinh tế"
                3 -> "Công nghệ"
                4 -> "Sức khỏe"
                5 -> "Thể thao"
                else -> "Giải trí"
            }
        }.attach()

        binding.viewPager2.setCurrentItem(viewModel.selectedTabIndex, false)

    }


    override fun initListener() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("tung","onDestroyView home")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("tung","onDestroy home")
    }

    override fun onResume() {
        super.onResume()
        Log.d("tung","onResume home")
    }

    override fun onPause() {
        super.onPause()
        Log.d("tung","onPause home")
        viewModel.selectedTabIndex = binding.viewPager2.currentItem
    }
}