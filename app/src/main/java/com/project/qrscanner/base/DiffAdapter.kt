package com.project.qrscanner.base

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding


class ModelDiffCallback<T : Any>(
    private val areItemsTheSameCallback: (oldItem: T, newItem: T) -> Boolean,
    private val areContentsTheSameCallback: (oldItem: T, newItem: T) -> Boolean
) : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return areItemsTheSameCallback(oldItem, newItem)
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return areContentsTheSameCallback(oldItem, newItem)
    }
}

abstract class DiffAdapter<T, VB : ViewBinding>(
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, DiffAdapter<T, VB>.ViewHolder>(diffCallback) {

    lateinit var context: Context
    var binding: VB? = null
    var currentPosition = MutableLiveData<Int>(RecyclerView.NO_POSITION)

    abstract fun createBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): VB

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = createBinding(inflater, parent, viewType)
        context = parent.context
        return ViewHolder(binding!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.bind(item, position)
    }

    abstract fun VB.bind(item: T, position: Int)

    fun submitListCustom(newList: List<T>) {
        submitList(ArrayList(newList)) // Prevents modification issues
    }

    fun removeItem(position: Int) {
        val currentList = currentList.toMutableList()
        if (position in currentList.indices) {
            currentList.removeAt(position)
            submitListCustom(currentList)
        }
    }

    fun addItem(item: T, index: Int) {
        val currentList = currentList.toMutableList()
        currentList.add(index, item)
        submitListCustom(currentList)
    }

    fun changeItemWithPos(index: Int, newItem: T) {
        val currentList = currentList.toMutableList()
        if (index in currentList.indices) {
            currentList[index] = newItem
            submitListCustom(currentList)
        }
    }

    fun setCurrentPos(position: Int) {
        notifyItemChanged(currentPosition.value ?: RecyclerView.NO_POSITION)
        currentPosition.value = position
        notifyItemChanged(position)
    }

    inner class ViewHolder(val binding: VB) : RecyclerView.ViewHolder(binding.root)
}
