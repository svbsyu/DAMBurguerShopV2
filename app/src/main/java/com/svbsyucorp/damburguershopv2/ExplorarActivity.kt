package com.svbsyucorp.damburguershopv2

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.svbsyucorp.damburguershopv2.databinding.ActivityExplorarBinding


class ExplorarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExplorarBinding
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExplorarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()

        initMenu()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initMenu() {
        binding.progressBar.visibility = View.VISIBLE
        val menuRef = database.reference.child("Items")
        menuRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<Item>()
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(Item::class.java)
                    if (item != null) {
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