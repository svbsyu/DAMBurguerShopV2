package com.svbsyucorp.damburguershopv2

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.svbsyucorp.damburguershopv2.domain.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PedidoDetalleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedido_detalle)

        val backArrow: ImageView = findViewById(R.id.back_arrow)
        val orderIdTextView: TextView = findViewById(R.id.textViewDetailOrderId)
        val dateTextView: TextView = findViewById(R.id.textViewDetailDate)
        val statusTextView: TextView = findViewById(R.id.textViewDetailStatus)
        val itemsLayout: LinearLayout = findViewById(R.id.linearLayoutItems)

        backArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val order = intent.getSerializableExtra("ORDER_DETAILS") as? Order

        if (order != null) {
            orderIdTextView.text = "ID Pedido: #${order.orderId.substring(4, 10)}"
            statusTextView.text = "Estado: ${order.status}"

            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val date = Date(order.timestamp)
            dateTextView.text = "Fecha: ${sdf.format(date)}"

            itemsLayout.removeAllViews()

            for (itemMap in order.items) {
                val itemTitle = itemMap["title"] as? String ?: "Artículo desconocido"
                val itemQuantity = (itemMap["quantity"] as? Long)?.toInt() ?: 0
                val itemPrice = itemMap["price"] as? Double ?: 0.0

                val itemView = TextView(this)
                itemView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                itemView.textSize = 16f
                itemView.setPadding(8, 4, 8, 4)
                itemView.text = "• $itemQuantity x $itemTitle (c/u $${String.format("%.2f", itemPrice)})"

                itemsLayout.addView(itemView)
            }
        } else {
            orderIdTextView.text = "Error al cargar el pedido"
        }
    }
}
