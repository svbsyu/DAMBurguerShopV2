package com.svbsyucorp.damburguershopv2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.svbsyucorp.damburguershopv2.R
import com.svbsyucorp.damburguershopv2.domain.CartManager
import com.svbsyucorp.damburguershopv2.domain.ItemModel

class PopularAdapter(
    private val items: List<ItemModel>,
    private val onItemClick: (ItemModel) -> Unit
) : RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

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

        if (item.picUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(item.picUrl[0])
                .placeholder(R.drawable.placeholder_banner)
                .into(holder.imageView)
        }

        updateFavoriteButton(holder.favoriteButton, item.isFavorite)

        holder.favoriteButton.setOnClickListener {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                item.isFavorite = !item.isFavorite
                updateFavoriteButton(holder.favoriteButton, item.isFavorite)
                updateFavoriteInFirebase(currentUser.uid, item.id, item.isFavorite)
            } else {
                Toast.makeText(holder.itemView.context, "Debes iniciar sesión para añadir a favoritos", Toast.LENGTH_SHORT).show()
            }
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }

        holder.addButton.setOnClickListener {
            // La lógica de CartManager puede permanecer si funciona como esperas
            CartManager.addItem(item)
            Toast.makeText(holder.itemView.context, "Agregado al carrito", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = items.size

    private fun updateFavoriteButton(button: ImageView, isFavorite: Boolean) {
        val favoriteIcon = if (isFavorite) R.drawable.favorite_filled else R.drawable.favorite_ic
        button.setImageResource(favoriteIcon)
    }

    private fun updateFavoriteInFirebase(userId: String, itemId: String, isFavorite: Boolean) {
        val favoriteRef = database.child("Favorites").child(userId).child(itemId)
        if (isFavorite) {
            favoriteRef.setValue(true)
        } else {
            favoriteRef.removeValue()
        }
    }
}