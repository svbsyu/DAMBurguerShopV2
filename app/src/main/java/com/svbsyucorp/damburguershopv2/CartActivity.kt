package com.svbsyucorp.damburguershopv2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.svbsyucorp.damburguershopv2.adapter.CartAdapter
import com.svbsyucorp.damburguershopv2.domain.CartItem
import com.svbsyucorp.damburguershopv2.domain.CartManager
import java.util.HashMap
import com.airbnb.lottie.LottieAnimationView

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var totalTextView: TextView
    private lateinit var adapter: CartAdapter
    private lateinit var buttonCheckout: Button
    private lateinit var emptyCartLayout: LinearLayout
    private lateinit var emptyAnimation: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        initViews()
        setupRecyclerView()
        updateTotal()
        updateEmptyState()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerViewCart)
        totalTextView = findViewById(R.id.textViewTotal)
        buttonCheckout = findViewById(R.id.buttonCheckout)
        emptyCartLayout = findViewById(R.id.empty_cart_layout)
        emptyAnimation = findViewById(R.id.empty_cart_animation)

        buttonCheckout.setOnClickListener {
            if (CartManager.getItems().isNotEmpty()) {
                buttonCheckout.isEnabled = false
                Toast.makeText(this, "Procesando pedido...", Toast.LENGTH_SHORT).show()
                saveOrderToFirebase()
            } else {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<ImageView>(R.id.back_arrow).setOnClickListener {
            onBackPressed()
        }
    }

    private fun saveOrderToFirebase() {
        val database = FirebaseDatabase.getInstance().reference.child("Orders")
        val orderId = database.push().key

        if (orderId == null) {
            Toast.makeText(this, "Error al crear el pedido.", Toast.LENGTH_SHORT).show()
            buttonCheckout.isEnabled = true
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
        val orderTimestamp = System.currentTimeMillis()
        val total = CartManager.getTotalPrice()
        val items = CartManager.getItems()

        val orderData = HashMap<String, Any>()
        orderData["orderId"] = orderId
        orderData["userId"] = userId
        orderData["timestamp"] = orderTimestamp
        orderData["totalPrice"] = total
        orderData["status"] = "Pendiente"
        orderData["items"] = items.map { cartItem ->
            mapOf(
                "title" to cartItem.item.title,
                "quantity" to cartItem.quantity,
                "price" to cartItem.item.price
            )
        }

        database.child(orderId).setValue(orderData)
            .addOnSuccessListener {
                Toast.makeText(this, "¡Pedido realizado con éxito!", Toast.LENGTH_LONG).show()
                CartManager.clear()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar el pedido: ${it.message}", Toast.LENGTH_LONG).show()
                buttonCheckout.isEnabled = true
            }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CartAdapter(CartManager.getItems().toMutableList()) {
            updateTotal()
            updateEmptyState()
        }
        recyclerView.adapter = adapter
    }

    private fun updateTotal() {
        val total = CartManager.getTotalPrice()
        totalTextView.text = "Total: $${String.format("%.2f", total)}"
    }

    private fun updateEmptyState() {
        val isEmpty = CartManager.getItems().isEmpty()
        
        if (isEmpty) {
            emptyCartLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyCartLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}