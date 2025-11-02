package com.svbsyucorp.damburguershopv2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.svbsyucorp.damburguershopv2.adapter.ManageDishAdapter
import com.svbsyucorp.damburguershopv2.domain.ItemModel

class ManageDishesActivity : AppCompatActivity() {

    private lateinit var recyclerDishes: RecyclerView
    private lateinit var btnCreateDish: Button
    private lateinit var btnVolver: ImageView
    private lateinit var backArrow: ImageView
    private lateinit var adapter: ManageDishAdapter
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_dishes)

        database = FirebaseDatabase.getInstance()
        
        recyclerDishes = findViewById(R.id.recycler_dishes)
        btnCreateDish = findViewById(R.id.btn_create_dish)
        btnVolver = findViewById(R.id.btn_volver)
        backArrow = findViewById(R.id.back_arrow)

        setupRecyclerView()
        loadDishes()

        btnCreateDish.setOnClickListener {
            val intent = Intent(this, CreateDishActivity::class.java)
            startActivity(intent)
        }

        btnVolver.setOnClickListener {
            finish()
        }

        backArrow.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadDishes()
    }

    private fun setupRecyclerView() {
        adapter = ManageDishAdapter(
            mutableListOf(),
            onEditClick = { dish ->
                val intent = Intent(this, EditDishActivity::class.java)
                intent.putExtra("DISH_ID", dish.id)
                intent.putExtra("DISH_TITLE", dish.title)
                intent.putExtra("DISH_PRICE", dish.price)
                intent.putExtra("DISH_DESCRIPTION", dish.description)
                intent.putExtra("DISH_EXTRA", dish.extra)
                intent.putExtra("DISH_IMAGE_URL", if (dish.picUrl.isNotEmpty()) dish.picUrl[0] else "")
                intent.putExtra("DISH_RATING", dish.rating)
                intent.putExtra("DISH_CATEGORY_ID", dish.categoryId)
                startActivity(intent)
            },
            onDeleteClick = { dish ->
                showDeleteDialog(dish)
            }
        )
        recyclerDishes.layoutManager = LinearLayoutManager(this)
        recyclerDishes.adapter = adapter
    }

    private fun loadDishes() {
        Log.d("ManageDishes", "Loading dishes...")
        database.reference.child("Items")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("ManageDishes", "Snapshot exists: ${snapshot.exists()}")
                    Log.d("ManageDishes", "Children count: ${snapshot.childrenCount}")
                    
                    val dishes = mutableListOf<ItemModel>()
                    for (dishSnapshot in snapshot.children) {
                        Log.d("ManageDishes", "Dish key: ${dishSnapshot.key}")
                        val dish = dishSnapshot.getValue(ItemModel::class.java)
                        if (dish != null) {
                            dish.id = dishSnapshot.key.orEmpty()
                            Log.d("ManageDishes", "Dish loaded: ${dish.title}")
                            dishes.add(dish)
                        }
                    }
                    Log.d("ManageDishes", "Total dishes loaded: ${dishes.size}")
                    adapter.updateDishes(dishes)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ManageDishes", "Error loading dishes: ${error.message}")
                    Toast.makeText(this@ManageDishesActivity, "Error cargando platos", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showDeleteDialog(dish: ItemModel) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar plato")
            .setMessage("¿Estás seguro de eliminar ${dish.title}?")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteDish(dish)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteDish(dish: ItemModel) {
        database.reference.child("Items").child(dish.id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Plato eliminado", Toast.LENGTH_SHORT).show()
                loadDishes()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
            }
    }
}