package com.mpt.hotelbediax.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mpt.hotelbediax.databinding.ItemDestinationBinding
import com.mpt.hotelbediax.models.Destination

class DestinationAdapter( private val clickListener: ClickListener) :
    ListAdapter<Destination, DestinationAdapter.ViewHolder>(
        CountryDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder private constructor(private val binding: ItemDestinationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: Destination,
            clickListener: ClickListener
        ) {
            binding.apply {
                itemDestinationName.text = item.name
                itemDestinationDesc.text = item.description
                itemDestinationType.text = item.type
                itemDestinationLastDate.text = item.lastModify
                itemDestinationDesc.visibility = View.GONE
                itemDestinationDeleteButton.visibility = View.GONE
                itemDestinationEditButton.visibility = View.GONE
                itemDestinationContainer.setOnClickListener {
                    itemDestinationDesc.visibility = itemDestinationDesc.visibility.let {
                        if (it == View.VISIBLE) View.GONE else View.VISIBLE }
                    itemDestinationDeleteButton.visibility = itemDestinationDeleteButton.visibility.let {
                        if (it == View.VISIBLE) View.GONE else View.VISIBLE }
                    itemDestinationEditButton.visibility = itemDestinationEditButton.visibility.let {
                        if (it == View.VISIBLE) View.GONE else View.VISIBLE }
                }
                itemDestinationDeleteButton.setOnClickListener {
                    clickListener.onClickDelete(item)
                }
                itemDestinationEditButton.setOnClickListener {
                    clickListener.onClickEdit(item)
                }
            }
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
        fun onClickDelete(destination: Destination)
        fun onClickEdit(destination: Destination)
    }

    fun getDestinationPosition(position: Int): Destination? {
        return getItem(position)
    }
}