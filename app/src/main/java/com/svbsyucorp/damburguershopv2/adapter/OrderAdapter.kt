package com.svbsyucorp.damburguershopv2.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.svbsyucorp.damburguershopv2.PedidoDetalleActivity
import com.svbsyucorp.damburguershopv2.R
import com.svbsyucorp.damburguershopv2.domain.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderAdapter(private val orders: List<Order>) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderIdTextView: TextView = view.findViewById(R.id.textViewOrderId)
        val orderDateTextView: TextView = view.findViewById(R.id.textViewOrderDate)
        val orderTotalTextView: TextView = view.findViewById(R.id.textViewOrderTotal)
        val orderStatusTextView: TextView = view.findViewById(R.id.textViewOrderStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]

        holder.orderIdTextView.text = "Pedido ID: #${order.orderId.substring(4, 10)}"
        holder.orderTotalTextView.text = "Total: $${String.format("%.2f", order.totalPrice)}"
        holder.orderStatusTextView.text = "Estado: ${order.status}"

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val date = Date(order.timestamp)
        holder.orderDateTextView.text = "Fecha: ${sdf.format(date)}"

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context

            val intent = Intent(context, PedidoDetalleActivity::class.java).apply {
                putExtra("ORDER_DETAILS", order)
            }

            context.startActivity(intent)
        }
    }

    override fun getItemCount() = orders.size
}
