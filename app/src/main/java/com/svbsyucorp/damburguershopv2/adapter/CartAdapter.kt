package com.svbsyucorp.damburguershopv2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.svbsyucorp.damburguershopv2.R
import com.svbsyucorp.damburguershopv2.domain.CartItem
import com.svbsyucorp.damburguershopv2.domain.CartManager
import kotlin.collections.isNotEmpty

class CartAdapter(
    private val items: MutableList<CartItem>,
    private val onItemChanged: () -> Unit
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageViewItem)
        val titleTextView: TextView = view.findViewById(R.id.textViewTitle)
        val priceTextView: TextView = view.findViewById(R.id.textViewPrice)
        val quantityTextView: TextView = view.findViewById(R.id.textViewQuantity)
        val plusButton: View = view.findViewById(R.id.buttonPlus)
        val minusButton: View = view.findViewById(R.id.buttonMinus)
        val removeButton: View = view.findViewById(R.id.buttonRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartItem = items[position]
        
        holder.titleTextView.text = cartItem.item.title
        holder.priceTextView.text = "$${cartItem.item.price}"
        holder.quantityTextView.text = cartItem.quantity.toString()
        
        if (cartItem.item.picUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(cartItem.item.picUrl[0])
                .into(holder.imageView)
        }
        
        holder.plusButton.setOnClickListener {
            cartItem.quantity++
            notifyItemChanged(position)
            onItemChanged()
        }
        
        holder.minusButton.setOnClickListener {
            if (cartItem.quantity > 1) {
                cartItem.quantity--
                notifyItemChanged(position)
                onItemChanged()
            }
        }
        
        holder.removeButton.setOnClickListener {
            CartManager.removeItem(cartItem.item)
            items.removeAt(position)
            notifyItemRemoved(position)
            onItemChanged()
        }
    }

    override fun getItemCount() = items.size
}