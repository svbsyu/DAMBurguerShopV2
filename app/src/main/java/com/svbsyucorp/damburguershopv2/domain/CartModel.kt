package com.svbsyucorp.damburguershopv2.domain

data class CartItem(
    val item: ItemModel,
    var quantity: Int = 1
)

object CartManager {
    private val cartItems = mutableListOf<CartItem>()
    
    fun addItem(item: ItemModel) {
        val existingItem = cartItems.find { it.item.title == item.title }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            cartItems.add(CartItem(item))
        }
    }
    
    fun removeItem(item: ItemModel) {
        cartItems.removeAll { it.item.title == item.title }
    }
    
    fun getItems(): List<CartItem> = cartItems.toList()
    
    fun getTotalPrice(): Double = cartItems.sumOf { it.item.price * it.quantity }
    
    fun clear() = cartItems.clear()
}