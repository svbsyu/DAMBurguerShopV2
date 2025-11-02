package com.svbsyucorp.damburguershopv2

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.svbsyucorp.damburguershopv2.databinding.ActivityFavoritosBinding

class FavoritosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritosBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private val favoriteItems = mutableListOf<Item>()
    private lateinit var adapter: MenuAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        setupBackButton()
        setupRecyclerView()

        ViewCompat.setOnApplyWindowInsetsListener(binding.favoriteLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        loadFavoriteItems()
    }

    private fun setupBackButton() {
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        adapter = MenuAdapter(favoriteItems) { position ->
            // Esta es la lógica del callback
            if (position >= 0 && position < favoriteItems.size) {
                favoriteItems.removeAt(position)
                adapter.notifyItemRemoved(position)
                if (favoriteItems.isEmpty()) {
                    updateUiForEmptyFavorites()
                }
            }
        }
        binding.recyclerViewFavorites.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerViewFavorites.adapter = adapter
    }

    private fun loadFavoriteItems() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show()
            updateUiForEmptyFavorites()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.emptyText.visibility = View.GONE
        binding.recyclerViewFavorites.visibility = View.GONE

        val favoriteRef = database.reference.child("Favorites").child(currentUser.uid)
        favoriteRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favoriteIds = snapshot.children.mapNotNull { it.key }

                if (favoriteIds.isEmpty()) {
                    updateUiForEmptyFavorites()
                    return
                }

                fetchItemsDetails(favoriteIds)
            }

            override fun onCancelled(error: DatabaseError) {
                updateUiForEmptyFavorites()
                Toast.makeText(this@FavoritosActivity, "Error al cargar favoritos", Toast.LENGTH_SHORT).show()
            }
        })
    }
    
    private fun fetchItemsDetails(favoriteIds: List<String>) {
        val itemsRef = database.reference.child("Items")
        val localFavoritesList = mutableListOf<Item>()
        var itemsToFetch = favoriteIds.size

        if (itemsToFetch == 0) {
            updateRecyclerView(localFavoritesList)
            return
        }

        favoriteIds.forEach { itemId ->
            itemsRef.child(itemId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(itemSnapshot: DataSnapshot) {
                    val item = itemSnapshot.getValue(Item::class.java)
                    if (item != null) {
                        item.id = itemSnapshot.key.orEmpty()
                        item.isFavorite = true
                        localFavoritesList.add(item)
                    }
                    itemsToFetch--
                    if (itemsToFetch == 0) {
                        updateRecyclerView(localFavoritesList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    itemsToFetch--
                    if (itemsToFetch == 0) {
                        updateRecyclerView(localFavoritesList)
                    }
                }
            })
        }
    }

    private fun updateRecyclerView(newList: List<Item>) {
        favoriteItems.clear()
        favoriteItems.addAll(newList)
        adapter.notifyDataSetChanged()

        binding.progressBar.visibility = View.GONE
        if (favoriteItems.isEmpty()) {
            binding.emptyText.visibility = View.VISIBLE
            binding.recyclerViewFavorites.visibility = View.GONE
        } else {
            binding.emptyText.visibility = View.GONE
            binding.recyclerViewFavorites.visibility = View.VISIBLE
        }
    }
    
    private fun updateUiForEmptyFavorites() {
        favoriteItems.clear()
        adapter.notifyDataSetChanged()
        binding.progressBar.visibility = View.GONE
        binding.emptyText.visibility = View.VISIBLE
        binding.recyclerViewFavorites.visibility = View.GONE
    }
}