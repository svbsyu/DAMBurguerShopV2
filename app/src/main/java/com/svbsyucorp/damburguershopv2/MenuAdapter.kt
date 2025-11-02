package com.svbsyucorp.damburguershopv2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.svbsyucorp.damburguershopv2.databinding.ItemMenuBinding

class MenuAdapter(
    private val items: MutableList<Item>,
    private val onUnfavorite: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

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

            setFavoriteIcon(item.isFavorite)

            binding.favoriteBtn.setOnClickListener {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    // Invertir el estado
                    item.isFavorite = !item.isFavorite
                    setFavoriteIcon(item.isFavorite)
                    updateFavoriteInFirebase(currentUser.uid, item.id, item.isFavorite)

                    // Si se acaba de desmarcar, notificar a través del callback
                    if (!item.isFavorite) {
                        onUnfavorite?.invoke(adapterPosition)
                    }
                }
            }
        }

        private fun setFavoriteIcon(isFavorite: Boolean) {
            val favoriteIcon = if (isFavorite) R.drawable.favorite_filled else R.drawable.favorite_ic
            binding.favoriteBtn.setImageResource(favoriteIcon)
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
}