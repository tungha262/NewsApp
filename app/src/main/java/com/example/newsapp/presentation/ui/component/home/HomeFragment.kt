package com.example.newsapp.presentation.ui.component.home

import android.util.Log
import androidx.fragment.app.activityViewModels
import com.example.newsapp.databinding.FragmentHomeBinding
import com.example.newsapp.presentation.base.BaseFragment
import com.example.newsapp.presentation.viewModel.RemoteViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private lateinit var adapter: NewPagerAdapter

    private val viewModel: RemoteViewModel by activityViewModels()

    override fun initUi() {
        adapter = NewPagerAdapter(this)
        binding.viewPager2.adapter = adapter
        binding.viewPager2.offscreenPageLimit = 6
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