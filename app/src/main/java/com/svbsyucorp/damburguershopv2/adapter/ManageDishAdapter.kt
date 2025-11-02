package com.svbsyucorp.damburguershopv2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.svbsyucorp.damburguershopv2.R
import com.svbsyucorp.damburguershopv2.domain.ItemModel

class ManageDishAdapter(
    private var dishes: MutableList<ItemModel>,
    private val onEditClick: (ItemModel) -> Unit,
    private val onDeleteClick: (ItemModel) -> Unit
) : RecyclerView.Adapter<ManageDishAdapter.DishViewHolder>() {

    inner class DishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgDish: ImageView = itemView.findViewById(R.id.img_dish)
        val txtDishName: TextView = itemView.findViewById(R.id.txt_dish_name)
        val txtDishPrice: TextView = itemView.findViewById(R.id.txt_dish_price)
        val btnEdit: Button = itemView.findViewById(R.id.btn_edit)
        val btnDelete: Button = itemView.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_manage_dish, parent, false)
        return DishViewHolder(view)
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        val dish = dishes[position]
        
        holder.txtDishName.text = dish.title
        holder.txtDishPrice.text = "$${dish.price}"
        
        if (dish.picUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(dish.picUrl[0])
                .placeholder(R.drawable.placeholder_banner)
                .into(holder.imgDish)
        }
        
        holder.btnEdit.setOnClickListener { onEditClick(dish) }
        holder.btnDelete.setOnClickListener { onDeleteClick(dish) }
    }

    override fun getItemCount(): Int = dishes.size

    fun updateDishes(newDishes: List<ItemModel>) {
        dishes.clear()
        dishes.addAll(newDishes)
        notifyDataSetChanged()
    }
}