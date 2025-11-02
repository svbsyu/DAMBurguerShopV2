package com.svbsyucorp.damburguershopv2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.svbsyucorp.damburguershopv2.adapter.BannerAdapter
import com.svbsyucorp.damburguershopv2.adapter.CategoryAdapter
import com.svbsyucorp.damburguershopv2.adapter.PopularAdapter
import com.svbsyucorp.damburguershopv2.databinding.ActivityMainBinding
import com.svbsyucorp.damburguershopv2.domain.CategoryModel
import com.svbsyucorp.damburguershopv2.domain.ItemModel
import android.widget.TextView
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var timer: Timer
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private var allItems = mutableListOf<ItemModel>()
    private lateinit var popularAdapter: PopularAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        
        initViews()
        loadData()
        setupBottomNavigation()
        setupVerTodoButton()
    }

    private fun setupVerTodoButton() {
        val txtVerTodo = findViewById<TextView>(R.id.txt_ver_todo)
        txtVerTodo.setOnClickListener {
            startActivity(Intent(this, ExplorarActivity::class.java))
        }
    }

    private fun initViews() {
        // Views are accessed via binding
    }

    private fun loadData() {
        // Categorías estáticas
        val categories = listOf(
            CategoryModel(0, "Hamburguesa"),
            CategoryModel(1, "Burrito"),
            CategoryModel(2, "Pizza"),
            CategoryModel(3, "Brocheta"),
            CategoryModel(4, "Fusion")
        )

        loadBanners()
        setupCategories(categories)
        loadFavoriteItemsAndThenPopularItems()
    }

    private fun loadBanners() {
        database.reference.child("Banner")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val banners = mutableListOf<String>()
                    for (bannerSnapshot in snapshot.children) {
                        val bannerUrl = bannerSnapshot.child("url").getValue(String::class.java)
                        if (bannerUrl != null) {
                            banners.add(bannerUrl)
                        }
                    }
                    if (banners.isEmpty()) {
                        banners.add("https://res.cloudinary.com/dkauxesya/image/upload/v1760165943/logo_opcional_mzhb9m.jpg")
                    }
                    setupBanner(banners)
                }

                override fun onCancelled(error: DatabaseError) {
                    val defaultBanners = listOf("https://res.cloudinary.com/dkauxesya/image/upload/v1760165943/logo_opcional_mzhb9m.jpg")
                    setupBanner(defaultBanners)
                }
            })
    }

    private fun loadFavoriteItemsAndThenPopularItems() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            loadPopularItems(emptySet()) // Cargar sin favoritos si no hay sesión
            return
        }

        val favoriteRef = database.reference.child("Favorites").child(currentUser.uid)
        favoriteRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favoriteIds = snapshot.children.mapNotNull { it.key }.toSet()
                loadPopularItems(favoriteIds)
            }

            override fun onCancelled(error: DatabaseError) {
                loadPopularItems(emptySet()) // Cargar sin favoritos si hay error
            }
        })
    }

    private fun loadPopularItems(favoriteIds: Set<String>) {
        database.reference.child("Items")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = mutableListOf<ItemModel>()
                    for (itemSnapshot in snapshot.children) {
                        val item = itemSnapshot.getValue(ItemModel::class.java)
                        if (item != null) {
                            item.id = itemSnapshot.key.orEmpty()
                            item.isFavorite = favoriteIds.contains(item.id)
                            items.add(item)
                        }
                    }
                    setupPopularItems(items)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity, "Error cargando platillos", Toast.LENGTH_SHORT).show()
                }
            })
    }
    
    private fun setupBanner(banners: List<String>) {
        val adapter = BannerAdapter(banners)
        binding.bannerViewPager.adapter = adapter
        binding.progressBarBanner.visibility = View.GONE

        if (banners.size > 1) {
            autoScrollBanner(banners.size)
        }
    }

    private fun setupCategories(categories: List<CategoryModel>) {
        binding.recyclerViewCategory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val adapter = CategoryAdapter(categories) { category ->
            filterItemsByCategory(category.id)
        }
        binding.recyclerViewCategory.adapter = adapter

        binding.progressBarCategory.visibility = View.GONE
    }

    private fun filterItemsByCategory(categoryId: Int) {
        val filteredItems = allItems.filter { it.categoryId == categoryId.toString() }
        if (::popularAdapter.isInitialized) {
            popularAdapter.updateItems(filteredItems)
        }
    }

    private fun setupPopularItems(items: List<ItemModel>) {
        binding.recyclerViewPopular.layoutManager = GridLayoutManager(this, 2)
        
        allItems.clear()
        allItems.addAll(items)

        popularAdapter = PopularAdapter(items.toMutableList()) { item ->
            Toast.makeText(this, "Item seleccionado: ${item.title}", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerViewPopular.adapter = popularAdapter

        binding.progressBarPopular.visibility = View.GONE
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.explorar -> {
                    startActivity(Intent(this, ExplorarActivity::class.java))
                    true
                }
                R.id.carrito -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    true
                }
                R.id.favoritos -> {
                    startActivity(Intent(this, FavoritosActivity::class.java))
                    true
                }
                R.id.panel -> {
                    startActivity(Intent(this, AdminActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun autoScrollBanner(bannerCount: Int) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            val current = binding.bannerViewPager.currentItem
            val next = if (current + 1 < bannerCount) current + 1 else 0
            binding.bannerViewPager.currentItem = next
        }

        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(runnable)
            }
        }, 2000, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::timer.isInitialized) {
            timer.cancel()
        }
    }
}