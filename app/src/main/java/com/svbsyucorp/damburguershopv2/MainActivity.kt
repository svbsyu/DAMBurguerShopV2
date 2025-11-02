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

    private lateinit var bannerViewPager: ViewPager2
    private lateinit var timer: Timer
    private val bannerUrls = mutableListOf<String>()
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = FirebaseDatabase.getInstance()
        initViews()
        loadData()
//        setupBottomNavigation()
    }

    private fun initViews() {
        bannerViewPager = findViewById(R.id.bannerViewPager)
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
        bannerViewPager.adapter = adapter

        findViewById<View>(R.id.progressBarBanner).visibility = View.GONE

        if (bannerUrls.size > 1) {
            autoScrollBanner()
        }
    }

    private fun setupCategories(categories: List<CategoryModel>) {
        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewCategory)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val adapter = CategoryAdapter(categories) { category ->
            Toast.makeText(this, "Categoría seleccionada: ${category.title}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter

        findViewById<View>(R.id.progressBarCategory).visibility = View.GONE
    }

    private fun setupPopularItems(items: List<ItemModel>) {
        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewPopular)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        val adapter = PopularAdapter(items) { item ->
            Toast.makeText(this, "Item seleccionado: ${item.title}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter

        findViewById<View>(R.id.progressBarPopular).visibility = View.GONE
    }

//    private fun setupBottomNavigation() {
//        findViewById<View>(R.id.explorerBtn).setOnClickListener {
//            startActivity(Intent(this, ExplorarActivity::class.java))
//        }
//
//        findViewById<View>(R.id.cartBtn).setOnClickListener {
//            startActivity(Intent(this, CartActivity::class.java))
//        }
//
//        findViewById<View>(R.id.favoriteBtn).setOnClickListener {
//            startActivity(Intent(this, FavoritosActivity::class.java))
//        }
//
//
//    }

    private fun autoScrollBanner() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            val current = bannerViewPager.currentItem
            val next = if (current + 1 < bannerUrls.size) current + 1 else 0
            bannerViewPager.currentItem = next
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