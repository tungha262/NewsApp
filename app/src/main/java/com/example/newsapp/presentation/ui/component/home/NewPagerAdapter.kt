package com.example.newsapp.presentation.ui.component.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.newsapp.presentation.ui.component.category.CategoryFragment

class NewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> CategoryFragment()
            1 -> CategoryFragment()
            2 -> CategoryFragment()
            3 -> CategoryFragment()
            4 -> CategoryFragment()
            5 -> CategoryFragment()
            6 -> CategoryFragment()
            else -> CategoryFragment()
        }
    }

    override fun getItemCount(): Int {
        return 7
    }
}