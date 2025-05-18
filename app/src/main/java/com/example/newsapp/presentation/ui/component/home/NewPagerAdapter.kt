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

    private val fragmentMap = mutableMapOf<Int, Fragment>()

    override fun createFragment(position: Int): Fragment {
        return fragmentMap.getOrPut(position) {
            val category = categories[position]
            CategoryFragment().apply {
                arguments = Bundle().apply {
                    putString(Constant.CATEGORY, category)
                }
            }
        }
    }

    override fun getItemCount(): Int = categories.size
}
