package com.svbsyucorp.damburguershopv2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
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
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var timer: Timer
    private val bannerUrls = mutableListOf<String>()
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        initViews()
        loadData()
        setupBottomNavigation()
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

        val banners = listOf(
            "https://res.cloudinary.com/dkauxesya/image/upload/v1760165943/logo_opcional_mzhb9m.jpg"
        )

        setupBanner(banners)
        setupCategories(categories)

        // Cargar items desde Firebase
        loadItemsFromFirebase()
    }

    private fun loadItemsFromFirebase() {
        database.reference.child("Items")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = mutableListOf<ItemModel>()
                    for (itemSnapshot in snapshot.children) {
                        val item = itemSnapshot.getValue(ItemModel::class.java)
                        item?.let { items.add(it) }
                    }
                    setupPopularItems(items)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity, "Error cargando platillos", Toast.LENGTH_SHORT).show()
                    loadBackupData()
                }
            })
    }

    private fun loadBackupData() {
        val backupItems = listOf(
            ItemModel(
                categoryId = "0",
                description = "Hamburguesa clásica con ingredientes frescos",
                extra = "Clásica",
                picUrl = listOf("https://res.cloudinary.com/dkauxesya/image/upload/v1760165943/food1_mn5tkz.png"),
                price = 3.5,
                rating = 4.0,
                title = "Hamburguesa Clásica"
            )
        )
        setupPopularItems(backupItems)
    }

    private fun setupBanner(banners: List<String>) {
        bannerUrls.clear()
        bannerUrls.addAll(banners)

        val adapter = BannerAdapter(bannerUrls)
        binding.bannerViewPager.adapter = adapter

        binding.progressBarBanner.visibility = View.GONE

        if (bannerUrls.size > 1) {
            autoScrollBanner()
        }
    }

    private fun setupCategories(categories: List<CategoryModel>) {
        binding.recyclerViewCategory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val adapter = CategoryAdapter(categories) { category ->
            Toast.makeText(this, "Categoría seleccionada: ${category.title}", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerViewCategory.adapter = adapter

        binding.progressBarCategory.visibility = View.GONE
    }

    private fun setupPopularItems(items: List<ItemModel>) {
        binding.recyclerViewPopular.layoutManager = GridLayoutManager(this, 2)

        val adapter = PopularAdapter(items) { item ->
            Toast.makeText(this, "Item seleccionado: ${item.title}", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerViewPopular.adapter = adapter

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
                    Toast.makeText(this, "Carrito Próximamente", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.favoritos -> {
                    Toast.makeText(this, "Favoritos Próximamente", Toast.LENGTH_SHORT).show()
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

    private fun autoScrollBanner() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            val current = binding.bannerViewPager.currentItem
            val next = if (current + 1 < bannerUrls.size) current + 1 else 0
            binding.bannerViewPager.currentItem = next
        }

        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(runnable)
            }
        }, 3000, 3000)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::timer.isInitialized) {
            timer.cancel()
        }
    }
}