package com.example.ambulance.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.ambulance.R
import com.example.ambulance.databinding.ItemLocationsBinding
import com.example.ambulance.model.UserLocations

class MyListAdapter(val context: Context) : RecyclerView.Adapter<MyListAdapter.VH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyListAdapter.VH {
        val inflate = LayoutInflater.from(parent.context)
        val view = ItemLocationsBinding.inflate(inflate, parent, false)
        return VH(context = context, view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val currentLocation = differ.currentList[position]

        holder.onBind(currentLocation)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class VH(val context: Context, val binding: ItemLocationsBinding) : ViewHolder(binding.root) {
        fun onBind(item: UserLocations) {
            binding.apply {
                tvLocationName.text = context.getString(
                    R.string.full_location, item.city,
                    item.street, item.village, item.home
                )
            }
        }
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<UserLocations>() {
        override fun areItemsTheSame(oldItem: UserLocations, newItem: UserLocations): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserLocations, newItem: UserLocations): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, diffCallBack)

}