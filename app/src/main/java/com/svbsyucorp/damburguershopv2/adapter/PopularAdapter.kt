package com.svbsyucorp.damburguershopv2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.svbsyucorp.damburguershopv2.R
import com.svbsyucorp.damburguershopv2.domain.CartManager
import com.svbsyucorp.damburguershopv2.domain.FavoriteManager
import com.svbsyucorp.damburguershopv2.domain.ItemModel

class PopularAdapter(
    private val items: List<ItemModel>,
    private val onItemClick: (ItemModel) -> Unit
) : RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {

    inner class PopularViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.pic)
        val titleText: TextView = itemView.findViewById(R.id.titleTxt)
        val priceText: TextView = itemView.findViewById(R.id.priceTxt)
        val addButton: ImageView = itemView.findViewById(R.id.addBtn)
        val favoriteButton: ImageView = itemView.findViewById(R.id.favoriteBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_popular, parent, false)
        return PopularViewHolder(view)
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        val item = items[position]

        holder.titleText.text = item.title
        holder.priceText.text = "$${item.price}"

        // Cargar imagen con Glide
        if (item.picUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(item.picUrl[0])
                .placeholder(R.drawable.placeholder_banner)
                .into(holder.imageView)
        }

        // Configurar botón de favoritos
        updateFavoriteButton(holder.favoriteButton, item.title)
        holder.favoriteButton.setOnClickListener {
            if (FavoriteManager.isFavorite(item.title)) {
                FavoriteManager.removeFavorite(item.title)
                Toast.makeText(holder.itemView.context, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
            } else {
                FavoriteManager.addFavorite(item.title)
                Toast.makeText(holder.itemView.context, "Agregado a favoritos", Toast.LENGTH_SHORT).show()
            }
            updateFavoriteButton(holder.favoriteButton, item.title)
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }

        holder.addButton.setOnClickListener {
            CartManager.addItem(item)
            Toast.makeText(holder.itemView.context, "Agregado al carrito", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = items.size

    private fun updateFavoriteButton(button: ImageView, itemTitle: String) {
        if (FavoriteManager.isFavorite(itemTitle)) {
            button.setColorFilter(ContextCompat.getColor(button.context, android.R.color.holo_red_dark))
        } else {
            button.setColorFilter(ContextCompat.getColor(button.context, android.R.color.darker_gray))
        }
    }
}