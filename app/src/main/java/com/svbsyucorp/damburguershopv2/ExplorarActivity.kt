package com.svbsyucorp.damburguershopv2

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.svbsyucorp.damburguershopv2.databinding.ActivityExplorarBinding
import com.svbsyucorp.damburguershopv2.domain.Item

class ExplorarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExplorarBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExplorarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        initMenu()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initMenu() {
        binding.progressBar.visibility = View.VISIBLE

        val currentUser = auth.currentUser
        if (currentUser == null) {
            loadAllItems(emptySet())
            return
        }

        val favoriteRef = database.reference.child("Favorites").child(currentUser.uid)
        favoriteRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favoriteIds = snapshot.children.mapNotNull { it.key }.toSet()
                loadAllItems(favoriteIds)
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = View.GONE
                loadAllItems(emptySet())
            }
        })
    }

    private fun loadAllItems(favoriteIds: Set<String>) {
        val menuRef = database.reference.child("Items")
        menuRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<Item>()
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(Item::class.java)
                    if (item != null) {
                        item.id = itemSnapshot.key.orEmpty()
                        item.isFavorite = favoriteIds.contains(item.id)
                        items.add(item)
                    }
                }
                if (items.isNotEmpty()) {
                    val adapter = MenuAdapter(items)
                    binding.recyclerMenu.layoutManager = GridLayoutManager(this@ExplorarActivity, 2)
                    binding.recyclerMenu.adapter = adapter
                }
                binding.progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = View.GONE
            }
        })
    }
}