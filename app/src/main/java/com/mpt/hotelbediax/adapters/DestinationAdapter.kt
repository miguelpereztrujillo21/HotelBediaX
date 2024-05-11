package com.mpt.hotelbediax.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mpt.hotelbediax.databinding.ItemDestinationBinding
import com.mpt.hotelbediax.models.Destination

class DestinationAdapter(private val context: Context, private val clickListener: ClickListener) :
    ListAdapter<Destination, DestinationAdapter.ViewHolder>(
        CountryDiffCallback()
    ) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: ItemDestinationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            context: Context,
            item: Destination,
            clickListener: ClickListener
        ) {
            binding.itemDestinationName.text = item.name
            binding.itemDestinationDesc.text = item.description
            binding.itemDestinationType.text = item.type
            binding.itemDestinationLastDate.text = item.lastModify



        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDestinationBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }

    class CountryDiffCallback : DiffUtil.ItemCallback<Destination>() {
        override fun areContentsTheSame(oldItem: Destination, newItem: Destination): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areItemsTheSame(oldItem: Destination, newItem: Destination): Boolean {
            return oldItem == newItem
        }
    }

    interface ClickListener {
        fun onClick(position: Int)
    }

    fun getDestinationPosition(position: Int): Destination? {
        return getItem(position)
    }
}