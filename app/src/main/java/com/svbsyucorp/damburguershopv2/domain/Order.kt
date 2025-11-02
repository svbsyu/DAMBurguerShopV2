package com.svbsyucorp.damburguershopv2.domain

import java.io.Serializable

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val timestamp: Long = 0,
    val totalPrice: Double = 0.0,
    val status: String = "",
    val items: List<HashMap<String, Any>> = emptyList()
) : Serializable
