package com.svbsyucorp.damburguershopv2

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.svbsyucorp.damburguershopv2.adapter.CartAdapter
import com.svbsyucorp.damburguershopv2.domain.CartManager

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var totalTextView: TextView
    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        initViews()
        setupRecyclerView()
        updateTotal()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerViewCart)
        totalTextView = findViewById(R.id.textViewTotal)

        findViewById<android.widget.Button>(R.id.buttonCheckout).setOnClickListener {
            if (CartManager.getItems().isNotEmpty()) {
                Toast.makeText(this, "Procesando pedido...", Toast.LENGTH_SHORT).show()
                CartManager.clear()
                finish()
            } else {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<ImageView>(R.id.back_arrow).setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CartAdapter(CartManager.getItems().toMutableList()) {
            updateTotal()
        }
        recyclerView.adapter = adapter
    }

    private fun updateTotal() {
        val total = CartManager.getTotalPrice()
        totalTextView.text = "Total: $${String.format("%.2f", total)}"
    }
}