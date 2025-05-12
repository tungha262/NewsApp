package com.example.newsapp.presentation.ui.component.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentHomeBinding
import com.example.newsapp.presentation.base.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    override fun initUi() {
        val adapter = NewPagerAdapter(this)
        binding.viewPager2.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager2){ tab, pos ->
            tab.text = when(pos){
                0 -> "Mới nhất"
                1 -> "Thế giới"
                2 -> "Chính trị"
                3 -> "Kinh tế"
                4 -> "Công nghệ"
                5 -> "Sức khỏe"
                6 -> "Thể thao"
                else -> "Giải trí"
            }
        }.attach()
    }

    override fun initListener() {

    }

    override fun observerViewModel() {
        super.observerViewModel()

    }

}