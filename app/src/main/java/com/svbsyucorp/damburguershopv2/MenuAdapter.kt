package com.svbsyucorp.damburguershopv2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.svbsyucorp.damburguershopv2.databinding.ItemMenuBinding

class MenuAdapter(private val items: List<Item>) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class MenuViewHolder(private val binding: ItemMenuBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.titleTxt.text = item.title
            binding.priceTxt.text = "$${item.price}"
            Glide.with(binding.root.context)
                .load(item.picUrl.firstOrNull())
                .into(binding.pic)
        }
    }
}