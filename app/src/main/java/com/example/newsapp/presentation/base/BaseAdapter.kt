package com.example.newsapp.presentation.base

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<VB : ViewBinding, T : Any> :
    RecyclerView.Adapter<BaseAdapter.BaseViewHolder<VB>>() {

    private var list: List<T> = listOf()
    private var onItemClickListener: ((item: T) -> Unit)? = null

    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
    abstract fun bind(binding: VB, item: T)

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<VB> {
        val binding = bindingInflater(LayoutInflater.from(parent.context), parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<VB>,
        position: Int
    ) {
        val item = list[position]
        bind(holder.binding, item)
        holder.binding.root.setOnClickListener {
            onItemClickListener?.let {
                it(item)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<T>) {
        this.list = data
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (item: T) -> Unit) {
        this.onItemClickListener = listener
    }

    class BaseViewHolder<VB : ViewBinding>(val binding: VB) :
        RecyclerView.ViewHolder(binding.root) {}
}