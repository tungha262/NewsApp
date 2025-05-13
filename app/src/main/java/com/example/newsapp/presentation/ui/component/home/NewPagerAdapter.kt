package com.example.newsapp.presentation.ui.component.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.newsapp.presentation.ui.component.category.CategoryFragment
import com.example.newsapp.utils.Constant

class NewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val categories = listOf(
        "top", "world", "business", "technology",
        "health", "sports", "entertainment"
    )
    override fun createFragment(position: Int): Fragment {
        val category = categories[position]
        return CategoryFragment().apply {
            arguments = Bundle().apply {
                putString(Constant.CATEGORY, category)
            }
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}