package com.svbsyucorp.damburguershopv2

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.svbsyucorp.damburguershopv2.adapter.OrderAdapter
import com.svbsyucorp.damburguershopv2.domain.Order

class PedidosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var ordersAdapter: OrderAdapter
    private lateinit var dbRef: DatabaseReference

    private val ordersList = ArrayList<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedidos)

        recyclerView = findViewById(R.id.recyclerViewOrders)
        progressBar = findViewById(R.id.progressBar)
        findViewById<ImageView>(R.id.back_arrow).setOnClickListener {
            onBackPressed()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        ordersAdapter = OrderAdapter(ordersList)
        recyclerView.adapter = ordersAdapter

        fetchOrders()
    }

    private fun fetchOrders() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        dbRef = FirebaseDatabase.getInstance().getReference("Orders")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ordersList.clear()
                if (snapshot.exists()) {
                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(Order::class.java)
                        if (order != null) {
                            ordersList.add(order)
                        }
                    }
                    ordersList.reverse()
                    ordersAdapter.notifyDataSetChanged()
                }
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@PedidosActivity, "Error al cargar pedidos: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
